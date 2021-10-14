package utils.comparation.te;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.product.Product;
import ui_windows.product.Products;
import utils.comparation.se.ComparingRules;
import utils.property_change_protect.ChangeProtectService;
import utils.property_change_protect.ProductProtectChange;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class TotalComparator {
    private static final Logger logger = LogManager.getLogger(TotalComparator.class);
    private TotalComparisonResult comparisonResult;

    public TotalComparisonResult compare(Collection<Product> items1, Collection<ImportedProduct> items2, ComparingRules<Product> rules) {
        logger.debug("Start comparing with rules {}", rules);
        long t0 = System.currentTimeMillis();
        SingleComparator comparator = new SingleComparator();
        ChangeProtectService protectService = new ChangeProtectService();
        comparisonResult = new TotalComparisonResult();

        Map<String, Product> leftItems = collectionToMap(items1);
        logger.trace("Подготовка к сравнению завершена, прошло времени {} мс", System.currentTimeMillis() - t0);

        for (ImportedProduct importedItem : items2) {
            Product existItem = leftItems.remove(importedItem.getId());

            if (existItem != null) {
                ChangedItem changedItem = comparator.compare(existItem, importedItem, rules);

                if (changedItem.getChangedPropertyList().size() > 0) {
                    comparisonResult.getChangedItemList().add(changedItem);
                } else {
                    comparisonResult.getNonChangedItemList().add(existItem);
                }

                if (changedItem.getProtectedField().size() > 0) {
                    comparisonResult.getNonChangedProtectedItemList().add(changedItem);
                }
            } else if (rules.addNewItem_v2(importedItem)) {
                comparisonResult.getNewItemList().add(importedItem);
            }

            ProductProtectChange productProtectChange = protectService.checkProtectChangesAndGetResult(importedItem);
            if (productProtectChange.getPropertyProtectChangeList().size() > 0) {
                comparisonResult.getProtectChangeItemList().add(productProtectChange);
            }
        }

        logger.trace("сравнение завершено, прошло времени {} мс", System.currentTimeMillis() - t0);
        comparisonResult.getGoneItemList().addAll(leftItems.values());
        logger.trace("результаты сравнения готовы, прошло времени {}", System.currentTimeMillis() - t0);

        return comparisonResult;
    }

    private Map<String, Product> collectionToMap(Collection<Product> items) {
        return items.stream()
//                .peek(item -> logger.debug("add collection item to map '{}'", item))
                .collect(Collectors.toMap(
                        item -> Products.getInstance().getVendorMaterial(item),
                        item -> item
                ));
    }
}
