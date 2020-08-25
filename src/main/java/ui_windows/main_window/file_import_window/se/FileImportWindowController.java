package ui_windows.main_window.file_import_window.se;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ui.Dialogs;
import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.main_window.file_import_window.FileImportTable;
import ui_windows.product.data.DataItem;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;

import static ui_windows.product.data.DataItem.DATA_EMPTY;
import static ui_windows.product.data.DataItem.DATA_ORDER_NUMBER;

public class FileImportWindowController implements Initializable {
    private List<FileImportParameter> parameters;

    @FXML
    TableView<FileImportParameter> tvFields;

    @FXML
    ComboBox<String> cbSheetNames;

    @FXML
    CheckBox cbxDelPrevStat;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new FileImportTable(tvFields);
    }

    public void init(FileImport fileImport) {
        cbSheetNames.getItems().addAll(fileImport.getExcelFile().getSheetsName());
        cbSheetNames.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                tvFields.getItems().clear();
                int sheetIndex = cbSheetNames.getItems().indexOf(newValue);
                List<FileImportParameter> parameters = fileImport.getExcelFile().getImportParameters(sheetIndex);
                tvFields.getItems().addAll(parameters);
            }
        });
        if (cbSheetNames.getItems().size() > 0) {
            cbSheetNames.getSelectionModel().select(0);
        }
        cbxDelPrevStat.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != oldValue) {
                fileImport.setDeleteOldStatistic(newValue);
            }
        });
    }

    public void startImport() {
        if (checkNameDoubles()) {
            Dialogs.showMessage("Выбор столбцов", "Не допускается импортировать данные с разных столбцов" +
                    " в одно свойство, должен быть выбран столбец с заказным номером и минимум два столбца.");
            return;
        }

        if (cbxDelPrevStat.isSelected() && !Dialogs.confirm("Удаление информации о предыдущем импорте",
                "Информация об изменённых позициях во время предыдущего импорта будет удалена. Продолжаем?")) {
            return;
        }

        ((Stage) tvFields.getScene().getWindow()).close();
//        fileImport.getSelectionListener().selectionEvent(tvFields.getItems());
        parameters = tvFields.getItems();
    }

    public void cancelImport() {
        close();
    }

    public void close() {
        parameters = null;
        ((Stage) tvFields.getScene().getWindow()).close();
    }

    public boolean checkNameDoubles() {
        boolean hasntMaterial = true;
        ArrayList<DataItem> selectedItems = new ArrayList<>();
        for (FileImportParameter fiti : tvFields.getItems()) {
            if (fiti.getDataItem() != DATA_EMPTY) selectedItems.add(fiti.getDataItem());
            if (fiti.getDataItem() == DATA_ORDER_NUMBER) hasntMaterial = false;
        }

        HashSet<DataItem> singlesNames = new HashSet<>(selectedItems);
        return selectedItems.size() != singlesNames.size() || selectedItems.size() < 2 || hasntMaterial;
    }

    public List<FileImportParameter> getParameters() {
        return parameters;
    }
}
