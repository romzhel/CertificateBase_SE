package ui_windows.main_window.filter_window;

import core.CoreModule;
import core.SharedData;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.RadioMenuItem;
import ui_windows.main_window.MainTable;
import ui_windows.main_window.filter_window_se.FilterParameters_SE;
import ui_windows.product.Product;

import java.util.ArrayList;

import static core.SharedData.*;

public class DataSelectorMenuItem extends RadioMenuItem {
    private FilterParameters_SE filterParameters;
    private SyncDataSource syncDataSource;
    private SyncContextMenu syncContextMenu;

    public DataSelectorMenuItem(String text, FilterParameters_SE filterParameters, SyncDataSource syncDataSource,
                                SyncContextMenu syncContextMenu) {
        super(text);
        this.filterParameters = filterParameters;
        this.syncDataSource = syncDataSource;
        this.syncContextMenu = syncContextMenu;
    }

    public FilterParameters_SE getFilterParameters() {
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
//        CoreModule.setCurrentItems(syncDataSource.syncData());
        SHD_FILTER_PARAMETERS.setData(filterParameters);
        SHD_DATA_SET.setData(syncDataSource.syncData());
        MainTable.setContextMenu(syncContextMenu.getContextMenu());
//        CoreModule.getFilter().switchFilterParameters(filterParameters);
//        setSelected(true);
    }
}
