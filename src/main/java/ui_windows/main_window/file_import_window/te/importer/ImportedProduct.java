package ui_windows.main_window.file_import_window.te.importer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ui_windows.product.Product;

@Data
@EqualsAndHashCode(callSuper = true)
public class ImportedProduct extends Product {
    private ImportDataSheet importDataSheet;
}
