package ui_windows.main_window.file_import_window.te.importer;

import lombok.Getter;
import ui_windows.main_window.file_import_window.te.conflicts_preprocessing.ConflictItemsPreprocessor;

@Getter
public abstract class AbstractFileImporter implements FileImporter {
    protected ConflictItemsPreprocessor conflictItemsPreprocessor = new ConflictItemsPreprocessor();
}
