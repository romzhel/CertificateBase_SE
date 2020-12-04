package ui_windows.request;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class RequestWindowController implements Initializable {
    @FXML
    Button btnApply;

    @FXML
    TextArea taData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        taData.setWrapText(true);
        btnApply.requestFocus();
    }

    public void actionRequest() {
        close();
    }

    public void actionRequestCancel() {
        close();
    }

    private void close() {
        ((Stage) btnApply.getScene().getWindow()).close();
    }
}
