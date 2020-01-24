package utils.comparation.prices;

import core.Dialogs;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import ui_windows.main_window.MainWindow;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class PriceComparationWindowController implements Initializable {
    private File file1;
    private File file2;
    @FXML
    RadioButton rbUseNewPrice;
    @FXML
    RadioButton rbUseExistPrice;
    @FXML
    TextField tfPriceName1;
    @FXML
    TextField tfPriceName2;
    @FXML
    Button btnSelectPrice1;
    @FXML
    Button btnSelectPrice2;
    @FXML
    Button btnOk;
    @FXML
    Button btnCancel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup price1source = new ToggleGroup();
        rbUseNewPrice.setToggleGroup(price1source);
        rbUseExistPrice.setToggleGroup(price1source);

        price1source.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            tfPriceName1.setDisable(rbUseNewPrice.isSelected());
            btnSelectPrice1.setDisable(rbUseNewPrice.isSelected());
        });
        rbUseNewPrice.setSelected(true);

        btnSelectPrice1.setOnAction(event -> {
            file1 = new Dialogs().selectAnyFile(MainWindow.getMainStage(), "Выбор файла",
                    Dialogs.EXCEL_FILES, null);
            if (file1 != null && file1.exists()) {
                tfPriceName1.setText(file1.getName());
            } else {
                file1 = null;
            }
        });

        btnSelectPrice2.setOnAction(event -> {
            file2 = new Dialogs().selectAnyFile(MainWindow.getMainStage(), "Выбор файла",
                    Dialogs.EXCEL_FILES, null);
            if (file2 != null && file2.exists()) {
                tfPriceName2.setText(file2.getName());
            } else {
                file2 = null;
            }
        });

        btnOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new PricesComparator(rbUseNewPrice.isSelected() ? null : file1, file2);
            }
        });

        btnCancel.setOnAction(event -> ((Stage) btnOk.getParent().getScene().getWindow()).close());
    }
}
