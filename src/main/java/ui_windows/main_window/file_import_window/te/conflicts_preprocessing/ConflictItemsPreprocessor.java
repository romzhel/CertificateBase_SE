package ui_windows.main_window.file_import_window.te.conflicts_preprocessing;

import core.ThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.main_window.file_import_window.te.ImportColumnParameter;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.product.Product;
import ui_windows.product.data.DataItem;
import utils.comparation.products.ProductNameResolver;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ConflictItemsPreprocessor {
    private static final Logger logger = LogManager.getLogger(ConflictItemsPreprocessor.class);
    private Map<String, ImportedProduct> processedItems = new HashMap<>();
    private Map<String, ConflictItem> conflictItems = new HashMap<>();

    public void process(ImportedProduct... importedProducts) {
        for (ImportedProduct importedItem : importedProducts) {
            String resolvedMaterial = ProductNameResolver.resolve(importedItem.getMaterial());

            ImportedProduct existsProduct;

            if (conflictItems.containsKey(resolvedMaterial)) {
                ConflictItem conflictItem = conflictItems.get(resolvedMaterial);
                existsProduct = conflictItems.get(resolvedMaterial).getCalculatedItem();
                List<DataItem> diffDataItems = getDifferDataItems(importedItem, existsProduct);

                for (DataItem changedDataItem : diffDataItems) {
                    conflictItem.getConflictValues().compute(changedDataItem, (k, v) -> {
                        ConflictItemValue conflictItemValue = new ConflictItemValue();
                        conflictItemValue.setValue(changedDataItem.getValue(importedItem));

                        if (v == null) {
                            ConflictItemValue conflictItemValue2 = new ConflictItemValue();
                            conflictItemValue2.setValue(changedDataItem.getValue(existsProduct));
                            return Arrays.asList(conflictItemValue2, conflictItemValue);
                        } else {
                            boolean valueNotExists = v.stream()
                                    .map(ConflictItemValue::getValue)
                                    .noneMatch(value -> value.equals(changedDataItem.getValue(importedItem)));
                            if (valueNotExists) {
                                List<ConflictItemValue> values = new ArrayList<>(v);
                                values.add(conflictItemValue);
                                v = values;
                            }
                            return v;
                        }
                    });
                }
            } else if (processedItems.containsKey(resolvedMaterial)) {
                existsProduct = processedItems.get(resolvedMaterial);
                List<DataItem> diffDataItems = getDifferDataItems(importedItem, existsProduct);

                for (DataItem changedDataItem : diffDataItems) {
                    ConflictItem conflictItem = new ConflictItem();
                    conflictItem.setCalculatedItem(existsProduct);

                    ConflictItemValue val1 = new ConflictItemValue();
                    val1.setValue(changedDataItem.getValue(existsProduct));
                    ConflictItemValue val2 = new ConflictItemValue();
                    val2.setValue(changedDataItem.getValue(importedItem));

                    conflictItem.getConflictValues().put(changedDataItem, Arrays.asList(val1, val2));
                    conflictItems.put(resolvedMaterial, conflictItem);

                    processedItems.remove(resolvedMaterial);
                }
            } else {
                processedItems.put(resolvedMaterial, importedItem);
            }
        }
    }

    private List<DataItem> getDifferDataItems(ImportedProduct item1, Product item2) {
        return item1.getImportDataSheet().getColumnParams().stream()
                .map(ImportColumnParameter::getDataItem)
//                .peek(dataItem -> logger.debug("check diff for data item {}", dataItem))
                .filter(dataItem -> dataItem.getValue(item1) != null && !dataItem.getValue(item1).equals(dataItem.getValue(item2)))
                .collect(Collectors.toList());
    }

    public List<ImportedProduct> processConflictsAndGetItems() throws RuntimeException {
        List<ConflictItem> items = new ArrayList<>(conflictItems.values());
        List<ConflictItem> confirmedValues = ThreadManager.executeFxTaskSafe(() -> new ValueConflictResolverWindow(items).getResult());
        AtomicReference<Throwable> lambdaError = new AtomicReference<>(null);
        List<ImportedProduct> fixedItems = confirmedValues.stream()
                .map(conflictItem -> {
                    ImportedProduct product = conflictItem.getCalculatedItem();
                    Object value = null;
                    Field field = null;
                    try {
                        for (Map.Entry<DataItem, List<ConflictItemValue>> entry : conflictItem.getConflictValues().entrySet()) {
                            value = entry.getValue().stream().filter(ConflictItemValue::isSelected).findFirst().get();
                            field = entry.getKey().getField();
                            field.setAccessible(true);
                            field.set(product, value);
                        }
                    } catch (Exception e) {
                        lambdaError.set(e);
                        logger.error("Error setting  value '{}' for field'{}' of Product '{}'", value, field, product, e);
                    }
                    return product;
                })
                .collect(Collectors.toList());

        if (lambdaError.get() != null) {
            throw new RuntimeException(lambdaError.get());
        }

        List<ImportedProduct> result = new ArrayList<>();
        result.addAll(processedItems.values());
        result.addAll(fixedItems);
        return result;
    }

    public void clearCash() {
        processedItems = new HashMap<>();
        conflictItems = new HashMap<>();
    }
}
