package ui_windows.main_window.file_import_window.te;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.product.data.DataItem;
import ui_windows.product.data.DataSets;

import java.util.List;
import java.util.Map;

import static ui_windows.main_window.file_import_window.te.FilesImportParametersEnum.FOR_IMPORT;
import static ui_windows.main_window.file_import_window.te.FilesImportParametersEnum.FOR_LOGGING;

public class ColumnMappingTable {
    private static final Logger logger = LogManager.getLogger(ColumnMappingTable.class);

    public ColumnMappingTable(TableView<ImportColumnParameter> tableView,
                              Map<String, List<ImportColumnParameter>> parameters) {
        TableColumn<ImportColumnParameter, String> titleNameCol = new TableColumn<>("Столбец");
        titleNameCol.setCellValueFactory(new PropertyValueFactory<>("columnTitle"));
        titleNameCol.setPrefWidth(300);

        TableColumn<ImportColumnParameter, DataItem> productFieldCol = new TableColumn<>("Назначение");
        productFieldCol.setCellFactory(ComboBoxTableCell.forTableColumn(new StringConverter<DataItem>() {
            @Override
            public String toString(DataItem object) {
                return object != null ? object.getDisplayingName() : "";
            }

            @Override
            public DataItem fromString(String string) {
                return DataItem.getByDisplayingName(string);
            }
        }, DataSets.getDataItemsForNowImport()));
        productFieldCol.setCellValueFactory(param -> {
            ObjectProperty<DataItem> dataItemProperty = new SimpleObjectProperty<>(param.getValue().getDataItem());
            dataItemProperty.addListener((observable, oldValue, newValue) -> {
                param.getValue().setDataItem(newValue);
                param.getValue().getOptions().get(FOR_IMPORT).set(newValue != DataItem.DATA_EMPTY);
                param.getValue().getOptions().get(FOR_LOGGING).set(newValue != DataItem.DATA_EMPTY);
                logger.debug("column import param => {}", param.getValue());
            });
            return dataItemProperty;
        });
        productFieldCol.setPrefWidth(300);
        productFieldCol.setEditable(true);

        TableColumn<ImportColumnParameter, Boolean> importCol = new TableColumn<>("Импорт");
        importCol.setCellFactory(CheckBoxTableCell.forTableColumn(importCol));
        importCol.setCellValueFactory(param -> {
            param.getValue().getOptions().get(FOR_IMPORT).addListener((observable, oldValue, newValue) -> {
                param.getValue().getOptions().get(FOR_IMPORT).set(newValue);
                param.getValue().getOptions().get(FOR_LOGGING).set(newValue);
                logger.debug("column import param => {}", param.getValue());
            });
            return param.getValue().getOptions().get(FOR_IMPORT);
        });

        TableColumn<ImportColumnParameter, Boolean> historyCol = new TableColumn<>("Журнал");
        historyCol.setCellFactory(CheckBoxTableCell.forTableColumn(importCol));
        historyCol.setCellValueFactory(param -> {
            param.getValue().getOptions().get(FOR_LOGGING).addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    param.getValue().getOptions().get(FOR_IMPORT).set(true);
                }
//                param.getValue().getOptions().get(FOR_LOGGING).set(newValue);
                logger.debug("column import param => {}", param.getValue());
            });
            return param.getValue().getOptions().get(FOR_LOGGING);
        });

        tableView.getColumns().addAll(titleNameCol, productFieldCol, importCol, historyCol);
        tableView.setEditable(true);
    }
}
