package ui_windows.options_window.product_lgbk;

import core.CoreModule;
import core.Dialogs;
import database.ProductLgbksDB;
import javafx.scene.control.TreeTableView;
import ui_windows.main_window.Product;
import ui_windows.main_window.Products;
import ui_windows.options_window.families_editor.ProductFamily;

import java.util.ArrayList;

public class ProductLgbks {
    private ArrayList<ProductLgbk> productLgbks;
    private ProductLgbksTable productLgbksTable;
    private ProductLgbksDB db;

    public ProductLgbks() {
        productLgbks = new ArrayList<>();
        db = new ProductLgbksDB();
    }

    public ProductLgbks getFromDB() {
        productLgbks = db.getData();
        return this;
    }

    public void addItem(ProductLgbk productLgbk) {
        if (db.putData(productLgbk)) {
            productLgbks.add(productLgbk);

            TreeTableView<ProductLgbk> tableView = productLgbksTable.getTableView();
            CoreModule.getProductLgbkGroups().createFromLgbks(this);
            tableView.setRoot(CoreModule.getProductLgbkGroups().getLgbkTreeSet());
        }
    }

    public void addItems(ArrayList<ProductLgbk> items){
        for (ProductLgbk productLgbk:items             ) {
            if (db.putData(productLgbk)){
                productLgbks.add(productLgbk);
            }
        }
    }

    public void removeItem(ProductLgbk pl) {
        if (db.deleteData(pl)) {
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

    public int getFamilyIdByLgbk(ProductLgbk pl) {
        LgbkAndParent lgbkAndParent = CoreModule.getProductLgbkGroups().getLgbkAndParent(pl);
        if (lgbkAndParent == null) return -1;

        if (lgbkAndParent.getLgbkItem() == null || lgbkAndParent.getLgbkItem().getFamilyId() == -1) {
            if (lgbkAndParent.getLgbkParent() == null) {
                return -1;
            } else {
                return lgbkAndParent.getLgbkParent().getFamilyId();
            }
        } else {
            return lgbkAndParent.getLgbkItem().getFamilyId();
        }
    }

    public ProductLgbk getLgbkByValues(ProductLgbk pl) {
        String lgbkP = pl.getLgbk();
        String hierarchyL;

        for (ProductLgbk plg : productLgbks) {
            if (plg.getHierarchy().trim().length() == 0) hierarchyL = ".*";
            else hierarchyL = plg.getHierarchy().replaceAll("\\*", ".*");

            if (lgbkP.length() > 0 && plg.getLgbk().length() > 0) {

                if (lgbkP.matches(plg.getLgbk()) && pl.getHierarchy().matches(hierarchyL)) return plg;
            } else if (lgbkP.length() == 0 && plg.getLgbk().length() == 0) {
                if (pl.getHierarchy().matches(hierarchyL)) return plg;
            }
        }

        return null;
    }

    public ArrayList<ProductLgbk> getProductLgbks() {
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
