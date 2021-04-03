package ui_windows.main_window.file_import_window.te.importer;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;

@Getter
public abstract class AbstractFileImporter implements FileImporter {
    protected static final Logger logger = LogManager.getLogger(AbstractFileImporter.class);
    protected File file;
    protected Map<String, List<String>> sheetSet;
    protected Map<String, Integer> columnIndexMap;
}
