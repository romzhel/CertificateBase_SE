package utils.comparation.te;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.main_window.file_import_window.te.importer.ImportedProperty;
import ui_windows.product.Product;
import utils.comparation.se.ComparingRules;

import java.util.HashSet;
import java.util.Set;

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
            changedProperty.setOldValue(existValue != null ? existValue : "");

            if (existItem.getProtectedData().contains(property.getDataItem())) {
                result.getProtectedField().add(changedProperty);
                logger.info("property {} {} will not changed due protect", existItem.toString(), property.getDataItem());
                continue;
            }

            if (!rules.isCanBeSkipped_v2(changedProperty)) {
                result.getChangedPropertyList().add(changedProperty);
            }
        }

        return result;
    }

    public ChangedItem compare(ImportedProduct item1, ImportedProduct item2) throws RuntimeException {
        ChangedItem result = new ChangedItem();
        result.setId(item1.getId());

        Set<ImportedProperty> properties2set = new HashSet<>(item2.getProperties().values());

        for (ImportedProperty property1 : item1.getProperties().values()) {
            ImportedProperty property2 = item2.getProperties().get(property1.getDataItem());

            if (property2 == null) {
                continue;
            }

            properties2set.remove(property2);

            Object value1 = property1.getNewValue();
            Object value2 = property2.getNewValue();

            if (value1.equals(value2)) {
                continue;
            }

            try {
                addChangedProperty(result, property2, value1);
            } catch (Exception e) {
                logger.error("error of ChangedProperty creation from ImportedProperty {} for {}, item1={}, item2={}",
                        property2, result.getId(), item1, item2);
            }
        }

        for (ImportedProperty newProperty : properties2set) {
            addChangedProperty(result, newProperty, null);
        }

        return result;
    }

    private void addChangedProperty(ChangedItem result, ImportedProperty property2, Object value1) throws RuntimeException {
        ChangedProperty changedProperty = new ChangedProperty(property2);
        changedProperty.setOldValue(value1);
        result.getChangedPropertyList().add(changedProperty);
    }
}
