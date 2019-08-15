package ui_windows.options_window.product_lgbk;

import core.CoreModule;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.options_window.OptionsWindow;
import ui_windows.Mode;

import static ui_windows.Mode.*;

public class LgbkEditorWindow extends OrdinalWindow {

    public LgbkEditorWindow(Mode editorMode) {
        super(OptionsWindow.getStage(), Modality.APPLICATION_MODAL,
                editorMode, "lgbkEditorWindow.fxml", "LGBK / Иерархия");

        if (mode == ADD) {

        } else if (mode == EDIT) {//put data into fields

            TreeTableView<ProductLgbk> plt = CoreModule.getProductLgbks().getProductLgbksTable().getTableView();
            plt.getSelectionModel().getSelectedItem().getValue().showInEditorWindow(rootAnchorPane);

        } else if (mode == DELETE) {

        }

        applyProfileSimple(CoreModule.getUsers().getCurrentUser().getProfile().getFamilies());

        stage.show();
    }
}