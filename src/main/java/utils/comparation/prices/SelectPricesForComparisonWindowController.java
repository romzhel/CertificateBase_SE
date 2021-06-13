package utils.comparation.prices;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import ui.Dialogs;
import ui_windows.main_window.MainWindow;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SelectPricesForComparisonWindowController implements Initializable {
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
        ToggleGroup price2source = new ToggleGroup();
        rbUseNewPrice.setToggleGroup(price2source);
        rbUseExistPrice.setToggleGroup(price2source);

        price2source.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            tfPriceName2.setDisable(rbUseNewPrice.isSelected());
            btnSelectPrice2.setDisable(rbUseNewPrice.isSelected());
        });
        rbUseNewPrice.setSelected(true);

        btnSelectPrice1.setOnAction(event -> {
            file1 = new Dialogs().selectAnyFile(MainWindow.getMainStage(), "Выбор файла первого прайса",
                    Dialogs.EXCEL_FILES_ALL, null).get(0);
            if (file1 != null && file1.exists()) {
                tfPriceName1.setText(file1.getName());
            } else {
                file1 = null;
            }
        });

        btnSelectPrice2.setOnAction(event -> {
            file2 = new Dialogs().selectAnyFile(MainWindow.getMainStage(), "Выбор файла второго прайса",
                    Dialogs.EXCEL_FILES_ALL, null).get(0);
            if (file2 != null && file2.exists()) {
                tfPriceName2.setText(file2.getName());
            } else {
                file2 = null;
            }
        });

        btnOk.setOnAction(event -> {
            if (file1 != null && (file2 != null || rbUseNewPrice.isSelected())) {

                if (rbUseNewPrice.isSelected()) {
                    file2 = null;
                }

                ((Stage) btnOk.getParent().getScene().getWindow()).close();
            }
        });

        btnCancel.setOnAction(event -> ((Stage) btnOk.getParent().getScene().getWindow()).close());
    }

    public File getFile1() {
        return file1;
    }

    public File getFile2() {
        return file2;
    }
}
