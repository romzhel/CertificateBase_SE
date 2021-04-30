package ui_windows.main_window.file_import_window.te.importer;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.main_window.file_import_window.te.conflicts_preprocessing.ConflictItemsPreprocessor;

@Getter
public abstract class AbstractFileImporter implements FileImporter {
    protected static final Logger logger = LogManager.getLogger(AbstractFileImporter.class);
    protected ConflictItemsPreprocessor conflictItemsPreprocessor = new ConflictItemsPreprocessor();
}
