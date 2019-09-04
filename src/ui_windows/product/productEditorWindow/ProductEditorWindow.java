package ui_windows.product.productEditorWindow;

import core.CoreModule;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.Mode;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.certificates_editor.certificate_content_editor.certificatesChecker.CertificateVerificationItem;
import ui_windows.product.MultiEditor;
import ui_windows.product.Product;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.profile_editor.SimpleRight;
import ui_windows.options_window.user_editor.User;
import ui_windows.product.ProductTypes;
import utils.Utils;

import java.util.ArrayList;
import java.util.TreeSet;

import static ui_windows.Mode.*;
import static ui_windows.options_window.profile_editor.SimpleRight.*;

public class ProductEditorWindow extends OrdinalWindow {

    public ProductEditorWindow(Mode editorMode, ObservableList<Product> selectedProducts){
        super(MainWindow.getMainStage(), Modality.APPLICATION_MODAL,
                editorMode, "productEditorWindow.fxml", "");

//        ArrayList<Product> selectedItems = new ArrayList<>(selectedProducts);

        if (mode == ADD) {

        } else if (mode == EDIT) {//put data into fields

            if (selectedProducts.size() == 1) {
                Product product = selectedProducts.get(0);
                String lastUpdate = product.getLastChangeDate() == null ? "нет данных" : product.getLastChangeDate();
                stage.setTitle("Продукт " + product.getArticle() + " (посл. обновление: " + lastUpdate + ")");

                CoreModule.getProducts().getSelectedItem().displayInEditorWindow(rootAnchorPane);
            } else if (selectedProducts.size() > 1) {
                stage.setTitle("Элементов выбрано: " + selectedProducts.size());

                MultiEditor multiEditor = new MultiEditor(selectedProducts);
                ((ProductEditorWindowController)loader.getController()).setMultiEditor(multiEditor);
                ProductEditorWindowActions.setMultiEditor(multiEditor);
            }


        } else if (mode == DELETE){

        }

        ProductEditorWindowActions.fillCertificateVerificationTable();
        fillProductTypesCombo();

        applyProfileSimple(CoreModule.getUsers().getCurrentUser().getProfile().getProducts());

        stage.show();
    }

    public static void fillProductTypesCombo() {
        ProductEditorWindowController pewc = ProductEditorWindow.getLoader().getController();
        pewc.cbType.getItems().clear();
        pewc.cbType.getItems().add(ProductTypes.NO_SELECTED);
        TreeSet<String> prodTypeNames = new TreeSet<>();
        for (CertificateVerificationItem cvi : pewc.tvCertVerification.getItems()) {
            if (cvi != null && !cvi.getProdType().isEmpty()) {
                prodTypeNames.add(cvi.getProdType());
            }
        }
        pewc.cbType.getItems().addAll(prodTypeNames);
    }

    @Override
    public void applyProfileSimple(SimpleRight sr){
        Product prd = CoreModule.getProducts().getSelectedItem();
        User user = CoreModule.getUsers().getCurrentUser();

        int familyId;
        if (prd.getFamily() > 0) {
            familyId = prd.getFamily();
        } else {
            familyId = CoreModule.getProductLgbks().getFamilyIdByLgbk(new ProductLgbk(prd.getLgbk(), prd.getHierarchy()));
        }
        String famName = CoreModule.getProductFamilies().getFamilyNameById(familyId);

        boolean profileRights = (sr == DISPLAY);
        boolean familyOfUser = famName.isEmpty() ? false : user.getProductFamilies().contains(famName) && sr == OWN;
        boolean adminRights = (sr == FULL);

        if ((profileRights || !familyOfUser) && !adminRights) Utils.disableMenuItemsButton(rootAnchorPane, "btnApply");
    }
}
