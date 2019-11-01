package ui_windows.main_window.file_import_window;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import ui_windows.product.data.ProductProperties;

public class FileImportTable {
    private TableView<FileImportTableItem> tableView;

    public FileImportTable(TableView<FileImportTableItem> tableView) {
        this.tableView = tableView;
        TableColumn<FileImportTableItem, String> titleNameCol = new TableColumn<>("Столбец");
        titleNameCol.setCellValueFactory(new PropertyValueFactory<>("tableTitle"));
        titleNameCol.setPrefWidth(300);

        TableColumn<FileImportTableItem, String> productFieldCol = new TableColumn<>("Назначение");
        productFieldCol.setCellValueFactory(param -> {
            param.getValue().productFieldProperty().addListener((observable, oldValue, newValue) -> {
//                param.getValue().setProductField(newValue);
                if (!newValue.isEmpty()){
                    param.getValue().setImportValue(true);
                    param.getValue().setLogHistory(true);
                } else {
                    param.getValue().setImportValue(false);
                    param.getValue().setLogHistory(false);
                }
            });
            return param.getValue().productFieldProperty();
        });
        productFieldCol.setPrefWidth(300);
        productFieldCol.setCellFactory(ComboBoxTableCell.forTableColumn(ProductProperties.getAllNamesRu()));
        productFieldCol.setEditable(true);

        TableColumn<FileImportTableItem, Boolean> importCol = new TableColumn<>("Импорт");
        importCol.setCellValueFactory(param -> {
            BooleanProperty booleanProperty = param.getValue().importValueProperty();
            booleanProperty.addListener((observable, oldValue, newValue) -> {
                param.getValue().setImportValue(newValue);
                param.getValue().setLogHistory(newValue);
            });
            return booleanProperty;
        });
        importCol.setCellFactory(CheckBoxTableCell.forTableColumn(importCol));

        TableColumn<FileImportTableItem, Boolean> historyCol = new TableColumn<>("Журнал");
        historyCol.setCellValueFactory(param -> {
            BooleanProperty booleanProperty = param.getValue().logHistoryProperty();
            booleanProperty.addListener((observable, oldValue, newValue) -> {
                param.getValue().setLogHistory(newValue);
                param.getValue().setImportValue(newValue == true ? true : param.getValue().isImportValue() || newValue);
            });
            return booleanProperty;
        });
        historyCol.setCellFactory(CheckBoxTableCell.forTableColumn(importCol));

        tableView.getColumns().addAll(titleNameCol, productFieldCol, importCol, historyCol);
        tableView.setEditable(true);
    }

    public TableView<FileImportTableItem> getTableView() {
        return tableView;
    }
}
