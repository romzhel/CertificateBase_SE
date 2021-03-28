package ui_windows.main_window.file_import_window.te.mapper;

import ui_windows.main_window.file_import_window.te.ImportColumnParameter;
import ui_windows.product.Product;

import java.util.List;

public interface FileRecordToProductMapper<T> {
    Product getProductFromFileRecord(T record, List<ImportColumnParameter> params);
}
