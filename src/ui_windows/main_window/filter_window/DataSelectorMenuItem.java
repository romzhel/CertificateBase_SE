package ui_windows.main_window.filter_window;

import core.CoreModule;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.RadioMenuItem;
import ui_windows.main_window.MainTable;
import ui_windows.product.Product;

import java.util.ArrayList;

public class DataSelectorMenuItem extends RadioMenuItem {
    private FilterParameters filterParameters;
    private SyncDataSource syncDataSource;
    private SyncContextMenu syncContextMenu;

    public DataSelectorMenuItem(String text, FilterParameters filterParameters, SyncDataSource syncDataSource,
                                SyncContextMenu syncContextMenu) {
        super(text);
        this.filterParameters = filterParameters;
        this.syncDataSource = syncDataSource;
        this.syncContextMenu = syncContextMenu;
    }

    public FilterParameters getFilterParameters() {
        return filterParameters;
    }

    public SyncDataSource getSyncDataSource() {
        return syncDataSource;
    }

    public SyncContextMenu getSyncContextMenu() {
        return syncContextMenu;
    }

    public interface SyncDataSource {
        ArrayList<Product> syncData();
    }

    public interface SyncContextMenu {
        ContextMenu getContextMenu();
    }

    public void activate() {
        CoreModule.setCurrentItems(syncDataSource.syncData());
        MainTable.setContextMenu(syncContextMenu.getContextMenu());
        CoreModule.getFilter().switchFilterParameters(filterParameters);
        setSelected(true);
    }
}
