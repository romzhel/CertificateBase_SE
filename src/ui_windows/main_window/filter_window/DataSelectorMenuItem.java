package ui_windows.main_window.filter_window;

import javafx.scene.control.RadioMenuItem;
import ui_windows.product.Product;

import java.util.ArrayList;

public class DataSelectorMenuItem extends RadioMenuItem {
    private FilterParameters filterParameters;
    private SyncDataSource syncDataSource;

    public DataSelectorMenuItem(String text, FilterParameters filterParameters, SyncDataSource syncDataSource) {
        super(text);
        this.filterParameters = filterParameters;
        this.syncDataSource = syncDataSource;
    }

    public FilterParameters getFilterParameters() {
        return filterParameters;
    }

    public SyncDataSource getSyncDataSource() {
        return syncDataSource;
    }

    public interface SyncDataSource {
        ArrayList<Product> syncData();
    }
}
