package ui_windows.main_window.file_import_window.te.conflicts_preprocessing;

import lombok.Data;
import ui_windows.main_window.file_import_window.te.importer.ImportedProperty;
import ui_windows.product.data.DataItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ConflictItem {
    private String id;
    private Map<DataItem, ImportedProperty> propertyMap = new HashMap<>();
    private Map<DataItem, List<ConflictProperty>> conflictPropertyMap = new HashMap<>();
}
