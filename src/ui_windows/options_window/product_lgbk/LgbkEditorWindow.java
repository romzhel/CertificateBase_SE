package ui_windows.options_window.product_lgbk;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.stage.Modality;
import ui_windows.Mode;
import ui_windows.OrdinalWindow;
import ui_windows.options_window.OptionsWindow;
import ui_windows.options_window.user_editor.Users;

import static ui_windows.Mode.*;

public class LgbkEditorWindow extends OrdinalWindow {

    public LgbkEditorWindow(Mode editorMode) {
        super(OptionsWindow.getStage(), Modality.APPLICATION_MODAL,
                editorMode, "lgbkEditorWindow.fxml", "LGBK / Иерархия");

        if (mode == ADD) {

        } else if (mode == EDIT) {//put data into fields

            TreeTableView<ProductLgbk> plt = ProductLgbks.getInstance().getProductLgbksTable().getTableView();
            TreeItem<ProductLgbk> selectedItem = plt.getSelectionModel().getSelectedItem();

            if (selectedItem != null && selectedItem.getValue() != null) {
                selectedItem.getValue().showInEditorWindow(rootAnchorPane);
            }

        } else if (mode == DELETE) {

        }

        applyProfileSimple(Users.getInstance().getCurrentUser().getProfile().getFamilies());

        stage.show();
    }
}