package ui_windows.main_window.file_import_window.te.conflicts_preprocessing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.main_window.file_import_window.te.importer.ImportedProperty;
import ui_windows.product.data.DataItem;

import java.util.*;

public class ConflictItemService {
    private static final Logger logger = LogManager.getLogger(ConflictItemService.class);

    public ConflictItem merge(ImportedProduct item, ConflictItem conflictItem) {
        Set<ImportedProperty> properties1 = new HashSet<>(item.getProperties().values());

        for (Map.Entry<DataItem, List<ConflictProperty>> conflictEntry : conflictItem.getConflictPropertyMap().entrySet()) {
            DataItem dataItem = conflictEntry.getKey();
            ImportedProperty importedProperty = item.getProperties().get(dataItem);

            if (importedProperty == null) {
                continue;
            }

            properties1.remove(importedProperty);

            boolean valueIsNotPresent = conflictEntry.getValue().stream()
                    .noneMatch(conflictProperty -> conflictProperty.getProperty().equals(importedProperty));
            if (valueIsNotPresent) {
                List<ConflictProperty> list = new LinkedList<>(conflictEntry.getValue());
                list.add(new ConflictProperty(importedProperty));
                conflictEntry.setValue(list);
            }
        }

        for (ImportedProperty property2 : conflictItem.getPropertyMap().values()) {
            ImportedProperty property1 = item.getProperties().get(property2.getDataItem());
            DataItem dataItem = property2.getDataItem();
            properties1.remove(property1);
            if (property1 != null && !property1.equals(property2)) {
                List<ConflictProperty> conflictPropertyList = conflictItem.getConflictPropertyMap().getOrDefault(dataItem, new LinkedList<>());
                conflictPropertyList.add(new ConflictProperty(property1));
            } else {
                conflictItem.getPropertyMap().put(dataItem, property1);
            }
        }

        for (ImportedProperty property : properties1) {
            conflictItem.getPropertyMap().put(property.getDataItem(), property);
        }

        return conflictItem;
    }

    public ConflictItem merge(ImportedProduct item1, ImportedProduct item2) {
        ConflictItem conflictItem = new ConflictItem();
        conflictItem.setId(item1.getId());
        Set<ImportedProperty> properties2 = new HashSet<>(item2.getProperties().values());

        for (ImportedProperty property1 : item1.getProperties().values()) {
            ImportedProperty property2 = item2.getProperties().get(property1.getDataItem());
            DataItem dataItem = property1.getDataItem();
            properties2.remove(property2);
            if (property2 != null && !property1.equals(property2)) {
                List<ConflictProperty> conflictPropertyList = conflictItem.getConflictPropertyMap().getOrDefault(dataItem, new LinkedList<>());
                conflictPropertyList.add(new ConflictProperty(property1));
                conflictPropertyList.add(new ConflictProperty(property2));
                conflictItem.getConflictPropertyMap().put(dataItem, conflictPropertyList);
            } else {
                conflictItem.getPropertyMap().put(dataItem, property1);
            }
        }

        for (ImportedProperty property : properties2) {
            conflictItem.getPropertyMap().put(property.getDataItem(), property);
        }

        return conflictItem;
    }
}
