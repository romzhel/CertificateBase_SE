package ui_windows.product;

import ui_windows.product.data.DataItem;
import utils.comparation.te.PropertyProtectEnum;

import java.util.List;

import static utils.comparation.te.PropertyProtectEnum.*;

public class MultiEditorItem {
    public static final boolean CAN_BE_SAVED = true;
    public static final boolean CAN_NOT_BE_SAVED = false;
    private DataItem dataItem;
    //    private Control control;
    private boolean canBeSaved;
    private Object commonValue;
    private Object newValue;

    public MultiEditorItem(DataItem dataItem, /*Control control,*/ boolean canBeSaved) {
        this.dataItem = dataItem;
//        this.control = control;
        this.canBeSaved = canBeSaved;
        commonValue = null;
        newValue = null;
    }

    public DataItem getDataItem() {
        return dataItem;
    }

//    public Control getControl() {
//        return control;
//    }

    public boolean isCanBeSaved() {
        return canBeSaved;
    }

    /*public void setUiAccessibility() {
        control.setDisable(!(commonValue != null && canBeSaved));
        *//*if (control instanceof CheckBox) {
            ((CheckBox) control).setSelected(commonValue != null && (boolean) commonValue);
            ((CheckBox) control).setIndeterminate(commonValue == null);
        } else if (control instanceof TextInputControl) {
            ((TextInputControl) control).setText(commonValue != null ? (String) commonValue : "Разные значения");
        }*//*

    }*/

    /*public boolean isValueChanged() {
        if (control instanceof CheckBox) {
            if (((CheckBox) control).isSelected() != (boolean) commonValue) {
                newValue = ((CheckBox) control).isSelected();
                return true;
            }
        } else if (control instanceof TextInputControl) {
            if (commonValue != null && !((TextInputControl) control).getText().equals((String) commonValue)) {
                newValue = ((TextInputControl) control).getText();
                return true;
            }
        }
        return false;
    }*/

    public PropertyProtectEnum compare(List<Product> items) {
        Object tempValue = null;
        boolean compRes = true;
        int propertyProtectCount = 0;
        for (Product product : items) {
            Object value = dataItem.getValue(product);
            if (tempValue == null) {
                tempValue = value;
            } else {
                compRes &= tempValue.equals(value);
            }

            if (product.getProtectedData().contains(dataItem)) {
                propertyProtectCount++;
            }
        }

        commonValue = compRes ? dataItem.getValue(items.get(0)) : null;

        return propertyProtectCount == items.size() ? PROTECTED :
                propertyProtectCount == 0 ? NON_PROTECTED : COMBINED;
    }

    public Object getCommonValue() {
        return commonValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    @Override
    public String toString() {
        return "MultiEditorItem{" +
                "dataItem=" + dataItem +
                ", canBeSaved=" + canBeSaved +
                ", commonValue=" + commonValue +
                ", newValue=" + newValue +
                '}';
    }
}
