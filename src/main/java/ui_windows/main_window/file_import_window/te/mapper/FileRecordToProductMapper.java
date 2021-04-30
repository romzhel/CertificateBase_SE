package ui_windows.main_window.file_import_window.te.mapper;

import ui_windows.main_window.file_import_window.te.importer.ImportDataSheet;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;

public interface FileRecordToProductMapper<T> {
    ImportedProduct getProductFromFileRecord(T record, ImportDataSheet importDataSheet);
}
