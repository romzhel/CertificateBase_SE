package ui_windows.request;

import core.CoreModule;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui_windows.OrdinalWindow;
import ui_windows.main_window.MainWindow;
import ui_windows.product.Product;

import java.util.ArrayList;

public class RequestWindow extends OrdinalWindow {
    private RequestWindowController controller;

    public RequestWindow() {
        super(MainWindow.getMainStage(), Modality.APPLICATION_MODAL, null, "certificateRequestWindow.fxml",
                "Ввод данных");

        controller = (RequestWindowController) loader.getController();

        stage.show();
    }


}
