package utils.comparation.se;

import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.product.Product;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ProductsComparator implements Comparator<Product> {
    public static final Logger logger = LogManager.getLogger(ProductsComparator.class);
    private ComparingParameters comparingParameters;
    private ComparisonResult<Product> comparisonResult;
    private ChangesFixer<Product> changesFixer;
    private Callback<Param<Product>, Param<Product>> customComparingRule;

    public ProductsComparator() {
        comparisonResult = new ComparisonResult<>();
    }

    public ComparisonResult<Product> compare(Product object1, Product object2, ComparingParameters parameters) {
        return compare(Collections.singleton(object1), Collections.singleton(object2), parameters);
    }

    public ComparisonResult<Product> compare(Collection<Product> items1, Collection<Product> items2, ComparingParameters parameters) {
        long t0 = System.currentTimeMillis();
        ObjectsComparatorSe<Product> objectsComparator = new ObjectsComparatorSe<>();
        comparingParameters = parameters;

        Map<String, Product> changedItems = collectionToMap(items2, parameters);
        Map<String, Product> goneItems = parameters.isCheckGoneItems() ? collectionToMap(items1, parameters) : new HashMap<>();
        logger.trace("подготовка к сравнению завершена, прошло времени {}", System.currentTimeMillis() - t0);

        Product item2;
        String material;
        for (Product item1 : items1) {
            material = parameters.getComparingRules().treatMaterial(item1.getMaterial());
            item2 = changedItems.get(material);

            if (item2 != null) {
                comparisonResult.addChangedItemResult(objectsComparator.compare(item1, item2, parameters));

                goneItems.remove(material);
                changedItems.remove(material);
            }
        }
        logger.trace("сравнение завершено, прошло времени {}", System.currentTimeMillis() - t0);

        for (Product item : changedItems.values()) {
            if (parameters.getComparingRules().addNewItem(item, parameters.getFields())) {
                comparisonResult.addNewItemResult(new ObjectsComparatorResultSe<>(null, item, parameters.getFields()));
            }
        }

        for (Product item : goneItems.values()) {
            comparisonResult.addGoneItemResult(new ObjectsComparatorResultSe<>(item, null, parameters.getFields()));
        }

        logger.trace("реульаты сравнения готовы, прошло времени {}", System.currentTimeMillis() - t0);

        return comparisonResult;
    }

    public void fixChanges() {
        changesFixer = new ChangesFixer<>();
        for (ObjectsComparatorResultSe<Product> result : comparisonResult.getChangedItemsResult()) {
            if (changesFixer.fixChanges(result)) {
                comparingParameters.getComparingRules().addHistoryComment(result);
            }
        }
        for (ObjectsComparatorResultSe<Product> result : comparisonResult.getNewItemsResult()) {
            comparingParameters.getComparingRules().addHistoryComment(result);

        }
        for (ObjectsComparatorResultSe<Product> result : comparisonResult.getGoneItemsResult()) {
            comparingParameters.getComparingRules().addHistoryComment(result);
        }
    }

    public String getLog() {
//        return logger.getComment(comparisonResult);
        return "not released yet";
    }

    public ComparisonResult<Product> getComparisonResult() {
        return comparisonResult;
    }

    private Map<String, Product> collectionToMap(Collection<Product> items, ComparingParameters parameters) {
        Map<String, Product> map = new HashMap<>(20000);
        items.forEach(product -> map.put(parameters.getComparingRules().treatMaterial(product.getMaterial()), product));
        return map;
    }
}
