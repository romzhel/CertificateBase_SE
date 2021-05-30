package utils.comparation.te;

import ui_windows.main_window.file_import_window.te.importer.ImportedProperty;
import ui_windows.product.Product;

import static ui_windows.main_window.file_import_window.te.FilesImportParametersEnum.BLOCK_PROPERTY;

public class PropertyProtectService {


    public boolean isPropertyNeedToProtect(ImportedProperty property) {
        return property.getParams().getOrDefault(BLOCK_PROPERTY, false);
    }

    public boolean isPropertyProtect(Product product, ImportedProperty property) {
        return product.getProtectedData().contains(property.getDataItem());
    }
}
