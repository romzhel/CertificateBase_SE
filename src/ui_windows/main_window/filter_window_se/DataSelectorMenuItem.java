package ui_windows.main_window.filter_window_se;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.RadioMenuItem;
import ui_windows.main_window.MainTable;
import ui_windows.product.Product;

import java.util.Collection;

import static core.SharedData.SHD_DATA_SET;
import static core.SharedData.SHD_FILTER_PARAMETERS;

public class DataSelectorMenuItem extends RadioMenuItem {
    private ItemsSelection itemsSelection;
    private SyncDataSource syncDataSource;
    private SyncContextMenu syncContextMenu;

    public DataSelectorMenuItem(String text, ItemsSelection itemsSelection, SyncDataSource syncDataSource,
                                SyncContextMenu syncContextMenu) {
        super(text);
        this.itemsSelection = itemsSelection;
        this.syncDataSource = syncDataSource;
        this.syncContextMenu = syncContextMenu;
    }

    public SyncDataSource getSyncDataSource() {
        return syncDataSource;
    }

    public SyncContextMenu getSyncContextMenu() {
        return syncContextMenu;
    }

    public void activate() {
        ((FilterParameters_SE) SHD_FILTER_PARAMETERS.getData()).setItems(itemsSelection);
        SHD_DATA_SET.setData(this.getClass(), syncDataSource.syncData());
        MainTable.setContextMenu(syncContextMenu.getContextMenu());

        setSelected(true);
    }

    public interface FilterParamChanger {
        void changeParam();
    }

    public interface SyncDataSource {
        Collection<Product> syncData();
    }

    public interface SyncContextMenu {
        ContextMenu getContextMenu();
    }
}
