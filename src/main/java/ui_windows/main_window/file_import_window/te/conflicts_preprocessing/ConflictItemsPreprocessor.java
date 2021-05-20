package ui_windows.main_window.file_import_window.te.conflicts_preprocessing;

import core.ThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import utils.comparation.products.ProductNameResolver;

import java.util.*;
import java.util.stream.Collectors;

public class ConflictItemsPreprocessor {
    private static final Logger logger = LogManager.getLogger(ConflictItemsPreprocessor.class);
    private Map<String, ImportedProduct> processedItems = new HashMap<>();
    private Map<String, ConflictItem> conflictItems = new HashMap<>();

    public void process(ImportedProduct importedItem) {
        ConflictItemService conflictItemService = new ConflictItemService();
        ImportedItemService importedItemService = new ImportedItemService();
        String resolvedMaterial = ProductNameResolver.resolve(importedItem.getId());

        if (conflictItems.containsKey(resolvedMaterial)) {
            ConflictItem mergedItem = conflictItemService.merge(importedItem, conflictItems.get(resolvedMaterial));
            conflictItems.put(resolvedMaterial, mergedItem);
            logger.debug("item '{}' was merged to '{}'", importedItem, mergedItem);
        } else if (processedItems.containsKey(resolvedMaterial)) {
            ImportedProduct existingItem = processedItems.get(resolvedMaterial);
            ImportedProduct mergedImportedItem = importedItemService.merge(existingItem, importedItem);
            if (mergedImportedItem != null) {
                processedItems.put(resolvedMaterial, mergedImportedItem);
                logger.debug("item '{}' was merged to '{}'", importedItem, mergedImportedItem);
            } else {
                processedItems.remove(resolvedMaterial);
                ConflictItem conflictItem = conflictItemService.merge(existingItem, importedItem);
                conflictItems.put(resolvedMaterial, conflictItem);
                logger.debug("item '{}' was merged to '{}'", importedItem, conflictItem);
            }
        } else {
            processedItems.put(resolvedMaterial, importedItem);
//            logger.debug("item '{}' was added to processed items", importedItem);
        }
    }

    public List<ImportedProduct> processConflictsAndGetItems() throws RuntimeException {
        List<ImportedProduct> result = new LinkedList<>(processedItems.values());
        List<ConflictItem> items = new ArrayList<>(conflictItems.values());

        if (items.size() > 0) {
            List<ConflictItem> confirmedConflictItems = ThreadManager.executeFxTaskSafe(() ->
                    new ValueConflictResolverWindow(items).getResult());

            List<ImportedProduct> treatedItems = confirmedConflictItems.stream()
                    .peek(conflictItem -> conflictItem.getConflictPropertyMap().values()
                            .forEach(list -> list.stream()
                                    .filter(ConflictProperty::isSelected)
                                    .forEach(property -> conflictItem.getPropertyMap().put(
                                            property.getProperty().getDataItem(),
                                            property.getProperty())
                                    )
                            )
                    )
                    .map(conflictItem -> new ImportedProduct(conflictItem.getPropertyMap()))
                    .collect(Collectors.toList());

            result.addAll(treatedItems);
        }

        return result;
    }

    public void clearCash() {
        processedItems = new HashMap<>();
        conflictItems = new HashMap<>();
    }
}
