package ui_windows.main_window;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;

import static ui_windows.main_window.MainTable.*;

public class MainTableContextMenuCustom extends ContextMenu {

    public MainTableContextMenuCustom() {
        super(MENU_OPEN_ITEM,
                new SeparatorMenuItem(),
                MENU_DELETE_ITEM_FROM_LIST,
                MENU_DELETE_ALL_ITEMS,
                new SeparatorMenuItem(),
                MENU_CHECK_CERTIFICATES,
                new SeparatorMenuItem(),
                MENU_EXPORT);
    }
}
