package ui_windows.main_window.file_import_window.te;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

import static ui_windows.main_window.file_import_window.te.FilesImportParametersEnum.BLOCK_PROPERTY;

public class ColumnMappingTable {
    private static final Logger logger = LogManager.getLogger(ColumnMappingTable.class);

    public ColumnMappingTable(TableView<ImportColumnParameter> tableView) {
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
                logger.debug("column import param => {}", param.getValue());
            });
            return dataItemProperty;
        });
        productFieldCol.setPrefWidth(300);
        productFieldCol.setEditable(true);

        /*TableColumn<ImportColumnParameter, Boolean> importCol = new TableColumn<>("Импорт");
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
        });*/

        TableColumn<ImportColumnParameter, Boolean> blockCol = new TableColumn<>("Блокировка");
        blockCol.setCellFactory(CheckBoxTableCell.forTableColumn(blockCol));
        blockCol.setCellValueFactory(param -> {
            boolean propertyValue = param.getValue().getOptions().getOrDefault(BLOCK_PROPERTY, false);
            BooleanProperty property = new SimpleBooleanProperty(propertyValue);
            property.addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    param.getValue().getOptions().put(BLOCK_PROPERTY, true);
                } else {
                    param.getValue().getOptions().remove(BLOCK_PROPERTY);
                }

                logger.debug("column import param => {}", param.getValue());
            });
            return property;
        });

        tableView.getColumns().addAll(titleNameCol, productFieldCol, blockCol);
        tableView.setEditable(true);
    }
}
