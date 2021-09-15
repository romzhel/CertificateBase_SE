package ui_windows.options_window.families_editor;

import core.Initializable;
import database.ProductFamiliesDB;
import ui_windows.options_window.product_lgbk.LgbkAndParent;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.product_lgbk.ProductLgbkGroups;
import ui_windows.product.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ProductFamilies implements Initializable {
    public static final ProductFamily UNKNOWN = new ProductFamily("Неизвестно", -1, "");
    private static ProductFamilies instance;
    private ProductFamiliesTable productFamiliesTable;
    private List<ProductFamily> productFamilies;

    private ProductFamilies() {
    }

    public static ProductFamilies getInstance() {
        if (instance == null) {
            instance = new ProductFamilies();
        }
        return instance;
    }

    @Override
    public void init() {
        productFamilies = new ProductFamiliesDB().getData();
    }

    public ProductFamiliesTable getProductFamiliesTable() {
        return productFamiliesTable;
    }

    public void setProductFamiliesTable(ProductFamiliesTable productFamiliesTable) {
        this.productFamiliesTable = productFamiliesTable;
    }

    public List<ProductFamily> getItems() {
        return productFamilies;
    }

    public void setProductFamilies(ArrayList<ProductFamily> productFamilies) {
        this.productFamilies = productFamilies;
    }

    public void addItem(ProductFamily pf) {
        if (new ProductFamiliesDB().putData(pf)) {
            productFamilies.add(pf);
            productFamiliesTable.getTableView().getItems().add(pf);
        }
    }

    public boolean hasDublicates(String name) {
        for (ProductFamily pf : productFamilies) {
            if (pf.getName().equals(name)) return true;
        }

        return false;
    }

    public void removeItem(ProductFamily pf) {
        if (new ProductFamiliesDB().deleteData(pf)) {
            productFamilies.remove(pf);
            productFamiliesTable.getTableView().getItems().remove(pf);
        }
    }

    public String getFamilyNameById(int id) {
        for (ProductFamily pf : productFamilies) {
            if (pf.getId() == id) return pf.getName();
        }

        return "";
    }

    public ProductFamily getFamilyById(int id) {
        for (ProductFamily pf : productFamilies) {
            if (pf.getId() == id) return pf;
        }

        return null;
    }

    public ProductFamily getFamilyByName(String name) {
        for (ProductFamily pf : productFamilies) {
            if (pf.getName().equals(name)) return pf;
        }

        return null;
    }

    public int getFamilyIdByName(String name) {
        if (name == null || name.isEmpty()) {
            return -1;
        }

        for (ProductFamily pf : productFamilies) {
            if (pf.getName().equals(name)) return pf.getId();
        }

        return -1;
    }

    public List<String> getFamiliesNames() {
        Set<String> res = new TreeSet<>();
        for (ProductFamily pf : productFamilies) {
            res.add(pf.getName());
        }

        return new ArrayList<>(res);
    }

    public ProductFamily getProductFamily(Product product) {
        if (product == null) {
            return UNKNOWN;
        }

        if (product.getFamily_id() != null && product.getFamily_id() > 0) {
            return getFamilyById(product.getFamily_id());
        } else {
            if (product.getLgbk() == null || product.getLgbk().isEmpty()) return UNKNOWN;

            return getFamilyByLgbk(new ProductLgbk(product));
        }
    }

    public ProductFamily getFamilyByLgbk(ProductLgbk lgbk) {
        if (lgbk == null || lgbk.getLgbk() == null || lgbk.getHierarchy() == null) {
            return UNKNOWN;
        }

        LgbkAndParent lgbkAndParent = ProductLgbkGroups.getInstance().getLgbkAndParent(lgbk);
        if (lgbkAndParent == null) return UNKNOWN;

        ProductLgbk lgbkItem = lgbkAndParent.getLgbkItem();
        ProductLgbk lgbkParent = lgbkAndParent.getLgbkParent();

        if (lgbkItem != null && lgbkItem.getFamilyId() > 0) {
            return getFamilyById(lgbkItem.getFamilyId());
        } else if (lgbkParent != null && lgbkParent.getFamilyId() > 0) {
            return getFamilyById(lgbkParent.getFamilyId());
        }

        return UNKNOWN;
    }

    public boolean isSpProduct(Product product) {
        ProductFamily pf = getProductFamily(product);
        return pf.getId() == 24;
    }

    public boolean isSpLgbkName(String lgbk) {
        return lgbk.startsWith("LVB");
    }
}
