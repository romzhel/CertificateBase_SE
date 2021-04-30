package ui_windows.main_window.file_import_window.te.importer;

import java.io.File;
import java.util.List;

public interface FileImporter {
    List<ImportedProduct> getProducts(List<File> files, boolean manualMode);
}
