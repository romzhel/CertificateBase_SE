package ui_windows.options_window.product_lgbk;

import ui_windows.main_window.Product;
import ui_windows.main_window.Products;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

public class ProductLgbkGroups {
    private ArrayList<ProductLgbkGroup> lgbkGroups;

    public ProductLgbkGroups() {
        lgbkGroups = new ArrayList<>();
    }

    public void create(Products products) {
        ArrayList<String> lgbkNames = new ArrayList<>();

        for (Product product : products.getItems()) {
            String productLgbk = product.getLgbk();
            String productHierarchy = product.getHierarchy().isEmpty() ?
                    "" :
                    product.getHierarchy().matches("^\\d.*") ?
                            product.getHierarchy().substring(1, 4) :
                            product.getHierarchy().substring(0, 3);

            int index = getIndexByLgbk(productLgbk);

            if (index < 0) {//new LGBK
                lgbkNames.add(productLgbk);
                ProductLgbkGroup productLgbkGroup = new ProductLgbkGroup(productLgbk);
                lgbkGroups.add(productLgbkGroup);
                productLgbkGroup.addHierarchyName(productHierarchy);
            } else {                                 //LGBK exists
                lgbkGroups.get(index).addHierarchyName(productHierarchy);
            }
        }

        TreeSet<String> sortedLgbkNames = new TreeSet<>(lgbkNames);
        ArrayList<ProductLgbkGroup> sortedLgbkGroups = new ArrayList<>();

        for (String lgbkName : sortedLgbkNames) {
            ProductLgbkGroup lgbkGroup = lgbkGroups.get(getIndexByLgbk(lgbkName));
            sortedLgbkGroups.add(lgbkGroup);
            lgbkGroup.sort();
        }

        lgbkGroups = sortedLgbkGroups;
    }

    private int getIndexByLgbk(String productLgbk) {
        for (ProductLgbkGroup plg : lgbkGroups) {
            if (plg.lgbkName.equals(productLgbk)) return lgbkGroups.indexOf(plg);
        }
        return -1;
    }

    public ArrayList<ProductLgbkGroup> getLgbkGroups() {
        return lgbkGroups;
    }

    public class ProductLgbkGroup {
        private String lgbkName;
        private ArrayList<String> hierarchyNames;

        public ProductLgbkGroup(String lgbkName) {
            this.lgbkName = lgbkName;
            hierarchyNames = new ArrayList<>();
        }

        private void addHierarchyName(String hierarchyName) {
            HashSet<String> temp = new HashSet<>(hierarchyNames);
            temp.add(hierarchyName);
            hierarchyNames = new ArrayList<>(temp);
        }

        private void sort() {
            hierarchyNames = new ArrayList<>(new TreeSet<>(hierarchyNames));
        }

        public String getLgbkName() {
            return lgbkName;
        }

        public ArrayList<String> getHierarchyNames() {
            return hierarchyNames;
        }
    }
}
