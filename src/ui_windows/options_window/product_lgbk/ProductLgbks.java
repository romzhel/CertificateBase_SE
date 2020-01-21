package ui_windows.options_window.product_lgbk;

import core.CoreModule;
import core.Dialogs;
import database.ProductLgbksDB;
import javafx.scene.control.TreeTableView;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.product.Product;

import java.util.ArrayList;
import java.util.TreeSet;

public class ProductLgbks {
    private ArrayList<ProductLgbk> productLgbks;
    private ProductLgbksTable productLgbksTable;

    public ProductLgbks() {
//        productLgbks = new ArrayList<>();
    }

    public ProductLgbks getFromDB() {
        productLgbks = new ProductLgbksDB().getData();
        return this;
    }

    public void addItem(ProductLgbk productLgbk) {
        if (new ProductLgbksDB().putData(productLgbk)) {
            productLgbks.add(productLgbk);

            TreeTableView<ProductLgbk> tableView = productLgbksTable.getTableView();
            CoreModule.getProductLgbkGroups().createFromLgbks(this);
            tableView.setRoot(CoreModule.getProductLgbkGroups().getFullTreeSet());
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
            CoreModule.getProductLgbkGroups().createFromLgbks(this);
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

    public ProductLgbk getByLgbkName(String lgbkName) {
        for (ProductLgbk plgbk : productLgbks) {
            if (plgbk.getLgbk().equals(lgbkName) && plgbk.getHierarchy().equals("Все")) {
                return plgbk;
            }
        }
        return null;
    }

    public ProductLgbk getByLgbkCombinedText(String combinedText) {
        if (combinedText.contains("[") && combinedText.contains("]")) {
            String findingLgbk = combinedText.substring(1, combinedText.indexOf("]"));
            return getByLgbkName(findingLgbk);
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

    public TreeSet<String> getLgbkDescALbyNamesAL(ArrayList<String> lgbkNames) {
        TreeSet<String> result = new TreeSet<>();
        ProductLgbk pl;
        for (String lgbkName : lgbkNames) {
            pl = getByLgbkName(lgbkName);
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
            pl = getByLgbkName(lgbkName);

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

    public ArrayList<ProductLgbk> getItems() {
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
