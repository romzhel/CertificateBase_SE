package ui_windows.options_window.families_editor;

import database.ProductFamiliesDB;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ProductFamilies {
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

    public void getFromDB() {
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
        for (ProductFamily pf : productFamilies) {
            if (pf.getName().equals(name)) return pf.getId();
        }

        return -1;
    }

    public ArrayList<String> getFamiliesNames() {
        TreeSet<String> res = new TreeSet<>();
        for (ProductFamily pf : productFamilies) {
            res.add(pf.getName());
        }

        return new ArrayList<>(res);
    }
}
