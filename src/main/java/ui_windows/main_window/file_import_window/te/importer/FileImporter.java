package ui_windows.main_window.file_import_window.te.importer;

import ui_windows.main_window.file_import_window.te.ImportColumnParameter;
import ui_windows.product.Product;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FileImporter {
    Map<String, List<String>> getSheetSet();

    Map<String, Integer> getColumnIndexMap();

    Set<Product> getProducts(String sheetName, List<ImportColumnParameter> params);

    void openFile(File file);

    void closeFile();
}
