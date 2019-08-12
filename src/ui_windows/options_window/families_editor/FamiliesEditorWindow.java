package ui_windows.options_window.families_editor;

import core.CoreModule;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.options_window.OptionsWindow;
import ui_windows.Mode;

import static ui_windows.Mode.*;

public class FamiliesEditorWindow extends OrdinalWindow {

    public FamiliesEditorWindow(Mode editorMode) {
        super(OptionsWindow.getStage(), Modality.APPLICATION_MODAL,
                editorMode, "familiesEditorWindow.fxml", "Направление");

        if (mode == EDIT) {//put data into fields
            TableView<ProductFamily> pft = CoreModule.getProductFamilies().getProductFamiliesTable().getTableView();
            pft.getSelectionModel().getSelectedItem().showInEditorWindow(rootAnchorPane);
        }

        applyProfileSimple(CoreModule.getUsers().getCurrentUser().getProfile().getFamilies());

        stage.show();
    }
}
