package ui_windows.product;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ui_windows.product.data.DataItemEnum;
import ui_windows.product.productEditorWindow.ProductEditorWindowController;

import java.util.ArrayList;

import static ui_windows.product.data.DataItemEnum.*;

public class MultiEditor {
    public static final boolean CAN_BE_SAVED = true;
    public static final boolean CAN_NOT_BE_SAVED = false;
    private ObservableList<Product> editedItems;
    private ArrayList<FieldsAndControls> fieldsAndControls;

    public MultiEditor(ObservableList<Product> editedItems, ProductEditorWindowController pewc) {
        this.editedItems = editedItems;
        fieldsAndControls = new ArrayList<>();

        compare(DATA_IS_IN_PRICE, pewc.cbxPrice, CAN_BE_SAVED);
        compare(DATA_LGBK, pewc.tfLgbk, CAN_NOT_BE_SAVED);
        compare(DATA_HIERARCHY, pewc.tfHierarchy, CAN_NOT_BE_SAVED);
        compare(DATA_DCHAIN, pewc.tfAccessibility, CAN_NOT_BE_SAVED);//пишется в базу с расшифровкой, НЕ ВКЛЮЧАТЬ
        compare(DATA_SERVICE_END, pewc.tfEndOfService, CAN_NOT_BE_SAVED);
        compare(DATA_COUNTRY, pewc.tfCountry, CAN_NOT_BE_SAVED); //пишется в базу с расшифровкой, НЕ ВКЛЮЧАТЬ
        compare(DATA_COMMENT, pewc.taComments, CAN_BE_SAVED);
        compare(DATA_TYPE, pewc.cbType, CAN_BE_SAVED);
        compare(DATA_DESCRIPTION_EN, pewc.taDescription, CAN_BE_SAVED);
        compare(DATA_DESCRIPTION_RU, pewc.taDescriptionEn, CAN_BE_SAVED);
    }

    public boolean compare(DataItemEnum dataItem, Node control, boolean canBeSaved) {
        Object tempValue = null;
        boolean compRes = true;
        for (Product product : editedItems) {
            Object value = dataItem.getValue(product);
            if (tempValue == null) {
                tempValue = value;
            } else {
                compRes &= tempValue.equals(value);
            }
        }

        fieldsAndControls.add(new FieldsAndControls(dataItem, control, compRes, canBeSaved));

        if (compRes && canBeSaved) {
            
        }

        if (dataItem.getField().getType().getName().toLowerCase().contains("boolean")) {
            if (control instanceof CheckBox) {
                if (compRes) {
                    ((CheckBox) control).setSelected((boolean) tempValue);
                } else {
                    ((CheckBox) control).setIndeterminate(true);
                    control.setDisable(true);
                }
            }
        } else if (dataItem.getField().getType().getName().toLowerCase().contains("string")) {
            if (control instanceof TextField) {
                if (compRes) {
                   /* if (fieldName.equals("dchain"))
                        ((TextField) control).setText(editedItems.get(0).getOrderableStatus());
                    else if (fieldName.equals("country")) ((TextField) control).setText(
                            Countries.getCombinedName(editedItems.get(0).getCountry()));
                    else ((TextField) control).setText((String) tempValue);*/
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
        } else if (dataItem.getField().getType().getName().toLowerCase().contains("int")) {
            if (control instanceof ComboBox) {
                /*if (field.getName().equals("type_id")) {
                    if (compRes) {
//                        ((ComboBox<String>) control).getEditor().setText(CoreModule.getProductTypes().getTypeById((int) tempValue));
                        ((ComboBox<String>) control).setValue(CoreModule.getProductTypes().getTypeById((int) tempValue));
                    } else {
                        control.setDisable(true);
                    }
                }*/
            }
        }
        return compRes;
    }

    public void save() {
//        try {
        for (FieldsAndControls fac : fieldsAndControls) {
            if (fac.isAllTheSame() && fac.canBeSaved) {
                for (Product product : editedItems) {
                        /*if (fac.getField().getType().getName().toLowerCase().contains("boolean")) {
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
                        }*/
                }
            }
        }
//        } catch (IllegalAccessException e) {
//            System.out.println(e.getMessage());
//        }
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
//            if (fac.getField().getName().equals(fieldName)) return fac;
        }
        return null;
    }

    public class FieldsAndControls {
        //        private Field field;
        private DataItemEnum dataItem;
        private Node control;
        private boolean isAllTheSame;
        private boolean canBeSaved;

//        public FieldsAndControls(Field field, Node control, boolean allTheSame, boolean canBeSaved) {
//            this.field = field;
//            this.control = control;
//            this.isAllTheSame = allTheSame;
//            this.canBeSaved = canBeSaved;
//        }

        public FieldsAndControls(DataItemEnum dataItem, Node control, boolean isAllTheSame, boolean canBeSaved) {
            this.dataItem = dataItem;
            this.control = control;
            this.isAllTheSame = isAllTheSame;
            this.canBeSaved = canBeSaved;
        }

        public DataItemEnum getDataItem() {
            return dataItem;
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
