package ui_windows.main_window.file_import_window.te.importer;

import lombok.Getter;

import java.io.File;
import java.util.List;
import java.util.Map;

@Getter
public abstract class AbstractFileImporter implements FileImporter {
    protected File file;
    protected Map<String, List<String>> sheetSet;
    protected Map<String, Integer> columnIndexMap;
}
