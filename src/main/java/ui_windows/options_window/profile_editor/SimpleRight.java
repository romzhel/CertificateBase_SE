package ui_windows.options_window.profile_editor;

import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public enum SimpleRight {
    HIDE("HIDE"), DISPLAY("DISPLAY"), OWN("OWN"), FULL("FULL");

    private String name;

    SimpleRight(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SimpleRight getSimpleRight(String name) {
        return valueOf(name);
    }

    public void apply(ContextMenu menu) {
        final String ADD = "add";
        final String EDIT = "edit";
        final String DELETE = "delete";
        final String OTHER = "other";
        for (MenuItem mi : menu.getItems()) {
            if (mi.getId() != null && mi.getId().toLowerCase().contains(ADD)) mi.setDisable(this == HIDE || this == DISPLAY);
            if (mi.getId() != null && mi.getId().toLowerCase().contains(EDIT)) mi.setDisable(this == HIDE);
            if (mi.getId() != null && mi.getId().toLowerCase().contains(DELETE)) mi.setDisable(this == HIDE || this == DISPLAY);
            if (mi.getId() != null && mi.getId().toLowerCase().contains(OTHER)) mi.setDisable(this == HIDE || this == DISPLAY);
        }
    }

    public void apply(Button button) {
        final String APPLY = "apply";
        final String OTHER = "other";
        if (button.getId() != null && button.getId().toLowerCase().contains(APPLY)) button.setDisable(this == HIDE || this == DISPLAY);
    }
}
