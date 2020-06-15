package ui_windows.product.productEditorWindow.configNormsWindow;

import javafx.stage.Modality;
import javafx.stage.Stage;
import ui_windows.OrdinalWindow;
import ui_windows.product.MultiEditor;
import ui_windows.product.productEditorWindow.CertificateVerificationTable;

public class ConfigNormsWindow extends OrdinalWindow {
    public ConfigNormsWindow(Stage parentStage, MultiEditor multiEditor, CertificateVerificationTable certificateVerificationTable) {
        super(parentStage, Modality.APPLICATION_MODAL, mode, "/fxml/configNormsWindow.fxml",
                "Настройка проверки норм");

        ConfigNormsWindowController cnwc = loader.getController();
        cnwc.init(multiEditor, certificateVerificationTable);

        stage.show();
    }
}
