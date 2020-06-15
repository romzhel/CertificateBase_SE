package ui_windows.main_window;

import core.CoreModule;
import javafx.scene.control.*;
import ui_windows.main_window.filter_window_se.DataSelectorMenuItem;
import ui_windows.product.Products;

import static ui_windows.main_window.filter_window_se.ItemsSelection.ALL_ITEMS;
import static ui_windows.main_window.filter_window_se.ItemsSelection.PRICE_ITEMS;

public class DataSelectorMenu {
    private static final String SPACE = "     ";
    public static final DataSelectorMenuItem MENU_DATA_CUSTOM_SELECTION = new DataSelectorMenuItem(
            SPACE + "Запрос" + SPACE, ALL_ITEMS, CoreModule::getCustomItems,
            MainTableContextMenuFactory::createContectMenuForCustomItems);
    public static final DataSelectorMenuItem MENU_DATA_LAST_IMPORT_RESULT = new DataSelectorMenuItem(
            SPACE + "Результаты последнего импорта" + SPACE, ALL_ITEMS,
            Products.getInstance()::getChangedPositions, MainTableContextMenuFactory::createContextMenuForAllData);
    private static final DataSelectorMenuItem MENU_DATA_ALL_ITEMS = new DataSelectorMenuItem(
            SPACE + "Все позиции" + SPACE, PRICE_ITEMS,
            Products.getInstance()::getItems, MainTableContextMenuFactory::createContextMenuForAllData);

    public DataSelectorMenu(Menu menu) {
        init(menu);
    }

    private void init(Menu menu) {
        menu.getItems().addAll(
                MENU_DATA_ALL_ITEMS,
                new SeparatorMenuItem(),
                MENU_DATA_CUSTOM_SELECTION,
                new SeparatorMenuItem(),
                MENU_DATA_LAST_IMPORT_RESULT);

        ToggleGroup dataSelector = new ToggleGroup();
        for (MenuItem mi : menu.getItems()) {
            if (mi instanceof RadioMenuItem) {
                ((RadioMenuItem) mi).setToggleGroup(dataSelector);
            }
        }

        dataSelector.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {//
            DataSelectorMenuItem newItem = (DataSelectorMenuItem) newValue;
            if (newItem != null) {
                newItem.activate();
                menu.setText("Данные: " + newItem.getText().trim().toLowerCase());
            }
        });

        MENU_DATA_ALL_ITEMS.setSelected(true);
    }

    public void selectMenuItem(RadioMenuItem radioMenuItem) {
        ((DataSelectorMenuItem) radioMenuItem).activate();
    }
}
