package ui_windows.main_window.file_import_window.te;

import core.ThreadManager;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import ui.Dialogs;
import ui_windows.main_window.file_import_window.te.importer.ImportDataSheet;
import ui_windows.product.data.DataItem;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ui_windows.product.data.DataItem.DATA_EMPTY;
import static ui_windows.product.data.DataItem.DATA_ORDER_NUMBER;

public class ColumnMappingWindowController {
    private static final Logger logger = LogManager.getLogger(ColumnMappingWindowController.class);

    @FXML
    private TableView<ImportColumnParameter> tvFields;
    @FXML
    private ComboBox<ImportDataSheet> cbSheetNames;
    @FXML
    private Label lblSource;
    private ImportDataSheet inputSheet;
    private ImportDataSheet result;

    public void init(ImportDataSheet inputData) {
        this.inputSheet = inputData;
        new ColumnMappingTable(tvFields);
        lblSource.setText(inputData.getFileName().concat("/").concat(inputData.getSheetName()));
        tvFields.getItems().addAll(inputData.getColumnParams());

        /*cbSheetNames.getItems().addAll(inputData);
        cbSheetNames.setCellFactory(new Callback<ListView<ImportDataSheet>, ListCell<ImportDataSheet>>() {
            @Override
            public ListCell<ImportDataSheet> call(ListView<ImportDataSheet> param) {
                return new ListCell<ImportDataSheet>(){
                    @Override
                    protected void updateItem(ImportDataSheet item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            setGraphic(null);
                            setText(item.getFileName().concat("/").concat(item.getSheetName()));
                        }
                    }
                };
            }
        });
        cbSheetNames.valueProperty().addListener((observable, oldValue, newValue) -> {
            tvFields.getItems().clear();
            tvFields.getItems().addAll(newValue.getColumnParams());
        });
        if (cbSheetNames.getItems().size() > 0) {
            cbSheetNames.getSelectionModel().select(0);
        }*/
    }

    public void apply() {
        if (isSelectionCorrect()) {
            result = inputSheet;
            tvFields.getScene().getWindow().hide();
//        fileImport.getSelectionListener().selectionEvent(tvFields.getItems());
        }
    }

    public void cancel() throws RuntimeException {
        if (ThreadManager.executeFxTaskSafe(() -> Dialogs.confirm("Отмена операции", "Действительно желаете отменить импорт?"))) {
            tvFields.getScene().getWindow().hide();
        }
    }

    public boolean isSelectionCorrect() {
        List<DataItem> selectedDataItems = tvFields.getItems().stream()
                .map(ImportColumnParameter::getDataItem)
                .filter(dataItem -> dataItem != DATA_EMPTY)
                .collect(Collectors.toList());

        if (selectedDataItems.size() < 2 || selectedDataItems.stream().noneMatch(dataItem -> dataItem == DATA_ORDER_NUMBER)) {
            Dialogs.showMessageTS("Выбор столбцов", "Минимально должно быть выбрано два столбца, " +
                    "один из которых - заказной номер.");
            return false;
        }

        Set<DataItem> dublicatedDataItems = selectedDataItems.stream()
                .filter(dataItem -> Collections.frequency(selectedDataItems, dataItem) > 1)
                .collect(Collectors.toSet());

        if (dublicatedDataItems.size() > 0) {
            String dublicatesInfo = Strings.join(dublicatedDataItems.stream()
                    .map(dataItem -> "-" + dataItem.getDisplayingName())
                    .collect(Collectors.toSet()), '\n');

            Dialogs.showMessageTS("Выбор столбцов", "Не допускается сопоставление нескольких столбцов с " +
                    "одним свойством. Найдено дублирование:\n\n" + dublicatesInfo);
            return false;
        }

        return true;
    }

    public ImportDataSheet getResult() {
        return result;
    }
}
