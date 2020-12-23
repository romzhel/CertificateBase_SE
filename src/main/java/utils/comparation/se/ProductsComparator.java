package utils.comparation.se;

import javafx.application.Platform;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui.Dialogs;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.product_lgbk.ProductLgbks;
import ui_windows.product.Product;
import ui_windows.product.data.DataItem;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductsComparator implements Comparator<Product> {
    public static final Logger logger = LogManager.getLogger(ProductsComparator.class);
    private ComparingParameters<Product> comparingParameters;
    private ProductsComparisonResult comparisonResult;
    private ChangesFixer<Product> changesFixer;
    private Callback<Param<Product>, Param<Product>> customComparingRule;

    public ProductsComparator() {
        comparisonResult = new ProductsComparisonResult();
    }

    public ComparisonResult<Product> compare(Product object1, Product object2, ComparingParameters<Product> parameters) {
        return compare(Collections.singleton(object1), Collections.singleton(object2), parameters);
    }

    public ComparisonResult<Product> compare(Collection<Product> items1, Collection<Product> items2, ComparingParameters<Product> parameters) {
        long t0 = System.currentTimeMillis();
        ObjectsComparatorSe<Product> objectsComparator = new ObjectsComparatorSe<>();
        comparingParameters = parameters;

        Map<String, Product> goneItems = collectionToMap(items1, parameters);
//        Map<String, Product> goneItems = parameters.isCheckGoneItems() ? collectionToMap(items1, parameters) : new HashMap<>();
        Map<String, Product> changedItems = collectionToMap(items2, parameters);
        Map<String, Product> nonChangedItems = new HashMap<>();
        logger.trace("подготовка к сравнению завершена, прошло времени {}", System.currentTimeMillis() - t0);

        Product item2;
        String material;
        for (Product item1 : items1) {

            if (item1.getMaterial().equals("410355768")) {
                System.out.println();
            }


            material = parameters.getComparingRules().treatMaterial(item1.getMaterial());
            item2 = changedItems.get(material);

            if (item2 != null) {
                ObjectsComparatorResultSe<Product> ocr = objectsComparator.compare(item1, item2, parameters);
                if (ocr.getChangedFields().size() > 0) {
                    comparisonResult.addChangedItemResult(ocr);
                } else {
                    comparisonResult.addNonChangedItemResult(ocr);
                }

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
            comparisonResult.addGoneItemResult(new ObjectsComparatorResultSe<>(item, null, Collections.emptyList()));
        }

        logger.trace("результаты сравнения готовы, прошло времени {}", System.currentTimeMillis() - t0);

        return comparisonResult;
    }

    public void fixChanges() {
        Set<ProductLgbk> changedProductLgbk = new TreeSet<>((o1, o2) -> {
            return o1.getLgbk().concat(o1.getHierarchy()).compareToIgnoreCase(o2.getLgbk().concat(o2.getHierarchy()));
        });
        Pattern pattern = Pattern.compile("(\\d)?([A-Z0-9]{3})(.*)?");
        Matcher matcher;
        changesFixer = new ChangesFixer<>();
        Set<String> lgbkNames = new TreeSet<>(String::compareToIgnoreCase);
        for (ObjectsComparatorResultSe<Product> result : comparisonResult.getChangedItemsResult()) {
            if (changesFixer.fixChanges(result)) {
                comparingParameters.getComparingRules().addHistoryComment(result);//TODO при импорте 3-х файлов история дублируется
                if (result.getChangedFields().contains(DataItem.DATA_LGBK.getField()) || result.getChangedFields().contains(DataItem.DATA_HIERARCHY.getField())) {
                    ProductLgbk plOld = ProductLgbks.getInstance().getLgbkByProduct(result.getItem_before());
                    ProductLgbk plNew = ProductLgbks.getInstance().getLgbkByProduct(result.getItem_after());
                    if (plNew == null && plOld != null) {
                        plNew = new ProductLgbk(result.getItem_after());
                        try {
                            String hier = plNew.getHierarchy();
                            matcher = pattern.matcher(hier);
                            matcher.matches();
                            plNew.setHierarchy(matcher.group(2).concat("..."));

                            plNew.setFamilyId(plOld.getFamilyId());
                            changedProductLgbk.add(plNew);
                            lgbkNames.add(plNew.getLgbk());
//                            logger.debug("new ProductLgbk {} from {}", plNew, plOld);
                        } catch (Exception e) {
                            logger.error("ошибка преобразования LGBK {}", plNew.toString());
                        }
                    }
                }
            }
        }
        for (String lgbk : lgbkNames) {
            if (ProductLgbks.getInstance().getGroupLgbkByName(lgbk) == null) {
                changedProductLgbk.add(new ProductLgbk(lgbk, "Все", ProductLgbk.GROUP_NODE));
            }
        }

        for (ObjectsComparatorResultSe<Product> result : comparisonResult.getNewItemsResult()) {
            comparingParameters.getComparingRules().addHistoryComment(result);

        }
        for (ObjectsComparatorResultSe<Product> result : comparisonResult.getGoneItemsResult()) {
            comparingParameters.getComparingRules().addHistoryComment(result);
        }

        if (changedProductLgbk.size() > 0) {
            StringBuilder messageBuilder = new StringBuilder();
            for (ProductLgbk plgbk : changedProductLgbk) {
                messageBuilder.append(plgbk.toString()).append("\n");
            }
            Platform.runLater(() -> Dialogs.showMessage("Новые LGBK/Hierarchy",
                    "Обнаружены новые коды LGBK/Hierarchy:\n" + messageBuilder.toString()));
            ProductLgbks.getInstance().addItems(changedProductLgbk);
        }
    }

    public String getLog() {
//        return logger.getComment(comparisonResult);
        return "not released yet";
    }

    public ProductsComparisonResult getComparisonResult() {
        return comparisonResult;
    }

    private Map<String, Product> collectionToMap(Collection<Product> items, ComparingParameters parameters) {
        Map<String, Product> map = new HashMap<>(20000);
        items.forEach(product -> map.put(parameters.getComparingRules().treatMaterial(product.getMaterial()), product));
        return map;
    }
}
