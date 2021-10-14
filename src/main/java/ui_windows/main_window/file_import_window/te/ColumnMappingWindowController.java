package ui_windows.main_window.file_import_window.te;

import core.ThreadManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import ui.Dialogs;
import ui_windows.main_window.file_import_window.te.importer.ImportDataSheet;
import ui_windows.product.data.DataItem;
import ui_windows.product.vendors.VendorEnum;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ui_windows.product.data.DataItem.DATA_EMPTY;
import static ui_windows.product.data.DataItem.DATA_ORDER_NUMBER;

@Log4j2
public class ColumnMappingWindowController {
    @FXML
    private TableView<ImportColumnParameter> tvFields;
    @FXML
    private ComboBox<ImportDataSheet> cbSheetNames;
    @FXML
    private Label lblSource;
    @FXML
    private Button btnReset;
    @FXML
    private ComboBox<VendorEnum> cbxVendor;

    private ImportDataSheet inputSheet;
    private ImportDataSheet result;

    public void init(ImportDataSheet inputData) {
        this.inputSheet = inputData;
        new ColumnMappingTable(tvFields);
        lblSource.setText(inputData.getFileName().concat("/").concat(inputData.getSheetName()));
        tvFields.getItems().addAll(inputData.getColumnParams());

        cbxVendor.setConverter(new StringConverter<VendorEnum>() {
            @Override
            public String toString(VendorEnum vendor) {
                return vendor.name();
            }

            @Override
            public VendorEnum fromString(String name) {
                return VendorEnum.valueOf(name);
            }
        });
        cbxVendor.getItems().addAll(VendorEnum.values());
        cbxVendor.getSelectionModel().select(0);
        cbxVendor.setOnAction(eh -> {
            log.debug("action select vendor: '{}'", cbxVendor.getSelectionModel().getSelectedItem());
            inputSheet.setVendor(cbxVendor.getSelectionModel().getSelectedItem());
        });

        btnReset.setOnAction(event -> {
            inputData.getColumnParams().stream()
                    .filter(item -> item.getDataItem() != DATA_ORDER_NUMBER)
                    .forEach(item -> item.setDataItem(DATA_EMPTY));
            tvFields.refresh();
        });
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
