package ui_windows.product;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextInputControl;
import ui_windows.product.data.DataItemEnum;

import java.util.List;

public class MultiEditorItem {
    public static final boolean CAN_BE_SAVED = true;
    public static final boolean CAN_NOT_BE_SAVED = false;
    private DataItemEnum dataItem;
    private Control control;
    private boolean canBeSaved;
    private Object commonValue;
    private Object newValue;

    public MultiEditorItem(DataItemEnum dataItem, Control control, boolean canBeSaved) {
        this.dataItem = dataItem;
        this.control = control;
        this.canBeSaved = canBeSaved;
        commonValue = null;
        newValue = null;
    }

    public DataItemEnum getDataItem() {
        return dataItem;
    }

    public Control getControl() {
        return control;
    }

    public boolean isCanBeSaved() {
        return canBeSaved;
    }

    public void display() {
        control.setDisable(!(commonValue != null && canBeSaved));

        if (control instanceof CheckBox) {
            ((CheckBox) control).setSelected(commonValue != null && (boolean) commonValue);
            ((CheckBox) control).setIndeterminate(commonValue == null);
        } else if (control instanceof TextInputControl) {
            ((TextInputControl) control).setText(commonValue != null ? (String) commonValue : "Разные значения");
        }
    }

    public boolean isValueChanged() {
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
    }

    public boolean compare(List<Product> items) {
        Object tempValue = null;
        boolean compRes = true;
        for (Product product : items) {
            Object value = dataItem.getValue(product);
            if (tempValue == null) {
                tempValue = value;
            } else {
                compRes &= tempValue.equals(value);
            }
        }
        if (compRes) commonValue = dataItem.getValue(items.get(0));
        return compRes;
    }

    public Object getCommonValue() {
        return commonValue;
    }

    public Object getNewValue() {
        return newValue;
    }
}
