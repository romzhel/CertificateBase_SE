package ui_windows.options_window.families_editor;

import javafx.scene.control.TableView;
import javafx.stage.Modality;
import ui_windows.Mode;
import ui_windows.OrdinalWindow;
import ui_windows.options_window.OptionsWindow;
import ui_windows.options_window.user_editor.Users;

import static ui_windows.Mode.EDIT;

public class FamiliesEditorWindow extends OrdinalWindow {

    public FamiliesEditorWindow(Mode editorMode) {
        super(OptionsWindow.getStage(), Modality.APPLICATION_MODAL,
                editorMode, "/fxml/familiesEditorWindow.fxml", "Направление");

        if (mode == EDIT) {//put data into fields
            TableView<ProductFamily> pft = ProductFamilies.getInstance().getProductFamiliesTable().getTableView();
            pft.getSelectionModel().getSelectedItem().showInEditorWindow(rootAnchorPane);
        }

        applyProfileSimple(Users.getInstance().getCurrentUser().getProfile().getFamilies());

        stage.show();
    }
}
