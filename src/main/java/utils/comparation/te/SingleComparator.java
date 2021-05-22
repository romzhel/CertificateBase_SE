package utils.comparation.te;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.main_window.file_import_window.te.importer.ImportedProperty;
import ui_windows.product.Product;
import utils.comparation.se.ComparingRules;

public class SingleComparator {
    private static final Logger logger = LogManager.getLogger(SingleComparator.class);

    public ChangedItem compare(Product existItem, ImportedProduct importedItem, ComparingRules<Product> rules) throws RuntimeException {
        ChangedItem result = new ChangedItem();
        result.setId(importedItem.getId());

        for (ImportedProperty property : importedItem.getProperties().values()) {
            Object existValue = property.getDataItem().getValue(existItem);
            Object newValue = property.getNewValue();

            if (newValue.equals(existValue)) {
                continue;
            }

            ChangedProperty changedProperty = new ChangedProperty(property);
            changedProperty.setOldValue(existValue);

            if (!rules.isCanBeSkipped_v2(changedProperty)) {
                result.getChangedPropertyList().add(changedProperty);
            }
        }

        return result;
    }
}
