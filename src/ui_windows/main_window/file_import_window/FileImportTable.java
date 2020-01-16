package ui_windows.main_window.file_import_window;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import ui_windows.product.data.DataItem;
import ui_windows.product.data.DataSets;

public class FileImportTable {
    private TableView<FileImportParameter> tableView;

    public FileImportTable(TableView<FileImportParameter> tableView) {
        this.tableView = tableView;
        TableColumn<FileImportParameter, String> titleNameCol = new TableColumn<>("Столбец");
        titleNameCol.setCellValueFactory(new PropertyValueFactory<>("tableTitle"));
        titleNameCol.setPrefWidth(300);

        TableColumn<FileImportParameter, DataItem> productFieldCol = new TableColumn<>("Назначение");
        productFieldCol.setCellFactory(ComboBoxTableCell.forTableColumn(new StringConverter<DataItem>() {
            @Override
            public String toString(DataItem object) {
                return object != null ?  object.getDisplayingName() : "";
            }

            @Override
            public DataItem fromString(String string) {
                return DataItem.getByDisplayingName(string);
            }
        }, DataSets.getDataItemsForNowImport()));
        productFieldCol.setCellValueFactory(param -> {
            param.getValue().dataItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != DataItem.DATA_EMPTY) {
                    param.getValue().setImportValue(true);
                    param.getValue().setLogHistory(true);
                } else {
                    param.getValue().setImportValue(false);
                    param.getValue().setLogHistory(false);
                }

            });
            return param.getValue().dataItemProperty();
        });
        productFieldCol.setPrefWidth(300);
        productFieldCol.setEditable(true);

        TableColumn<FileImportParameter, Boolean> importCol = new TableColumn<>("Импорт");
        importCol.setCellValueFactory(param -> {
            BooleanProperty booleanProperty = param.getValue().importValueProperty();
            booleanProperty.addListener((observable, oldValue, newValue) -> {
                param.getValue().setImportValue(newValue);
                param.getValue().setLogHistory(newValue);
            });
            return booleanProperty;
        });
        importCol.setCellFactory(CheckBoxTableCell.forTableColumn(importCol));

        TableColumn<FileImportParameter, Boolean> historyCol = new TableColumn<>("Журнал");
        historyCol.setCellFactory(CheckBoxTableCell.forTableColumn(importCol));
        historyCol.setCellValueFactory(param -> {
            BooleanProperty booleanProperty = param.getValue().logHistoryProperty();
            booleanProperty.addListener((observable, oldValue, newValue) -> {
                param.getValue().setLogHistory(newValue);
                param.getValue().setImportValue(newValue == true ? true : param.getValue().isImportValue() || newValue);
            });
            return booleanProperty;
        });

        tableView.getColumns().addAll(titleNameCol, productFieldCol, importCol, historyCol);
        tableView.setEditable(true);
    }

    public TableView<FileImportParameter> getTableView() {
        return tableView;
    }
}
