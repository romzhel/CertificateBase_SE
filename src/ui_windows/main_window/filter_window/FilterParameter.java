package ui_windows.main_window.filter_window;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;

public class FilterParameter {
    private Control control;
    private Object value;

    public FilterParameter(Object defaultValue) {
        control = null;
        value = defaultValue;
    }

    public void displayValue(Control control) {
        this.control = control;
        if (control instanceof CheckBox && value instanceof Boolean) {
            ((CheckBox) control).setSelected((Boolean) value);
        } else if (control instanceof ComboBox && value instanceof ProductFamily) {
            ((ComboBox) control).setValue((ProductFamily) value);
        } else if (control instanceof ComboBox && value instanceof ProductLgbk) {
            ((ComboBox) control).setValue((ProductLgbk) value);
        } else {
            System.out.println("unknown filter control " + control.getId());
        }
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
