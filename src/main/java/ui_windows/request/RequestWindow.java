package ui_windows.request;

import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.main_window.MainWindow;

public class RequestWindow extends OrdinalWindow {
    private RequestWindowController controller;

    public RequestWindow() {
        super(MainWindow.getMainStage(), Modality.APPLICATION_MODAL, null, "/fxml/certificateRequestWindow.fxml",
                "Ввод данных");

        controller = (RequestWindowController) loader.getController();

        stage.show();
    }


}
