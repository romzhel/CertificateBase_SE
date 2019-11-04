package ui_windows.main_window.filter_window;

import javafx.scene.control.*;

public class FilterParameter<T> {
    private Control control;
    private T value;

    public FilterParameter(T defaultValue) {
        control = null;
        value = defaultValue;
    }

    public void displayValue(Control control) {
        if (control == null) return;
        this.control = control;
        if (control instanceof RadioButton && value instanceof Boolean) {
            ((RadioButton) control).setSelected((Boolean) value);
        } else if (control instanceof ComboBox) {
            ((ComboBox) control).setValue(value);
        } else if (control instanceof TextField) {
            ((TextField) control).setText((String) value);
            ((TextField) control).selectRange(((String) value).length(), ((String) value).length());
        } else {
            System.out.println("unknown filter control " + control.getId());
        }
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
        displayValue(control);
    }
}
