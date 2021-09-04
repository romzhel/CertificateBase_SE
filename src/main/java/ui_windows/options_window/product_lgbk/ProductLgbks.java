package ui_windows.options_window.product_lgbk;

import core.Initializable;
import database.ProductLgbksDB;
import javafx.application.Platform;
import javafx.scene.control.TreeTableView;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import ui.Dialogs;
import ui_windows.main_window.file_import_window.te.importer.ImportedProperty;
import ui_windows.options_window.families_editor.ProductFamily;
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

@Data
@Log4j2
public class ProductLgbks implements Initializable {
    private static ProductLgbks instance;
    private final Pattern pattern = Pattern.compile("(\\d)?([A-Z0-9]{3})(.*)?");
    private List<ProductLgbk> productLgbks;
    private ProductLgbksTable productLgbksTable;

    private ProductLgbks() {
    }

    public static ProductLgbks getInstance() {
        if (instance == null) {
            instance = new ProductLgbks();
        }
        return instance;
    }

    @Override
    public void init() {
        productLgbks = new ProductLgbksDB().getData();
    }

    public void addItem(ProductLgbk productLgbk) {
        addItems(Collections.singleton(productLgbk));
    }

    public void addItems(Collection<ProductLgbk> items) {
        Collection<ProductLgbk> nonDoubleItems = items.stream()
                .filter(lgbk -> !hasDublicates(lgbk))
                .collect(Collectors.toList());

        if (new ProductLgbksDB().putData(nonDoubleItems)) {
            productLgbks.addAll(items);
            ProductLgbkGroups.getInstance().createFromLgbks(this);

            if (productLgbksTable != null) {
                TreeTableView<ProductLgbk> tableView = productLgbksTable.getTableView();
                tableView.setRoot(ProductLgbkGroups.getInstance().getFullTreeSet());
            }
        }
    }

    public void removeItem(ProductLgbk pl) {
        if (new ProductLgbksDB().deleteData(pl)) {
            productLgbks.remove(pl);
            ProductLgbkGroups.getInstance().createFromLgbks(this);
        }
    }

    public boolean isFamilyUsed(ProductFamily pf) {
        for (ProductLgbk pl : productLgbks) {
            if (pl.getFamilyId() == pf.getId()) return true;
        }

        return false;
    }

    public boolean hasDublicates(ProductLgbk pl) {
        for (ProductLgbk plgbk : productLgbks) {
            if (plgbk.getLgbk().equals(pl.getLgbk()) && plgbk.getHierarchy().equals(pl.getHierarchy())) {
                Dialogs.showMessage("Дублирующиеся значения", "Запись с такими данными уже существует");
                return true;
            }
        }

        return false;
    }

    public ProductLgbk getByLgbkNameDefValue(String lgbkName, ProductLgbk defValue) {
        ProductLgbk productLgbk = getGroupLgbkByName(lgbkName);
        return productLgbk == null ? defValue : productLgbk;
    }

    public ProductLgbk getGroupLgbkByName(String lgbkName) {
        for (ProductLgbk plgbk : productLgbks) {
            if (plgbk.getLgbk().equals(lgbkName) && plgbk.getNodeType() == ProductLgbk.GROUP_NODE) {
                return plgbk;
            }
        }
        return null;
    }

    public ProductLgbk getByLgbkCombinedText(String combinedText) {
        if (combinedText.contains("[") && combinedText.contains("]")) {
            String findingLgbk = combinedText.substring(1, combinedText.indexOf("]"));
            return getGroupLgbkByName(findingLgbk);
        }
        return null;
    }

    public ProductLgbk getByDescription(String lgbkDescription) {
        for (ProductLgbk plgbk : productLgbks) {
            if (plgbk.getDescription().equals(lgbkDescription) && plgbk.getHierarchy().equals("Все")) {
                return plgbk;
            }
        }
        return null;
    }

    public ProductLgbk getLgbkByHierarchy(String hierarchy) {
        for (ProductLgbk lgbk : productLgbks) {
            if (hierarchy.equals(lgbk.getHierarchy())) {
                return lgbk;
            }
        }

        return null;
    }

    public TreeSet<String> getLgbkDescALbyNamesAL(ArrayList<String> lgbkNames) {
        TreeSet<String> result = new TreeSet<>();
        ProductLgbk pl;
        for (String lgbkName : lgbkNames) {
            pl = getGroupLgbkByName(lgbkName);
            if (pl != null)
                result.add("[" + pl.getLgbk() + "] " + pl.getDescription());
        }
        return result;
    }

    public TreeSet<String> getLgbkNameALbyDescsAL(ArrayList<String> lgbkDescs) {
        TreeSet<String> result = new TreeSet<>();
        ProductLgbk pl;
        String lgbkName, desc;
        for (String lgbkDesc : lgbkDescs) {
            String[] parts = lgbkDesc.split("\\]");
            lgbkName = parts[0].replaceAll("[\\[\\s]", "");
            pl = getGroupLgbkByName(lgbkName);

            if (pl != null) result.add(pl.getLgbk());
        }
        return result;
    }

    public ProductLgbk getLgbkById(int id) {
        for (ProductLgbk lgbk : productLgbks) {
            if (id == lgbk.getId()) {
                return lgbk;
            }
        }
        return null;
    }

    public ProductLgbk getLgbkByLgbk(ProductLgbk findingLgbk) {
        String compHier = "";
        for (ProductLgbk currLgbk : productLgbks) {
            compHier = currLgbk.getHierarchy().replaceAll("\\.", "").trim();
            if (currLgbk.getId() == findingLgbk.getId() ||
                    currLgbk.getLgbk().equals(findingLgbk.getLgbk()) && findingLgbk.getHierarchy()
                            .contains(compHier) && !compHier.isEmpty()) {
                return currLgbk;
            }
        }

        return null;
    }

    public void treatStructureChanges(Collection<ChangedItem> changedItemList) {
        /*Set<ProductLgbk> changedProductLgbk = new TreeSet<>((o1, o2) ->
                o1.getLgbk().concat(o1.getHierarchy()).compareToIgnoreCase(o2.getLgbk().concat(o2.getHierarchy())));
        Set<String> lgbkNames = new TreeSet<>(String::compareToIgnoreCase);*/

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

                //            if (!(newToOldgbkMap.containsKey(plNew))) {
//                log.debug("new ProductLgbk '{}'", plNew);
//            }

                newToOldgbkMap.merge(plNew, Collections.singleton(plOld), (oldSet, newSet) -> {
                    Set<ProductLgbk> resultList = new HashSet<>(oldSet);
                    resultList.addAll(newSet);
                    return resultList;
                });
            }
        }

        List<ProductLgbk> newLgbkList = new ArrayList<>(newToOldgbkMap.keySet());
        newLgbkList.stream()
                .map(ProductLgbk::getLgbk)
                .filter(lgbkName -> ProductLgbks.getInstance().getGroupLgbkByName(lgbkName) == null)
                .forEach(lgbkName -> newToOldgbkMap.put(new ProductLgbk(lgbkName, "Все", ProductLgbk.GROUP_NODE), null));

        if (newToOldgbkMap.keySet().size() > 0) {
            List<ProductLgbk> sortedList = new ArrayList<>(newToOldgbkMap.keySet());
            sortedList.sort((o1, o2) -> getDoubleName(o1).compareToIgnoreCase(getDoubleName(o2)));
            for (ProductLgbk plgbk : sortedList) {
                log.info("Обнаружена новая структура LGBK/Hierarchy: {}", plgbk);
            }
            Platform.runLater(() -> Dialogs.showMessage("Новые LGBK/Hierarchy",
                    "Обнаружено новых кодов LGBK/Hierarchy: " + newToOldgbkMap.keySet().size()));
            addItems(newToOldgbkMap.keySet());
        }
    }

    public String getDoubleName(ProductLgbk lgbk) {
        return lgbk.getLgbk().concat("_").concat(lgbk.getHierarchy());
    }

    public ProductLgbk getLgbkByProduct(Product product) {
        return getLgbkByLgbk(new ProductLgbk(product));
    }
}
