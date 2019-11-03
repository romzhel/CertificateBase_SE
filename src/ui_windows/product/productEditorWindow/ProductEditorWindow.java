package ui_windows.product.productEditorWindow;

import core.CoreModule;
import javafx.collections.ObservableList;
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
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.profile_editor.SimpleRight;
import ui_windows.options_window.user_editor.User;
import ui_windows.product.MultiEditor;
import ui_windows.product.Product;
import utils.Utils;

import static ui_windows.Mode.*;
import static ui_windows.options_window.profile_editor.SimpleRight.*;

public class ProductEditorWindow extends OrdinalWindow {

    public ProductEditorWindow(Mode editorMode, ObservableList<Product> selectedProducts) {
        super(MainWindow.getMainStage(), Modality.APPLICATION_MODAL,
                editorMode, "productEditorWindow.fxml", "");

        ProductEditorWindowController pewc = (ProductEditorWindowController) loader.getController();
        rootAnchorPane = pewc.apRoot;

        if (mode == ADD) {

        } else if (mode == EDIT) {//put data into fields

            if (selectedProducts.size() == 1) {
                Product product = selectedProducts.get(0);
                String lastUpdate = product.getLastChangeDate() == null ? "нет данных" : product.getLastChangeDate();
                stage.setTitle("Продукт " + product.getArticle() + " (посл. обновление: " + lastUpdate + ")");
                product.displayInEditorWindow(rootAnchorPane);
            } else if (selectedProducts.size() > 1) {
                stage.setTitle("Элементов выбрано: " + selectedProducts.size());

                MultiEditor multiEditor = new MultiEditor(selectedProducts, pewc);
                pewc.setMultiEditor(multiEditor);
                ProductEditorWindowActions.setMultiEditor(multiEditor);
            }

        } else if (mode == DELETE) {

        }

        applyProfileSimple(CoreModule.getUsers().getCurrentUser().getProfile().getProducts());

        stage.setResizable(true);
        stage.show();
    }

    @Override
    public void applyProfileSimple(SimpleRight sr) {
        User user = CoreModule.getUsers().getCurrentUser();
        Product prd = CoreModule.getProducts().getSelectedItem();

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
}
