package ui_windows.options_window.requirements_types_editor;

import javafx.stage.Modality;
import ui_windows.Mode;
import ui_windows.OrdinalWindow;
import ui_windows.options_window.OptionsWindow;
import ui_windows.options_window.user_editor.Users;

import static ui_windows.Mode.EDIT;

public class RequirementTypeEditorWindow extends OrdinalWindow {

    public RequirementTypeEditorWindow(Mode editorMode) {
        super(OptionsWindow.getStage(), Modality.APPLICATION_MODAL,
                editorMode, "requirementTypeEditorWindow.fxml", "Регламент или норма");

        if (mode == EDIT) {//put data into fields
            RequirementTypeEditorWindowActions.displayData();
        }

        applyProfileSimple(Users.getInstance().getCurrentUser().getProfile().getCertificates());

        stage.show();
    }
}
