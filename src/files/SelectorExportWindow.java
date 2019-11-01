package files;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui_windows.OrdinalWindow;

public class SelectorExportWindow extends OrdinalWindow {
    @FXML
    AnchorPane apRoot;



    public SelectorExportWindow(Stage parentStage) {
        super(parentStage, Modality.APPLICATION_MODAL, null, "selectorExportWindow.fxml", "Выбор столбцов");

        stage.show();
    }
}
