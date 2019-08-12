package ui_windows.options_window.product_lgbk;

import core.Dialogs;
import database.ProductLgbksDB;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ui_windows.main_window.Product;
import ui_windows.main_window.Products;
import ui_windows.options_window.families_editor.ProductFamily;

import java.util.ArrayList;
import java.util.HashSet;

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

            TableView<ProductLgbk> tableView = productLgbksTable.getTableView();
            tableView.getItems().add(productLgbk);

            tableView.getColumns().get(0).setSortType(TableColumn.SortType.DESCENDING);
            tableView.getColumns().get(1).setSortType(TableColumn.SortType.ASCENDING);
            tableView.getSortOrder().setAll(tableView.getColumns().get(0), tableView.getColumns().get(1));
        }
    }

    public void removeItem(ProductLgbk pl) {
        if (db.deleteData(pl)) {
            productLgbks.remove(pl);
            productLgbksTable.getTableView().getItems().remove(pl);
        }
    }

    public boolean isFamilyUsed(ProductFamily pf) {
        for (ProductLgbk pl : productLgbks) {
            if (pl.getFamilyId() == pf.getId()) return true;
        }

        return false;
    }

//    public ArrayList<ProductLgbk> getSortedProductLgbks(){
//        HashSet<String> withLgbk = new HashSet<>();
//        HashSet<String> withHierarchy = new HashSet<>();
//
//        for (ProductLgbk pl:productLgbks) {
//            if (pl.getLgbk().trim().length() > 0) withLgbk.add(pl.)
//        }
//
//    }

    public boolean hasDublicates(ProductLgbk pl) {
        for (ProductLgbk plgbk : productLgbks) {
            if (plgbk.getLgbk().equals(pl.getLgbk()) && plgbk.getHierarchy().equals(pl.getHierarchy())) {
                Dialogs.showMessage("Дублирующиеся значения", "Запись с такими данными уже существует");
                return true;
            }
        }

        return false;
    }


    public ArrayList<ProductLgbk> getLostLgbkFromProducts(Products products) {
        ArrayList<ProductLgbk> res = new ArrayList<>();

        for (Product pr : products.getItems()) {
            if (!pr.hasLgbkMapping()) {
                ProductLgbk newPl = null;

                if (pr.getLgbk().length() > 0) {
                    newPl = new ProductLgbk(pr.getLgbk(), "", "", -1, false);
                } else if (pr.getHierarchy().length() > 0) {
                    newPl = new ProductLgbk("", pr.getHierarchy(), "", -1, false);
                }

                res.add(newPl);
                addItem(newPl);
            }
        }

        return res;
    }

    public int getFamilyIdByLgbk(ProductLgbk pl) {
//        System.out.println("founding using " + pl.getLgbk() + ", " + pl.getHierarchy());

        String lgbkP = pl.getLgbk();
        String hierarchyL;

        for (ProductLgbk plg : productLgbks) {
//            System.out.println("----------------------------------------------------");
//            System.out.println("analizing of " + plg.getLgbk() + ", " + plg.getHierarchy());

            if (plg.getHierarchy().trim().length() == 0) hierarchyL = ".*";
            else hierarchyL = plg.getHierarchy().replaceAll("\\*", ".*");
//            System.out.println("changing to " + plg.getLgbk() + ", " + hierarchyL);

            if (lgbkP.length() > 0 && plg.getLgbk().length() > 0) {
//                System.out.println("using both values");
//                System.out.println("comparing " + lgbkP + " vs " + plg.getLgbk() + " && " + pl.getHierarchy() + " vs " + hierarchyL);
//                System.out.println("result = " + (lgbkP.matches(plg.getLgbk()) && pl.getHierarchy().matches(hierarchyL)));

                if (lgbkP.matches(plg.getLgbk()) && pl.getHierarchy().matches(hierarchyL)) return plg.getFamilyId();
            } else if (lgbkP.length() == 0 && plg.getLgbk().length() == 0) {
//                System.out.println("using hierarchy value");
//                System.out.println("comparing " + pl.getHierarchy() + " vs " + hierarchyL);
//                System.out.println("result = " + (pl.getHierarchy().matches(hierarchyL)));

                if (pl.getHierarchy().matches(hierarchyL)) return plg.getFamilyId();
            }
        }

        return 0;
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
