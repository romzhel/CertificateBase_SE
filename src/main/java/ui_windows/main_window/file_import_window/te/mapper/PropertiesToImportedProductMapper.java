package ui_windows.main_window.file_import_window.te.mapper;

import lombok.extern.log4j.Log4j2;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.main_window.file_import_window.te.importer.ImportedProperty;
import ui_windows.options_window.families_editor.ProductFamilies;
import ui_windows.product.Products;
import ui_windows.product.data.DataItem;
import ui_windows.product.vendors.VendorEnum;

import java.util.Map;

import static ui_windows.product.data.DataItem.*;

@Log4j2
public class PropertiesToImportedProductMapper {

    public ImportedProduct importedProductMapper(Map<DataItem, ImportedProperty> propertyMap) {
        if (propertyMap.get(DATA_ORDER_NUMBER) == null || propertyMap.values().size() < 2) {
            return null;
        }

        ImportedProduct importedProduct = new ImportedProduct();
        importedProduct.setProperties(propertyMap);

        String material = propertyMap.get(DATA_ORDER_NUMBER).getNewValue().toString();

        VendorEnum vendor;
        if (propertyMap.containsKey(DATA_VENDOR)) {
            String fileImportedVendorRaw = propertyMap.get(DATA_VENDOR).getNewValue().toString();
            vendor = VendorEnum.recognizeVendor(fileImportedVendorRaw);
        } else if (propertyMap.containsKey(DATA_LGBK) &&
                ProductFamilies.getInstance().isSpGbkName(propertyMap.get(DATA_LGBK).getNewValue().toString())) {
            vendor = VendorEnum.VANDERBILT;
        } else if (propertyMap.containsKey(DATA_HIERARCHY) &&
                ProductFamilies.getInstance().isSpHierarchyName(propertyMap.get(DATA_HIERARCHY).getNewValue().toString())) {
            vendor = VendorEnum.VANDERBILT;
        } else {
            vendor = propertyMap.get(DATA_ORDER_NUMBER).getSource().getVendor();
        }

        String vendorMaterialId = Products.getInstance().getVendorMaterial(vendor, material);
        importedProduct.setId(vendorMaterialId);
        importedProduct.setVendor(vendor);

        return importedProduct;
    }
}
