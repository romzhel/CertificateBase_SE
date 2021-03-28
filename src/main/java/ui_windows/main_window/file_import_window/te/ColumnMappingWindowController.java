package ui_windows.main_window.file_import_window.te;

import core.ThreadManager;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.util.Strings;
import ui.Dialogs;
import ui_windows.product.data.DataItem;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ui_windows.product.data.DataItem.DATA_EMPTY;
import static ui_windows.product.data.DataItem.DATA_ORDER_NUMBER;

public class ColumnMappingWindowController {
    @FXML
    TableView<ImportColumnParameter> tvFields;
    @FXML
    ComboBox<String> cbSheetNames;
    @FXML
    CheckBox cbxDelPrevStat;
    @FXML
    CheckBox cbxResetCost;
    private Map<String, List<ImportColumnParameter>> inputParameters;
    private Map<String, List<ImportColumnParameter>> outputParameters;

    public void init(Map<String, List<ImportColumnParameter>> parameters) {
        this.inputParameters = parameters;
        new ColumnMappingTable(tvFields, parameters);

        cbSheetNames.getItems().addAll(parameters.keySet());
        cbSheetNames.valueProperty().addListener((observable, oldValue, newValue) -> {
            tvFields.getItems().clear();
            String selectedSheetName = cbSheetNames.getSelectionModel().getSelectedItem();
            tvFields.getItems().addAll(parameters.get(selectedSheetName));
        });
        if (cbSheetNames.getItems().size() > 0) {
            cbSheetNames.getSelectionModel().select(0);
        }
    }

    public void apply() {
        if (isSelectionCorrect()) {
            outputParameters = inputParameters;
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

    public Map<String, List<ImportColumnParameter>> getParameters() {
        return outputParameters;
    }
}
