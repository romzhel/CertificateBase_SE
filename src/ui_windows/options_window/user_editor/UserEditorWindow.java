package ui_windows.options_window.user_editor;

import javafx.stage.Modality;
import ui_windows.Mode;
import ui_windows.OrdinalWindow;
import ui_windows.options_window.OptionsWindow;

import static ui_windows.Mode.*;

public class UserEditorWindow extends OrdinalWindow {

    public UserEditorWindow(Mode editorMode) {
        super(OptionsWindow.getStage(), Modality.APPLICATION_MODAL,
                editorMode, "userEditorWindow.fxml", "Пользователь");

        if (mode == ADD) {

        } else if (mode == EDIT) {//put data into fields
            Users.getInstance().getTable().getSelectedItem().displayInEditorWindow(UserEditorWindow.getRootAnchorPane());
        } else if (mode == DELETE) {

        }

        applyProfileSimple(Users.getInstance().getCurrentUser().getProfile().getUsers());

        stage.show();
    }
}
