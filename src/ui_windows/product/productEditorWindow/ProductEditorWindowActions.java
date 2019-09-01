package ui_windows.product.productEditorWindow;

import core.CoreModule;
import database.ProductsDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ui_windows.main_window.file_import_window.ColumnsMapper2;
import ui_windows.main_window.file_import_window.FileImportTableItem;
import ui_windows.main_window.file_import_window.ObjectsComparator2;
import ui_windows.options_window.certificates_editor.certificate_content_editor.certificatesChecker.CertificateVerificationItem;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.Product;
import utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;

import static ui_windows.Mode.ADD;
import static ui_windows.Mode.EDIT;
import static ui_windows.main_window.file_import_window.NamesMapping.DESC_ORDER_NUMBER;

public class ProductEditorWindowActions {
    public static HashSet<Integer> existingNorms = new HashSet<>();
    public static HashSet<Integer> needNorms = new HashSet<>();
    private static TableView<CertificateVerificationItem> tableView;

    public static Product getEditedItem() {
        int index = CoreModule.getProducts().getTableView().getSelectionModel().getSelectedIndex();

        if (index > -1 && index < CoreModule.getProducts().getTableView().getItems().size()) {
            return CoreModule.getProducts().getTableView().getItems().get(index);
        } else return null;
    }

    public static void apply(Stage stage) {
        AnchorPane root = (AnchorPane) stage.getScene().getRoot();
        Product pr = getEditedItem();

        if (ProductEditorWindow.getMode() == ADD) {//save added new record

        } else if (ProductEditorWindow.getMode() == EDIT) {//save changes
            //comparing the changes, track in history

            Product changedProduct = new Product(root);

            //avoiding changing of automatically calculated family
            int id = CoreModule.getProductLgbks().getFamilyIdByLgbk(new ProductLgbk(pr.getLgbk(), pr.getHierarchy()));
            if (pr.getFamily() < 1 && changedProduct.getFamily() == id) changedProduct.setFamily(0);

            boolean productWasNeedAction = pr.isNeedaction() && !changedProduct.isNeedaction();

            /*ObjectsComparator comparator = new ObjectsComparator(pr, changedProduct, false,
                    "id", "article", "history", "lastchangedate", "dchain", "filename", "changecodes",
                    "productforprint", "lastimportcodes", "minorder", "packetsize", "leadtime", "weight", "localprice");*/

            ObservableList<FileImportTableItem> fiti = FXCollections.observableArrayList();
            fiti.add(new FileImportTableItem("", DESC_ORDER_NUMBER, true, false, -1, false));
            fiti.add(new FileImportTableItem("", "descriptionru", true, false, -1, false));
            fiti.add(new FileImportTableItem("", "descriptionen", true, false, -1, false));
            fiti.add(new FileImportTableItem("", "type_id", true, true, -1, false));
            fiti.add(new FileImportTableItem("", "family", true, true, -1, false));
            fiti.add(new FileImportTableItem("", "fileName", true, false, -1, false));
            fiti.add(new FileImportTableItem("", "replacement", true, false, -1, false));
            fiti.add(new FileImportTableItem("", "comments", true, false, -1, false));
            fiti.add(new FileImportTableItem("", "price", true, true, -1, false));
            fiti.add(new FileImportTableItem("", "archive", true, true, -1, false));
            fiti.add(new FileImportTableItem("", "needaction", true, true, -1, false));
            fiti.add(new FileImportTableItem("", "notused", true, true, -1, false));

            ColumnsMapper2 mapper = new ColumnsMapper2();
            ObjectsComparator2 comparator = new ObjectsComparator2(pr, changedProduct, false, mapper.getFieldsForImport(fiti));

            if (comparator.getResult().isNeedUpdateInDB()) {//was changed
                String oldHistory = pr.getHistory().trim();
                if (oldHistory.isEmpty()) {
                    pr.setHistory(oldHistory.concat(Utils.getDateTime()).concat(comparator.getResult().getHistoryComment()
                            .concat(", ").concat(CoreModule.getUsers().getCurrentUser().getSurname())));
                } else {
                    pr.setHistory(oldHistory.concat("|").concat(Utils.getDateTime()).concat(comparator.getResult().getHistoryComment()
                            .concat(", ").concat(CoreModule.getUsers().getCurrentUser().getSurname())));
                }

                System.out.println("result = " + comparator.getResult().getHistoryComment());

                if (productWasNeedAction) pr.setChangecodes("");

                ArrayList<Product> productsToUpdate = new ArrayList<>();
                productsToUpdate.add(pr);

                new Thread(() -> new ProductsDB().updateData(new ArrayList<Product>(productsToUpdate))).start(); //update in DB

            }

            CoreModule.getProducts().getTableView().refresh();
            stage.close();
        }
    }

    public static void fillCertificateVerificationTable() {
        CoreModule.getCertificates().getCertificatesChecker().check(getEditedItem());
        tableView.getItems().clear();
        tableView.getItems().addAll(CoreModule.getCertificates().getCertificatesChecker().getResultTableItems());
        tableView.refresh();
    }

    public static TableView<CertificateVerificationItem> getTableView() {
        return tableView;
    }

    public static void setTableView(TableView<CertificateVerificationItem> tableView) {
        ProductEditorWindowActions.tableView = tableView;
    }


}
