package ui_windows.options_window.requirements_types_editor;

import core.CoreModule;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

import static ui_windows.options_window.profile_editor.SimpleRight.*;

public class RequirementTypeEditorWindowController implements Initializable {

    @FXML
    Button btnApply;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        btnApply.setDisable(CoreModule.getUsers().getCurrentUser().getProfile().getCertificates() != FULL);
    }


    public void close() {
        RequirementTypeEditorWindowActions.close();
    }

    public void apply() {
        RequirementTypeEditorWindowActions.apply();
    }

    public void selectFolder(){
        RequirementTypeEditorWindowActions.selectFolder();
    }


}
