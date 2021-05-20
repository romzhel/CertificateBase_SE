package ui_windows.main_window.file_import_window.te.conflicts_preprocessing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.main_window.file_import_window.te.importer.ImportedProperty;

import java.util.HashSet;
import java.util.Set;

public class ImportedItemService {
    private static final Logger logger = LogManager.getLogger(ImportedItemService.class);

    public ImportedProduct merge(ImportedProduct item1, ImportedProduct item2) throws RuntimeException {
        Set<ImportedProperty> properties2 = new HashSet<>(item2.getProperties().values());

        for (ImportedProperty property1 : item1.getProperties().values()) {
            ImportedProperty property2 = item2.getProperties().get(property1.getDataItem());
            properties2.remove(property2);
            if (property2 != null && !property1.equals(property2)) {
                return null;
            }
        }

        for (ImportedProperty property : properties2) {
            item1.getProperties().put(property.getDataItem(), property);
        }

        return item1;
    }
}
