package ui_windows.product;

import core.CoreModule;
import database.ProductsDB;
import javafx.scene.control.TableView;
import ui_windows.main_window.MainWindow;
import utils.Utils;

import java.util.ArrayList;

public class Products {
    private ArrayList<Product> products;
    private TableView<Product> tableView;

    public Products() {
        products = new ArrayList<>();
    }

    public ArrayList<Product> getItems() {
        return products;
    }

    public void setItems(ArrayList<Product> list) {
        products.clear();
        products.addAll(list);
    }

    public Product getItemByMaterialOrArticle(String name) {
        String nameT = Utils.toEN(name.replaceAll("\\-", "").replaceAll("\\s", "").toUpperCase());
        String materialT;
        String articleT;

        for (Product pr : products) {
            materialT = pr.getMaterial().replaceAll("\\-", "").replaceAll("\\s", "");
            articleT = pr.getArticle().replaceAll("\\-", "").replaceAll("\\s", "");

            if (materialT.equals(nameT) || articleT.equals(nameT)) return pr;
        }

        return null;
    }

    public Products getFromDB() {
        products = new ProductsDB().getData();
        return this;
    }

    public TableView<Product> getTableView() {
        return tableView;
    }

    public void setTableView(TableView<Product> tableView) {
        this.tableView = tableView;
    }

    public boolean isProductTypeIsUsed(String type) {
        for (Product pr : products) {
            if (CoreModule.getProductTypes().getTypeById(pr.getType_id()).equals(type)) return true;
        }
        return false;
    }

    public Product getSelectedItem() {
        int index = tableView.getSelectionModel().getSelectedIndex();

        if (index >= 0) return tableView.getItems().get(index);
        else return null;
    }

    public ArrayList<Product> removeDoubles(ArrayList<Product>... list) {
        ArrayList<Product> original;
        ArrayList<Product> result;
        ArrayList<Product> temp = new ArrayList<>();

        if (list.length > 0) {
            original = new ArrayList<>(list[0]);
            result = new ArrayList<>(list[0]);
        } else {
            original = new ArrayList<>(products);
            result = new ArrayList<>(products);
        }

        for (Product obj : original) {
            for (Product compObj : result) {
                if (obj.getId() != compObj.getId() && obj.getMaterial().replaceAll("\\-", "")
                        .equals(compObj.getMaterial().replaceAll("\\-", "")))
                    temp.add(compObj);
            }
        }

        for (Product pr : temp) result.remove(pr);
        temp.clear();

        return result;
    }

    public ArrayList<Product> getDoubles(ArrayList<Product>... itemsList) {
        ArrayList<Product> doubles = new ArrayList<>();
        ArrayList<Product> items = itemsList.length > 0 ? new ArrayList<>(itemsList[0]) : new ArrayList<>(products);
        ArrayList<Product> items2 = new ArrayList<>(items);

        for (Product pr : items) {
            for (Product pr2 : items2) {
                if (!pr.equals(pr2) && pr.getMaterial().replaceAll("\\-", "").
                        equals(pr2.getMaterial().replaceAll("\\-", ""))) {

                    doubles.add(pr);
//                    doubles.add(pr2);

                    break;
                }
            }
            MainWindow.setProgress((double) items.indexOf(pr) / (double) items.size());
        }

        MainWindow.setProgress(0.0);

        return doubles;
    }

    public ArrayList<Product> resetLastImportCodes() {
        System.out.println("clearing last import results...");

        ArrayList<Product> changedItems = new ArrayList<>();

        for (Product product : products) {
            if (!product.getLastImportcodes().isEmpty()) {
                product.setLastImportcodes("");
                changedItems.add(product);
            }
        }

        return changedItems;
    }

    public ArrayList<Product> getChangedPositions() {
        ArrayList<Product> result = new ArrayList<>();

        for (Product product : products) {
            if (!product.getLastImportcodes().isEmpty()) result.add(product);
        }

        return result;
    }

/*    public ProductsDB getDb() {
        return db;
    }*/

/*    public void setDb(ProductsDB db) {
        this.db = db;
    }*/
}
