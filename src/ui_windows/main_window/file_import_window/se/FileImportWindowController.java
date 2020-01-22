package ui_windows.main_window.file_import_window.se;

import core.Dialogs;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ui_windows.main_window.file_import_window.FileImport;
import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.main_window.file_import_window.FileImportTable;

import java.net.URL;
import java.util.ResourceBundle;

public class FileImportWindowController implements Initializable {
    private FileImport fileImport;

    @FXML
    TableView<FileImportParameter> tvFields;

    @FXML
    ComboBox<String> cbSheetNames;

    @FXML
    CheckBox cbxDelPrevStat;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new FileImportTable(tvFields);

        cbSheetNames.valueProperty().addListener((observable, oldValue, newValue) ->
                fileImport.displayTitlesAndMapping(cbSheetNames.getItems().indexOf(newValue)));
    }

    public void startImport() {
        if (fileImport.checkNameDoubles()) {
            Dialogs.showMessage("Выбор столбцов", "Не допускается импортировать данные с разных столбцов" +
                    " в одно свойство, должен быть выбран столбец с заказным номером и минимум два столбца.");
            return;
        }

        if (cbxDelPrevStat.isSelected() && !Dialogs.confirm("Удаление информации о предыдущем импорте",
                "Информация об изменённых позициях во время предыдущего импорта будет удалена. Продолжаем?")) {
            return;
        }

        ObservableList<FileImportParameter> parameters = tvFields.getItems();
        ((Stage) tvFields.getScene().getWindow()).close();
        fileImport.startImport(parameters);
    }

    public void cancelImport() {
        close();
    }

    public void close() {
        fileImport.getExcelFile().close();
        fileImport.setExcelFile(null);
        ((Stage) tvFields.getScene().getWindow()).close();
    }

    public void setFileImport(FileImport fileImport) {
        this.fileImport = fileImport;
    }
}
