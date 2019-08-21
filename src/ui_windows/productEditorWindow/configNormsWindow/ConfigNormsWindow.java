package ui_windows.productEditorWindow.configNormsWindow;

import javafx.stage.Modality;
import javafx.stage.Stage;
import ui_windows.OrdinalWindow;

public class ConfigNormsWindow extends OrdinalWindow {
    public ConfigNormsWindow(Stage parentStage) {
        super(parentStage, Modality.APPLICATION_MODAL, mode, "configNormsWindow.fxml",
                "Настройка проверки норм");

        stage.show();
    }
}
