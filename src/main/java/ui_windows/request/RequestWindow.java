package ui_windows.request;

import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.main_window.MainWindow;

public class RequestWindow extends OrdinalWindow<RequestWindowController> {

    public RequestWindow() {
        super(MainWindow.getMainStage(), Modality.APPLICATION_MODAL, null, "/fxml/certificateRequestWindow.fxml",
                "Ввод данных");
    }

    public String showAndGetValues() {
        stage.showAndWait();
        return controller.taData.getText();
    }
}
