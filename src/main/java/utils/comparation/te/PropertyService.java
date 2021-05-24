package utils.comparation.te;

import ui_windows.main_window.file_import_window.te.importer.ImportedProperty;
import ui_windows.product.Product;

import static ui_windows.main_window.file_import_window.te.FilesImportParametersEnum.BLOCK_PROPERTY;

public class PropertyService {


    public boolean isPropertyNeedToBlocked(ImportedProperty property) {
        return property.getParams().getOrDefault(BLOCK_PROPERTY, false);
    }

    public boolean isPropertyBlocked(Product product, ImportedProperty property) {
        return product.getProtectedData().contains(property.getDataItem());
    }
}
