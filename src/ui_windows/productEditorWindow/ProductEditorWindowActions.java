package ui_windows.productEditorWindow;

import core.CoreModule;
import database.ProductsDB;
import javafx.scene.layout.AnchorPane;
import ui_windows.main_window.Product;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import utils.ObjectsComparator;
import utils.Utils;

import java.util.ArrayList;

import static ui_windows.Mode.ADD;
import static ui_windows.Mode.EDIT;

public class ProductEditorWindowActions {

    public static Product getItem() {
        int index = CoreModule.getProducts().getTableView().getSelectionModel().getSelectedIndex();

        if (index > -1 && index < CoreModule.getProducts().getTableView().getItems().size()) {
            return CoreModule.getProducts().getTableView().getItems().get(index);
        } else return null;
    }

    public static void apply() {
        AnchorPane root = (AnchorPane) ProductEditorWindow.getStage().getScene().getRoot();
        Product pr = getItem();

        if (ProductEditorWindow.getMode() == ADD) {//save added new record

        } else if (ProductEditorWindow.getMode() == EDIT) {//save changes
            //comparing the changes, track in history

            Product changedProduct = new Product(root);

            //avoiding changing of automatically calculated family
            int id = CoreModule.getProductLgbks().getFamilyIdByLgbk(new ProductLgbk(pr.getLgbk(), pr.getHierarchy()));
            if (pr.getFamily() < 1 && changedProduct.getFamily() == id) changedProduct.setFamily(0);

            boolean productWasNeedAction = pr.isNeedaction() && !changedProduct.isNeedaction();

            ObjectsComparator comparator = new ObjectsComparator(pr, changedProduct, false,
                    "id", "article", "history", "lastchangedate", "dchain", "filename", "changecodes",
                    "productforprint", "lastimportcodes");

            if (comparator.getResult().trim().length() > 0) {//was changed
                pr.setHistory(pr.getHistory().concat(Utils.getDateTime()).concat(comparator.getResult()
                       .concat(", ").concat(CoreModule.getUsers().getCurrentUser().getSurname()).concat("\n")));
                System.out.println("result = " + comparator.getResult());

                if (productWasNeedAction) pr.setChangecodes("");

                ArrayList<Product> productsToUpdate = new ArrayList<>();
                productsToUpdate.add(pr);

                new Thread(() -> new ProductsDB().updateData(new ArrayList<Product>(productsToUpdate))).start(); //update in DB

                CoreModule.getProducts().getTableView().refresh();
            }

            ProductEditorWindow.getStage().close();
        }
    }
}
