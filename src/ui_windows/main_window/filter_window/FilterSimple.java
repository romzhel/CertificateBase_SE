package ui_windows.main_window.filter_window;

import ui_windows.main_window.Product;

public class FilterSimple {
    private String description;
    private String uiname;
    private String fname;
    private boolean value;

    public FilterSimple(String description, String uiname, String fname, boolean value) {
        this.description = description;
        this.uiname = uiname;
        this.fname = fname;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUiname() {
        return uiname;
    }

    public void setUiname(String uiname) {
        this.uiname = uiname;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
