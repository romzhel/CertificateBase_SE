package ui_windows.main_window;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;

import static ui_windows.main_window.MainTable.*;

public class MainTableContextMenuData extends ContextMenu {

    public MainTableContextMenuData() {
        super(MENU_OPEN_ITEM,
                new SeparatorMenuItem(),
                MENU_ADD_ITEM_TO_CUSTOM,
                new SeparatorMenuItem(),
                MENU_CHECK_CERTIFICATES,
                new SeparatorMenuItem(),
                MENU_EXPORT);
    }
}
