package ui_windows.options_window.requirements_types_editor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

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
//        RequirementTypeEditorWindowActions.selectFolder();
    }


}
