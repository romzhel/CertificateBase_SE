package ui_windows.options_window.certificates_editor;

import javafx.stage.Modality;
import ui_windows.Mode;
import ui_windows.OrdinalWindow;
import ui_windows.options_window.OptionsWindow;
import ui_windows.options_window.user_editor.Users;
import utils.Utils;

import static ui_windows.Mode.*;
import static ui_windows.options_window.profile_editor.SimpleRight.FULL;
import static ui_windows.options_window.profile_editor.SimpleRight.OWN;

public class CertificateEditorWindow extends OrdinalWindow<CertificateEditorWindowController> {

    public CertificateEditorWindow(Certificate certificate, Mode editorMode) {
        super(OptionsWindow.getStage(), Modality.APPLICATION_MODAL,
                editorMode, "/fxml/certificateEditorWindow.fxml", "Сертификат");

        CertificateEditorWindowActions editorWindowActions = null;

        if (mode == ADD) {
//            Utils.clearControls((AnchorPane)root);
            editorWindowActions = new CertificateEditorWindowActions();
            editorWindowActions.init();
        } else if (mode == EDIT) {//put data into fields
            editorWindowActions = new CertificateEditorWindowActions(certificate);
            editorWindowActions.displayData();
        } else if (mode == DELETE) {
//            CertificateEditorWindowActions.deleteData();
        }

        controller.setEditorWindowActions(editorWindowActions);

        checkingOwnRights();

        stage.setResizable(true);
        stage.show();
        stage.setMinHeight(stage.getHeight());
        stage.setMinWidth(stage.getWidth());
    }

    private void checkingOwnRights() {
        boolean editorRightsFull = Users.getInstance().getCurrentUser().getProfile().getCertificates() != FULL;
        boolean editorRightsOwn = Users.getInstance().getCurrentUser().getProfile().getCertificates() != OWN;
        Utils.disableEditing(rootAnchorPane, editorRightsFull && editorRightsOwn);//disable buttons and menus (HIDE & DISPLAY)

        boolean ownRights = Users.getInstance().getCurrentUser().getProfile().getCertificates() == OWN;
        if (ownRights && mode == EDIT) {
            int certificateUserId = CertificatesTable.getInstance().getTableView().getSelectionModel().getSelectedItem().getUserId();
            int userId = Users.getInstance().getCurrentUser().getId();
            boolean existingUser = Users.getInstance().userExistsById(certificateUserId);

            if (certificateUserId > 0 && existingUser && userId != certificateUserId) {
                Utils.disableEditing(rootAnchorPane, true);
            }
        }
    }
}
