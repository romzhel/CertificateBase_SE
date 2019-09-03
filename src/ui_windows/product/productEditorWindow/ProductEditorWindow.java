package ui_windows.product.productEditorWindow;

import core.CoreModule;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.Mode;
import ui_windows.main_window.MainWindow;
import ui_windows.product.Product;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.profile_editor.SimpleRight;
import ui_windows.options_window.user_editor.User;
import utils.Utils;

import java.util.ArrayList;

import static ui_windows.Mode.*;
import static ui_windows.options_window.profile_editor.SimpleRight.*;

public class ProductEditorWindow extends OrdinalWindow {

    public ProductEditorWindow(Mode editorMode, ObservableList<Product> selectedProducts){
        super(MainWindow.getMainStage(), Modality.APPLICATION_MODAL,
                editorMode, "productEditorWindow.fxml", "");

        ArrayList<Product> selectedItems = new ArrayList<>(selectedProducts);



        if (mode == ADD) {

        } else if (mode == EDIT) {//put data into fields

            if (selectedItems.size() == 1) {
                Product product = selectedItems.get(0);
                String lastUpdate = product.getLastChangeDate() == null ? "нет данных" : product.getLastChangeDate();
                stage.setTitle("Продукт " + product.getArticle() + " (посл. обновление: " + lastUpdate + ")");

                CoreModule.getProducts().getSelectedItem().displayInEditorWindow(rootAnchorPane);
            } else if (selectedItems.size() > 1) {
                stage.setTitle("Элементов выбрано: " + selectedItems.size());




            }














        } else if (mode == DELETE){

        }

        applyProfileSimple(CoreModule.getUsers().getCurrentUser().getProfile().getProducts());

        stage.show();
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
