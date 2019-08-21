package ui_windows.productEditorWindow;

import core.CoreModule;
import database.ProductsDB;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ui_windows.main_window.Product;
import ui_windows.options_window.certificates_editor.certificate_content_editor.certificatesChecker.CertificateVerificationItem;
import ui_windows.options_window.product_lgbk.LgbkAndParent;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import utils.ObjectsComparator;
import utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;

import static ui_windows.Mode.ADD;
import static ui_windows.Mode.EDIT;

public class ProductEditorWindowActions {
    private static TableView<CertificateVerificationItem> tableView;
    public static HashSet<Integer> existingNorms = new HashSet<>();
    public static HashSet<Integer> needNorms = new HashSet<>();

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

            stage.close();
        }
    }

    public static void fillCertificateVerificationTable(){
        ArrayList<CertificateVerificationItem> existingCerts = CoreModule.getCertificates().checkCertificates(getEditedItem());
        tableView.getItems().clear();
        tableView.getItems().addAll(existingCerts);

//        HashSet<Integer> existingNorms = new HashSet<>();
        for (CertificateVerificationItem cv : existingCerts) {
            existingNorms.addAll(CoreModule.getRequirementTypes().getReqTypesIdsALbyShortNamesEnum(cv.getNorm()));
        }

//        HashSet<Integer> needNorms = new HashSet<>();
        ArrayList<Integer> productNorms = new ArrayList<>();
        productNorms.addAll(getEditedItem().getNormsList().getIntegerItems());
        if (getEditedItem().getNormsMode() == NormsList.ADD_TO_GLOBAL) {
            LgbkAndParent lgbkAndParent = CoreModule.getProductLgbkGroups().getLgbkAndParent(
                    new ProductLgbk(getEditedItem().getLgbk(), getEditedItem().getHierarchy()));
            needNorms.addAll(CoreModule.getProductLgbkGroups().getRootNode().getNormsList().getIntegerItems());
            needNorms.addAll(lgbkAndParent.getLgbkParent().getNormsList().getIntegerItems());
            needNorms.addAll(lgbkAndParent.getLgbkItem().getNormsList().getIntegerItems());
        }
        needNorms.removeAll(existingNorms);
        productNorms.removeAll(existingNorms);

        for (int normIndex : needNorms) {
            tableView.getItems().add(new CertificateVerificationItem(CoreModule.getRequirementTypes().getRequirementByID(normIndex).getShortName()));
        }

        for (int normIndex : productNorms) {
            tableView.getItems().add(new CertificateVerificationItem(CoreModule.getRequirementTypes().getRequirementByID(normIndex).getShortName()));
        }

        tableView.refresh();
    }

    public static TableView<CertificateVerificationItem> getTableView() {
        return tableView;
    }

    public static void setTableView(TableView<CertificateVerificationItem> tableView) {
        ProductEditorWindowActions.tableView = tableView;
    }
}
