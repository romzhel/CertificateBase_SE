package ui_windows.request_certificates;

import javafx.stage.Modality;
import javafx.stage.Stage;
import ui_windows.Mode;
import ui_windows.OrdinalWindow;
import ui_windows.main_window.MainWindow;

public class CertificateRequestWindow extends OrdinalWindow {


    public CertificateRequestWindow() {
        super(MainWindow.getMainStage(), Modality.APPLICATION_MODAL, null, "certificateRequestWindow.fxml",
                "Ввод данных");

        stage.show();
    }



}
