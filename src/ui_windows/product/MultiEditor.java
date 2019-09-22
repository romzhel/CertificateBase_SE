package ui_windows.product;

import core.CoreModule;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ui_windows.product.productEditorWindow.ProductEditorWindowController;
import utils.Countries;
import utils.comparation.ObjectsComparator;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MultiEditor {
    public static final boolean CAN_BE_SAVED = true;
    public static final boolean CAN_NOT_BE_SAVED = false;
    private ObjectsComparator objectsComparator;
    private ObservableList<Product> editedItems;
    private ArrayList<FieldsAndControls> fieldsAndControls;

    public MultiEditor(ObservableList<Product> editedItems, ProductEditorWindowController pewc) {
        this.editedItems = editedItems;
        objectsComparator = new ObjectsComparator();
        fieldsAndControls = new ArrayList<>();

        compare("price", pewc.cbxPrice, CAN_BE_SAVED);
        compare("notused", pewc.cbxNotUsed, CAN_BE_SAVED);
        compare("archive", pewc.cbxArchive, CAN_BE_SAVED);
        compare("lgbk", pewc.tfLgbk, CAN_NOT_BE_SAVED);
        compare("hierarchy", pewc.tfHierarchy, CAN_NOT_BE_SAVED);
        compare("dchain", pewc.tfAccessibility, CAN_NOT_BE_SAVED);//пишется в базу с расшифровкой, НЕ ВКЛЮЧАТЬ
        compare("endofservice", pewc.tfEndOfService, CAN_NOT_BE_SAVED);
        compare("country", pewc.tfCountry, CAN_NOT_BE_SAVED); //пишется в базу с расшифровкой, НЕ ВКЛЮЧАТЬ
        compare("comments", pewc.taComments, CAN_BE_SAVED);
        compare("type_id", pewc.cbType, CAN_BE_SAVED);
        compare("descriptionru", pewc.taDescription, CAN_BE_SAVED);
        compare("descriptionen", pewc.taDescriptionEn, CAN_BE_SAVED);
    }

    public boolean compare(String fieldName, Node control, boolean canBeSaved) {
        Field field = null;
        Object tempValue = null;
        boolean compRes = true;
        for (Product product : editedItems) {
            try {
                field = product.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);

                if (tempValue == null) {
                    tempValue = objectsComparator.getProperty(product, field);
                } else {
                    compRes &= tempValue.equals(objectsComparator.getProperty(product, field));
                }
            } catch (NoSuchFieldException e) {
                System.out.println(e.getMessage());
            }
        }

        fieldsAndControls.add(new FieldsAndControls(field, control, compRes, canBeSaved));

        if (field.getType().getName().toLowerCase().contains("boolean")) {
            if (control instanceof CheckBox) {
                if (compRes) {
                    ((CheckBox) control).setSelected((boolean) tempValue);
                } else {
                    ((CheckBox) control).setIndeterminate(true);
                    control.setDisable(true);
                }
            }
        } else if (field.getType().getName().toLowerCase().contains("string")) {
            if (control instanceof TextField) {
                if (compRes) {
                    if (fieldName.equals("dchain"))
                        ((TextField) control).setText(editedItems.get(0).getOrderableStatus());
                    else if (fieldName.equals("country")) ((TextField) control).setText(
                            Countries.getCombinedName(editedItems.get(0).getCountry()));
                    else ((TextField) control).setText((String) tempValue);
                } else {
                    control.setDisable(true);
                }
            } else if (control instanceof TextArea) {
                if (compRes) {
                    ((TextArea) control).setText((String) tempValue);
                } else {
                    control.setDisable(true);
                }
            }
        } else if (field.getType().getName().toLowerCase().contains("int")) {
            if (control instanceof ComboBox) {
                if (field.getName().equals("type_id")) {
                    if (compRes) {
//                        ((ComboBox<String>) control).getEditor().setText(CoreModule.getProductTypes().getTypeById((int) tempValue));
                        ((ComboBox<String>) control).setValue(CoreModule.getProductTypes().getTypeById((int) tempValue));
                    } else {
                        control.setDisable(true);
                    }
                }
            }
        }
        return compRes;
    }

    public void save() {
        try {
            for (FieldsAndControls fac : fieldsAndControls) {
                if (fac.isAllTheSame() && fac.canBeSaved) {
                    for (Product product : editedItems) {
                        if (fac.getField().getType().getName().toLowerCase().contains("boolean")) {
                            fac.getField().set(product, new SimpleBooleanProperty(((CheckBox) fac.getControl()).isSelected()));
                        } else if (fac.getField().getType().getName().toLowerCase().contains("string")) {
                            if (fac.getField().getType().getName().toLowerCase().contains("stringproperty")) {
                                if (fac.getControl() instanceof TextField) {
                                    fac.getField().set(product, new SimpleStringProperty(((TextField) fac.getControl()).getText()));
                                } else if (fac.getControl() instanceof TextArea) {
                                    fac.getField().set(product, new SimpleStringProperty(((TextArea) fac.getControl()).getText()));
                                }
                            } else {
                                if (fac.getControl() instanceof TextField) {
                                    fac.getField().set(product, ((TextField) fac.getControl()).getText());
                                } else if (fac.getControl() instanceof TextArea) {
                                    fac.getField().set(product, ((TextArea) fac.getControl()).getText());
                                }
                            }
                        } else if (fac.getField().getType().getName().toLowerCase().contains("int")) {
                            if (fac.getControl() instanceof ComboBox) {
                                fac.getField().set(product, CoreModule.getProductTypes().getIDbyType(
                                        ((ComboBox<String>) fac.getControl()).getValue()));
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    public ObservableList<Product> getEditedItems() {
        return editedItems;
    }

    public FieldsAndControls getFieldAndControl(Node node) {
        for (FieldsAndControls fac : fieldsAndControls) {
            if (fac.getControl() == node) return fac;
        }
        return null;
    }

    public FieldsAndControls getFieldAndControl(String fieldName) {
        for (FieldsAndControls fac : fieldsAndControls) {
            if (fac.getField().getName().equals(fieldName)) return fac;
        }
        return null;
    }

    public class FieldsAndControls {
        private Field field;
        private Node control;
        private boolean isAllTheSame;
        private boolean canBeSaved;

        public FieldsAndControls(Field field, Node control, boolean allTheSame, boolean canBeSaved) {
            this.field = field;
            this.control = control;
            this.isAllTheSame = allTheSame;
            this.canBeSaved = canBeSaved;
        }

        public Field getField() {
            return field;
        }

        public Node getControl() {
            return control;
        }

        public boolean isAllTheSame() {
            return isAllTheSame;
        }

        public boolean isCanBeSaved() {
            return canBeSaved;
        }
    }
}
