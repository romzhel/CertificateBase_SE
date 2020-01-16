package ui_windows.product.productEditorWindow;

import core.CoreModule;
import database.ProductsDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ui_windows.main_window.file_import_window.ColumnsMapper;
import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.main_window.file_import_window.singleProductsComparator;
import ui_windows.product.MultiEditor;
import ui_windows.product.Product;
import utils.Utils;

import java.util.ArrayList;

import static ui_windows.Mode.ADD;
import static ui_windows.Mode.EDIT;
import static ui_windows.product.data.DataItem.*;

public class ProductEditorWindowActions {
    private static CertificateVerificationTable certificateVerificationTable;
    private static MultiEditor multiEditor;

    public static Product getEditedItem() {
        int index = CoreModule.getProducts().getTableView().getSelectionModel().getSelectedIndices().get(0);

        if (index > -1 && index < CoreModule.getProducts().getTableView().getItems().size()) {
            return CoreModule.getProducts().getTableView().getItems().get(index);
        } else return null;
    }

    public static void apply(AnchorPane root, MultiEditor multiEditor) {
        Product pr;
        ArrayList<Product> productsToUpdate = new ArrayList<>();

        if (ProductEditorWindow.getMode() == ADD) {//save added new record

        } else if (ProductEditorWindow.getMode() == EDIT) {//save changes
            if (multiEditor == null) {
                //comparing the changes, track in history
                pr = getEditedItem();
                Product changedProduct = new Product(root);

                //avoiding changing of automatically calculated family
                if (pr.getFamily_id() < 1 && pr.getProductFamily() != null && changedProduct.getFamily_id() == pr.getProductFamily().getId()) {
                    changedProduct.setFamily_id(0);
                }

                ObservableList<FileImportParameter> parameters = FXCollections.observableArrayList();
                parameters.add(new FileImportParameter("", DATA_ORDER_NUMBER, true, false, -1, false));
                parameters.add(new FileImportParameter("", DATA_DESCRIPTION_RU, true, true, -1, false));
                parameters.add(new FileImportParameter("", DATA_DESCRIPTION_EN, true, true, -1, false));
                parameters.add(new FileImportParameter("", DATA_TYPE, true, true, -1, false));
                parameters.add(new FileImportParameter("", DATA_FAMILY_ID, true, true, -1, false));
                parameters.add(new FileImportParameter("", DATA_MANUAL_FILE, true, false, -1, false));
                parameters.add(new FileImportParameter("", DATA_REPLACEMENT, true, false, -1, false));
                parameters.add(new FileImportParameter("", DATA_COMMENT, true, false, -1, false));
                parameters.add(new FileImportParameter("", DATA_IS_IN_PRICE, true, true, -1, false));

                /*LgbkAndParent lap = CoreModule.getProductLgbkGroups().getLgbkAndParent(new ProductLgbk(pr));
                boolean globalDisabled = lap.getLgbkItem().isNotUsed() || lap.getLgbkParent().isNotUsed();
                if (!globalDisabled) {
                    fiti.add(new FileImportTableItem("", "notused", true, true, -1, false));
                }*/

                ColumnsMapper mapper = new ColumnsMapper();
                singleProductsComparator comparator = new singleProductsComparator(pr, changedProduct, true, parameters.toArray(new FileImportParameter[]{}));

                if (comparator.getResult().isNeedUpdateInDB()) {//was changed
                    String oldHistory = pr.getHistory() == null ? "" : pr.getHistory().trim();
                    if (oldHistory.isEmpty()) {
                        pr.setHistory(oldHistory.concat(Utils.getDateTime()).concat(comparator.getResult().getHistoryComment()
                                .concat(", ").concat(CoreModule.getUsers().getCurrentUser().getSurname())));
                    } else {
                        pr.setHistory(oldHistory.concat("|").concat(Utils.getDateTime()).concat(comparator.getResult().getHistoryComment()
                                .concat(", ").concat(CoreModule.getUsers().getCurrentUser().getSurname())));
                    }

                    System.out.println("result = " + comparator.getResult().getHistoryComment());

                    productsToUpdate.add(pr);
                }
            } else {
                if (multiEditor.checkAndApplyChanges()) {
                    productsToUpdate.addAll(multiEditor.getEditedItems());
                }
            }
//            new Thread(() -> new ProductsDB().updateData(new ArrayList<Product>(productsToUpdate))).start(); //update in DB
            boolean saveToDbResult = new ProductsDB().updateData(productsToUpdate); //update in DB

            if (saveToDbResult) {
                CoreModule.getFilter().apply();
                ((Stage) root.getScene().getWindow()).close();
            }
        }
    }

    public static void setMultiEditor(MultiEditor multiEditor) {
        ProductEditorWindowActions.multiEditor = multiEditor;
    }
}
