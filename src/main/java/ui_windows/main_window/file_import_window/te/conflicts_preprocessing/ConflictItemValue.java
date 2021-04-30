package ui_windows.main_window.file_import_window.te.conflicts_preprocessing;

import lombok.Data;
import ui_windows.main_window.file_import_window.te.importer.ImportDataSheet;

@Data
public class ConflictItemValue {
    private Object value;
    private ImportDataSheet source;
    private boolean selected;
}
