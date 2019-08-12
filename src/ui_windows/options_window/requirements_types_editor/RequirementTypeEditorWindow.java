package ui_windows.options_window.requirements_types_editor;

import core.CoreModule;
import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.Mode;
import ui_windows.options_window.OptionsWindow;

import static ui_windows.Mode.*;

public class RequirementTypeEditorWindow extends OrdinalWindow {

    public RequirementTypeEditorWindow(Mode editorMode) {
        super(OptionsWindow.getStage(), Modality.APPLICATION_MODAL,
                editorMode, "requirementTypeEditorWindow.fxml", "Регламент или норма");

        if (mode == EDIT) {//put data into fields
            RequirementTypeEditorWindowActions.displayData();
        }

        applyProfileSimple(CoreModule.getUsers().getCurrentUser().getProfile().getCertificates());

        stage.show();
    }
}
