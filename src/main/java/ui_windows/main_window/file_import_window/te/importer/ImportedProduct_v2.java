package ui_windows.main_window.file_import_window.te.importer;

import lombok.Getter;
import ui_windows.product.data.DataItem;

import java.util.Map;

@Getter
public class ImportedProduct_v2 {
    private final Map<DataItem, ImportedProperty> properties;

    public ImportedProduct_v2(Map<DataItem, ImportedProperty> properties) {
        this.properties = properties;
    }
}
