package utils.comparation.te;

import lombok.extern.log4j.Log4j2;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.main_window.file_import_window.te.importer.ImportedProperty;
import ui_windows.product.Product;

import java.lang.reflect.Field;

@Log4j2
public class ImportedProductToProductMapper {

    public Product mapToProduct(ImportedProduct importedProduct) {
        Product result = new Product();

        for (ImportedProperty property : importedProduct.getProperties().values()) {
            Field field = property.getDataItem().getField();
            field.setAccessible(true);

            try {
                field.set(result, property.getNewValue());
            } catch (IllegalAccessException e) {
                log.error("reflection error with product id={} field={}", importedProduct.getId(), property);
            }
        }

        return result;
    }
}
