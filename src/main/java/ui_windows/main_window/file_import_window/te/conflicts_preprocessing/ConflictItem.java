package ui_windows.main_window.file_import_window.te.conflicts_preprocessing;

import lombok.Data;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.product.data.DataItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ConflictItem {
    private ImportedProduct calculatedItem;
    private Map<DataItem, List<ConflictItemValue>> conflictValues = new HashMap<>();
}
