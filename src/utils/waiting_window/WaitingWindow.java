package utils.waiting_window;

import javafx.stage.Modality;
import javafx.stage.Stage;
import ui_windows.OrdinalWindow;

public class WaitingWindow extends OrdinalWindow {
    public WaitingWindow(Stage parentStage) {
        super(parentStage, Modality.APPLICATION_MODAL, null, "waitingWindow.fxml", "Выполнение запроса");

        stage.setIconified(false);
        stage.show();
    }
}
