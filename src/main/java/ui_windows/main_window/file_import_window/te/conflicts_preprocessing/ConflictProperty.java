package ui_windows.main_window.file_import_window.te.conflicts_preprocessing;

import lombok.Data;
import ui_windows.main_window.file_import_window.te.importer.ImportedProperty;

@Data
public class ConflictProperty {
    private ImportedProperty property;
    private boolean selected = false;

    public ConflictProperty(ImportedProperty property) {
        this.property = property;
    }
}
