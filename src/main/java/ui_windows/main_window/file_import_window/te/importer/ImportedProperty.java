package ui_windows.main_window.file_import_window.te.importer;

import lombok.Data;
import ui_windows.product.data.DataItem;

@Data
public class ImportedProperty {
    private Object value;
    private DataItem dataItem;
    private ImportDataSheet source;
}
