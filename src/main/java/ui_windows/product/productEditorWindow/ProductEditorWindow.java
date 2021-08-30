package ui_windows.product.productEditorWindow;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Modality;
import ui_windows.Mode;
import ui_windows.OrdinalWindow;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.profile_editor.SimpleRight;
import ui_windows.options_window.user_editor.User;
import ui_windows.options_window.user_editor.Users;
import ui_windows.product.Product;
import ui_windows.product.Products;
import utils.Utils;

import java.util.Arrays;
import java.util.List;

import static ui_windows.options_window.profile_editor.SimpleRight.*;

public class ProductEditorWindow extends OrdinalWindow<ProductEditorWindowController> {

    public ProductEditorWindow(Mode editorMode, List<Product> selectedProducts) {
        super(MainWindow.getMainStage(), Modality.APPLICATION_MODAL,
                editorMode, "/fxml/productEditorWindow.fxml", "");

        ProductEditorWindowController pewc = loader.getController();
        rootAnchorPane = pewc.apRoot;

        if (selectedProducts.size() == 1) {
            Product product = selectedProducts.get(0);
            String lastUpdate = product.getLastChangeDate() == null ? "нет данных" : product.getLastChangeDate();
            stage.setTitle("Продукт " + product.getArticle() + " (посл. обновление: " + lastUpdate + ")");
//            product.displayInEditorWindow(pewc);
        } else if (selectedProducts.size() > 1) {
            stage.setTitle("Элементов выбрано: " + selectedProducts.size());
        }

        applyProfile(Users.getInstance().getCurrentUser().getProfile().getProducts(), pewc);

        stage.setResizable(true);
        stage.show();
    }

    public void applyProfile(SimpleRight sr, ProductEditorWindowController controller) {
        applyProfileSimple(Users.getInstance().getCurrentUser().getProfile().getProducts());
        controller.cbxBlocked.setDisable(sr != FULL);
        controller.taCommentsPrice.setEditable(sr == FULL);
        controller.getPriceBox().setButtonDisabled(sr != FULL);
        Arrays.stream(controller.getBoxes()).forEach(box -> box.setDisable(sr != FULL));
    }

    @Override
    public void applyProfileSimple(SimpleRight sr) {
        User user = Users.getInstance().getCurrentUser();
        Product prd = Products.getInstance().getSelectedItem();

        ProductFamily pf = prd.getProductFamily();
        boolean profileRights = sr == DISPLAY;
        boolean familyOfUser = sr == OWN && pf != null && (user.getProductFamilies().contains(pf.getName()));
        boolean adminRights = sr == FULL;

        if ((profileRights || !familyOfUser) && !adminRights) Utils.disableMenuItemsButton(rootAnchorPane, "btnApply");
        else setSaveAccelerator();
    }

    private void setSaveAccelerator() {
        Scene scene = stage.getScene();
        if (scene == null) {
            System.out.println("scene == null");
            throw new IllegalArgumentException("setSaveAccelerator must be called when a button is attached to a scene");
        }

        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN),
                new Runnable() {
                    @FXML
                    public void run() {
                        ((ProductEditorWindowController) loader.getController()).apply();
                    }
                }
        );
    }

    public ProductEditorWindowController getController() {
        return loader.getController();
    }
}
