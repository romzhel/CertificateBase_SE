package ui_windows.options_window.product_lgbk;

import core.Initializable;
import database.ProductLgbksDB;
import javafx.scene.control.TreeTableView;
import ui.Dialogs;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.product.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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
        if (new ProductLgbksDB().putData(productLgbk)) {
            productLgbks.add(productLgbk);

            TreeTableView<ProductLgbk> tableView = productLgbksTable.getTableView();
            ProductLgbkGroups.getInstance().createFromLgbks(this);
            tableView.setRoot(ProductLgbkGroups.getInstance().getFullTreeSet());
        }
    }

    public void addItems(ArrayList<ProductLgbk> items) {
        for (ProductLgbk productLgbk : items) {
            if (new ProductLgbksDB().putData(productLgbk)) {
                productLgbks.add(productLgbk);
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

    public ProductLgbk getLgbkByProduct(Product product) {
        return getLgbkByLgbk(new ProductLgbk(product));
    }

    public List<ProductLgbk> getItems() {
        return productLgbks;
    }

    public void setProductLgbks(ArrayList<ProductLgbk> productLgbks) {
        this.productLgbks = productLgbks;
    }

    public ProductLgbksTable getProductLgbksTable() {
        return productLgbksTable;
    }

    public void setProductLgbksTable(ProductLgbksTable productLgbksTable) {
        this.productLgbksTable = productLgbksTable;
    }
}
