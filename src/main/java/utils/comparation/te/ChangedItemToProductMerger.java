package utils.comparation.te;

import lombok.extern.log4j.Log4j2;
import ui_windows.product.Product;

import java.lang.reflect.Field;

@Log4j2
public class ChangedItemToProductMerger {


    public Product mergeToProduct(Product product, ChangedItem changedItem) throws RuntimeException {
        for (ChangedProperty property : changedItem.getChangedPropertyList()) {
            Field field = property.getDataItem().getField();
            field.setAccessible(true);

            try {
                field.set(product, property.getNewValue());

            } catch (IllegalAccessException e) {
                log.error("reflection error with product id={} field={}", product.getMaterial(), property);
            }
        }

        return product;
    }
}
