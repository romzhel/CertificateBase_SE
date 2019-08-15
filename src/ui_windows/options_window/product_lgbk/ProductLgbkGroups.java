package ui_windows.options_window.product_lgbk;

import core.CoreModule;
import database.ProductLgbksDB;
import javafx.scene.control.TreeItem;
import ui_windows.main_window.Product;
import ui_windows.main_window.Products;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class ProductLgbkGroups {
    private TreeSet<ProductLgbkGroup> lgbkGroups;

    public ProductLgbkGroups() {
        lgbkGroups = new TreeSet<>((o1, o2) -> o1.getLgbkName().compareTo(o2.getLgbkName()));
    }

    public ProductLgbkGroups get() {
        lgbkGroups.clear();
        if (CoreModule.getProductLgbks().getProductLgbks().size() > 0) {
            createFromLgbks(CoreModule.getProductLgbks());
        } else {
            createFromProducts(CoreModule.getProducts());
        }
        return this;
    }

    public void createFromProducts(Products products) {
        ArrayList<ProductLgbk> newLgbkItems = new ArrayList<>();
        ProductLgbksDB lgbksDB = new ProductLgbksDB();

        for (Product product : products.getItems()) {
            String hierarchyName = product.getHierarchy();
            String productHierarchy = hierarchyName.isEmpty() ? "" : hierarchyName.matches("^\\d.*") ?
                    hierarchyName.substring(1, 4) : hierarchyName.substring(0, 3);

            ProductLgbk newLgbk = new ProductLgbk(product.getLgbk(), productHierarchy + "...");
            if (treateItem(newLgbk)) {
                newLgbkItems.add(newLgbk);
                try {
                    if (!CoreModule.getDataBase().getDbConnection().isClosed()){
                        lgbksDB.putData(newLgbk);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        CoreModule.getProductLgbks().getProductLgbks().clear();
        CoreModule.getProductLgbks().getProductLgbks().addAll(newLgbkItems);
    }

    public void createFromLgbks(ProductLgbks productLgbks) {
        for (ProductLgbk lgbk : productLgbks.getProductLgbks()) {
            treateItem(lgbk);
        }
    }

    private boolean treateItem(ProductLgbk newLgbk) {
        ProductLgbkGroup newGroup = new ProductLgbkGroup(newLgbk.getLgbk());

        if (!lgbkGroups.contains(newGroup)) {
            lgbkGroups.add(newGroup);
            newGroup.addProductLgbk(newLgbk);
            return true;
        } else {                                 //LGBK exists
            Iterator<ProductLgbkGroup> iterator = lgbkGroups.iterator();
            while (iterator.hasNext()) {
                ProductLgbkGroup temp = iterator.next();

                if (temp.getLgbkName().equals(newGroup.getLgbkName())) {
                    return temp.addProductLgbk(newLgbk);
                }
            }
        }
        return false;
    }

    public void checkConsistency() {
        createFromProducts(CoreModule.getProducts());
        CoreModule.getProductLgbks().getProductLgbksTable().getTableView().getItems().clear();
        CoreModule.getProductLgbks().getProductLgbksTable().getTableView().getItems().addAll(getLgbkTreeSet())
    };

    public TreeItem<ProductLgbk> getLgbkTreeSet() {
        TreeItem<ProductLgbk> rootNode = new TreeItem<>(new ProductLgbk("Все позиции", ""));

        for (ProductLgbkGroups.ProductLgbkGroup lgbkGroup : lgbkGroups) {
            TreeItem<ProductLgbk> lgbkGroupNode = new TreeItem<>(new ProductLgbk(lgbkGroup.getLgbkName(), "Все"));

            for (ProductLgbk productLgbk : lgbkGroup.getLgbkItems()) {
                lgbkGroupNode.getChildren().add(new TreeItem<>(productLgbk));
            }

            rootNode.getChildren().add(lgbkGroupNode);
        }

        return rootNode;
    }

    private class ProductLgbkGroup {
        private String lgbkName;
        private TreeSet<ProductLgbk> lgbkItems;

        private ProductLgbkGroup(String lgbkName) {
            this.lgbkName = lgbkName;
            lgbkItems = new TreeSet<>((o1, o2) -> o1.getHierarchy().compareTo(o2.getHierarchy()));
        }

        private boolean addProductLgbk(ProductLgbk lgbk) {
            if (!lgbkItems.contains(lgbk)) {
                lgbkItems.add(lgbk);
                return true;
            } else return false;
        }

        public String getLgbkName() {
            return lgbkName;
        }

        public TreeSet<ProductLgbk> getLgbkItems() {
            return lgbkItems;
        }
    }
}
