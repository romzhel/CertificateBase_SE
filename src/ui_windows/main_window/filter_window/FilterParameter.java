package ui_windows.main_window.filter_window;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;

public class FilterParameter<T> {
    private Control control;
    private T value;

    public FilterParameter(T defaultValue) {
        control = null;
        value = defaultValue;
    }

    public void displayValue(Control control) {
        this.control = control;
        if (control instanceof CheckBox && value instanceof Boolean) {
            ((CheckBox) control).setSelected((Boolean) value);
        } else if (control instanceof ComboBox && value instanceof ProductFamily) {
            ((ComboBox) control).setValue(value);
        } else if (control instanceof ComboBox && value instanceof ProductLgbk) {
            ((ComboBox) control).setValue(value);
        } else {
            System.out.println("unknown filter control " + control.getId());
        }
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
