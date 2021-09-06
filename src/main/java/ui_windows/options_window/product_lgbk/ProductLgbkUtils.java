package ui_windows.options_window.product_lgbk;

import javafx.scene.control.TreeItem;
import lombok.extern.log4j.Log4j2;
import ui_windows.main_window.file_import_window.te.importer.ImportedProperty;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.price_lists_editor.PriceLists;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.product.Product;
import ui_windows.product.Products;
import ui_windows.product.data.DataItem;
import utils.comparation.te.ChangedItem;
import utils.comparation.te.ChangedProperty;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ui_windows.product.data.DataItem.DATA_HIERARCHY;
import static ui_windows.product.data.DataItem.DATA_LGBK;

@Log4j2
public class ProductLgbkUtils {
    private final Pattern pattern = Pattern.compile("(\\d)?([A-Z0-9]{3})(.*)?");

    public Map<ProductLgbk, Set<ProductLgbk>> getOldToNewGbkMap(Collection<ChangedItem> changedItemList) {
        Map<ProductLgbk, Set<ProductLgbk>> newToOldgbkMap = new HashMap<>();
        Matcher matcher;

        for (ChangedItem changedItem : changedItemList) {
            Product existProduct = Products.getInstance().getProductByMaterial(changedItem.getId());

            boolean noHierarchyChanges = changedItem.getChangedPropertyList().stream()
                    .map(ImportedProperty::getDataItem)
                    .noneMatch(dataItem -> dataItem == DATA_LGBK || dataItem == DATA_HIERARCHY);

            if (noHierarchyChanges) {
                continue;
            }

            Map<DataItem, ChangedProperty> propertyMap = changedItem.getChangedPropertyList().stream()
                    .collect(Collectors.toMap(
                            ImportedProperty::getDataItem,
                            property -> property
                    ));
            String oldLgbk = existProduct.getLgbk();
            String oldHierarchy = existProduct.getHierarchy();
            String newLgbk = propertyMap.get(DATA_LGBK) == null ? oldLgbk : propertyMap.get(DATA_LGBK).getNewValue().toString();
            String newHierarchy = propertyMap.get(DATA_HIERARCHY) == null ? oldHierarchy : propertyMap.get(DATA_HIERARCHY).getNewValue().toString();

            ProductLgbk plOld = ProductLgbks.getInstance().getLgbkByLgbk(new ProductLgbk(oldLgbk, oldHierarchy));
            ProductLgbk plNew = ProductLgbks.getInstance().getLgbkByLgbk(new ProductLgbk(newLgbk, newHierarchy));

            if (plNew == null && plOld != null) {
                plNew = new ProductLgbk(newLgbk, newHierarchy);
                try {
                    String hier = plNew.getHierarchy();
                    matcher = pattern.matcher(hier);
                    matcher.matches();
                    plNew.setHierarchy(matcher.group(2).concat("..."));
                    plNew.setFamilyId(plOld.getFamilyId());
                } catch (Exception e) {
                    log.error("Ошибка преобразования LGBK '{}' - {}", plNew.toString(), e.getMessage());
                }

                newToOldgbkMap.merge(plNew, Collections.singleton(plOld), (oldSet, newSet) -> {
                    Set<ProductLgbk> resultList = new HashSet<>(oldSet);
                    resultList.addAll(newSet);
                    return resultList;
                });
            }
        }
        return newToOldgbkMap;
    }

    public void addMissedGroupItem(Map<ProductLgbk, Set<ProductLgbk>> newToOldgbkMap) {
        List<ProductLgbk> newLgbkList = new ArrayList<>(newToOldgbkMap.keySet());
        newLgbkList.stream()
                .map(ProductLgbk::getLgbk)
                .filter(lgbkName -> ProductLgbks.getInstance().getGroupLgbkByName(lgbkName) == null)
                .forEach(lgbkName -> newToOldgbkMap.put(
                        new ProductLgbk(lgbkName, "Все", ProductLgbk.GROUP_NODE), Collections.emptySet()));
    }

    public String getDoubleName(ProductLgbk lgbk) {
        return lgbk.getLgbk().concat("_").concat(lgbk.getHierarchy());
    }

    public void copyPriceToNewItems(Map<ProductLgbk, Set<ProductLgbk>> newToOldgbkMap) {
        PriceLists priceListService = PriceLists.getInstance();

        for (PriceList priceList : priceListService.getItems()) {
            log.debug("processing price list '{}' for new GBK", priceList.getName());

            for (PriceListSheet sheet : priceList.getSheets()) {
                log.debug("processing sheet '{}' for new GBK", sheet.getSheetName());

                List<Map.Entry<ProductLgbk, Set<ProductLgbk>>> sortedEntries = newToOldgbkMap.entrySet().stream()
                        .sorted((o1, o2) -> Integer.compare(o1.getKey().getNodeType(), o2.getKey().getNodeType()))
                        .collect(Collectors.toList());

                for (Map.Entry<ProductLgbk, Set<ProductLgbk>> entry : sortedEntries) {
                    Map<ProductLgbk, Boolean> map = sheet.getContentTable().getGbkInPriceMap();
                    ProductLgbk groupGbk = ProductLgbks.getInstance().getGroupLgbkByName(entry.getKey().getLgbk());
                    boolean groupGbkInPrice = sheet.getContentTable().getGbkInPriceMap().getOrDefault(groupGbk, false);
                    boolean newGbkInPrice = entry.getValue().stream().allMatch(sheet::isGbkStructureAddedToPrice);

                    if (groupGbkInPrice) {
                        if (newGbkInPrice) {
                            log.debug("GBK '{}' has parent price", entry.getKey());
                            continue;
                        } else {
                            log.debug("reset price for group GBK {}", groupGbk);
                            map.put(groupGbk, false);
                            TreeItem<ProductLgbk> groupTreeGbk = ProductLgbkGroups.getInstance().getTreeItem(groupGbk);
                            groupTreeGbk.getChildren().stream()
                                    .map(TreeItem::getValue)
                                    .peek(g -> log.debug("set price for children GDK {}", g))
                                    .forEach(g -> map.put(g, true));
                        }
                    }

                    map.put(entry.getKey(), newGbkInPrice);
                    log.debug("GBK '{}' was added to price content with value {}", entry.getKey(), newGbkInPrice);
                }
            }
            PriceLists.getInstance().saveItem(priceList);
        }

        priceListService.refreshContent();
    }

    private void inverseIfNeeded(PriceListSheet sheet, ProductLgbk key, Set<ProductLgbk> set) {


    }

    public void copyGbkSetting(Map<ProductLgbk, Set<ProductLgbk>> newToOldgbkMap) {
        for (Map.Entry<ProductLgbk, Set<ProductLgbk>> entry : newToOldgbkMap.entrySet()) {
            if (entry.getValue().size() == 1) {
                ProductLgbk newLgbk = entry.getKey();
                ProductLgbk oldLgbk = entry.getValue().iterator().next();
                ProductLgbk oldGroupLgbk = ProductLgbks.getInstance().getGroupLgbkByName(oldLgbk.getLgbk());

                NormsList normsList = new NormsList(oldLgbk.getNormsList() != null && !oldLgbk.getNormsList().getIntegerItems().isEmpty() ?
                        oldLgbk.getNormsList().getIntegerItems() : oldGroupLgbk.getNormsList().getIntegerItems());
                newLgbk.setNormsList(normsList);
                newLgbk.setFamilyId(oldLgbk.getFamilyId() == -1 ? oldGroupLgbk.getFamilyId() : oldLgbk.getFamilyId());
                newLgbk.setDescription_en(oldLgbk.getDescription_en());
                newLgbk.setDescription_ru(oldLgbk.getDescription_ru());
            } else {
                log.info("multi propagation for new GBK '{}'", entry.getKey());
            }
        }
    }
}
