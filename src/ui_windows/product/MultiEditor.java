package ui_windows.product;

import core.CoreModule;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ui_windows.product.productEditorWindow.ProductEditorWindow;
import ui_windows.product.productEditorWindow.ProductEditorWindowController;
import utils.Countries;
import utils.comparation.ObjectsComparator;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MultiEditor {
    private ObservableList<Product> editedItems;
    private ArrayList<FieldsAndControls> fieldsAndControls;

    public MultiEditor(ObservableList<Product> editedItems) {
        this.editedItems = editedItems;
        ProductEditorWindowController pewc = ProductEditorWindow.getLoader().getController();
        fieldsAndControls = new ArrayList<>();

//        compare("price", pewc.cbxPrice);
//        compare("notused", pewc.cbxNotUsed);
//        compare("archive", pewc.cbxArchive);
//        compare("lgbk", pewc.tfLgbk);
//        compare("hierarchy", pewc.tfHierarchy);
//        compare("dchain", pewc.tfAccessibility);
//        compare("endofservice", pewc.tfEndOfService);
//        compare("country", pewc.tfCountry);
//        compare("comments", pewc.taComments);
        compare("type_id", pewc.cbType);

    }

    public boolean compare(String fieldName, Node control) {
        ObjectsComparator objectsComparator = new ObjectsComparator();

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

        fieldsAndControls.add(new FieldsAndControls(field, control, compRes));

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
                        ((ComboBox<String>) control).getEditor().setText(CoreModule.getProductTypes().getTypeById((int) tempValue));
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
                if (fac.isCanBeSaved()) {
                    for (Product product : editedItems) {
                        if (fac.getField().getType().getName().toLowerCase().contains("boolean")) {
                            fac.getField().set(product, new SimpleBooleanProperty(((CheckBox) fac.getControl()).isSelected()));
                        } else if (fac.getField().getType().getName().toLowerCase().contains("String")) {
                            if (fac.getControl() instanceof TextField) {
                                fac.getField().set(product, ((TextField) fac.getControl()).getText());
                            } else if (fac.getControl() instanceof TextArea) {
                                fac.getField().set(product, ((TextArea) fac.getControl()).getText());
                            }
                        } else if (fac.getField().getType().getName().toLowerCase().contains("int")) {
                            if (fac.getControl() instanceof ComboBox) {
                                fac.getField().set(product, CoreModule.getProductTypes().getIDbyType(
                                        ((ComboBox) fac.getControl()).getEditor().getText()));
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

    private class FieldsAndControls {
        private Field field;
        private Node control;
        private boolean canBeSaved;

        public FieldsAndControls(Field field, Node control, boolean canBeSaved) {
            this.field = field;
            this.control = control;
            this.canBeSaved = canBeSaved;
        }

        public Field getField() {
            return field;
        }

        public Node getControl() {
            return control;
        }

        public boolean isCanBeSaved() {
            return canBeSaved;
        }
    }
}
