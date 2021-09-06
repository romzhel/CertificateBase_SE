package ui_windows.options_window.product_lgbk;

import core.Initializable;
import database.ProductLgbksDB;
import javafx.application.Platform;
import javafx.scene.control.TreeTableView;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import ui.Dialogs;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.product.Product;
import utils.comparation.te.ChangedItem;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Log4j2
public class ProductLgbks implements Initializable {
    private static ProductLgbks instance;
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
            productLgbks.addAll(nonDoubleItems);
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
                    currLgbk.getLgbk().equals(findingLgbk.getLgbk()) &&
                            (findingLgbk.getHierarchy().contains(compHier) && !compHier.isEmpty() ||
                                    findingLgbk.getHierarchy().equals(compHier))) {
                return currLgbk;
            }
        }

        return null;
    }

    public void treatStructureChanges(Collection<ChangedItem> changedItemList) {
        ProductLgbkUtils utils = new ProductLgbkUtils();
        Map<ProductLgbk, Set<ProductLgbk>> newToOldgbkMap = utils.getOldToNewGbkMap(changedItemList);
        utils.addMissedGroupItem(newToOldgbkMap);

        if (newToOldgbkMap.keySet().size() > 0) {
            List<ProductLgbk> sortedList = new ArrayList<>(newToOldgbkMap.keySet());
            sortedList.sort((o1, o2) -> utils.getDoubleName(o1).compareToIgnoreCase(utils.getDoubleName(o2)));
            for (ProductLgbk plgbk : sortedList) {
                log.info("Обнаружена новая структура LGBK/Hierarchy: {}", plgbk);
            }
            Platform.runLater(() -> Dialogs.showMessage("Новые LGBK/Hierarchy",
                    "Обнаружено новых кодов LGBK/Hierarchy: " + newToOldgbkMap.keySet().size()));

            utils.copyGbkSetting(newToOldgbkMap);
            ProductLgbks.getInstance().addItems(newToOldgbkMap.keySet());

            utils.copyPriceToNewItems(newToOldgbkMap);
        }
    }

    public ProductLgbk getLgbkByProduct(Product product) {
        return getLgbkByLgbk(new ProductLgbk(product));
    }
}
