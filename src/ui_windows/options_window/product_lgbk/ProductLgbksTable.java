package ui_windows.options_window.product_lgbk;

import core.CoreModule;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class ProductLgbksTable {
    private TableView<ProductLgbk> tableView;

    public ProductLgbksTable(TableView<ProductLgbk> tableView) {
        this.tableView = tableView;

        tableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("lgbk"));
        tableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("hierarchy"));
        tableView.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<ProductLgbk, String> col = new TableColumn<>("Направление");
        col.setPrefWidth(343);
        col.setCellValueFactory(param -> new SimpleStringProperty(
                CoreModule.getProductFamilies().getFamilyNameById(param.getValue().getFamilyId())));

        tableView.getColumns().add(col);

        TableColumn<ProductLgbk, Boolean> colB = new TableColumn<>("Не используется");
        colB.setCellValueFactory(param -> {
            return new SimpleBooleanProperty(param.getValue().isNotUsed());
        });
        colB.setCellFactory(CheckBoxTableCell.forTableColumn(colB));
        tableView.getColumns().add(colB);


        tableView.getItems().addAll(CoreModule.getProductLgbks().getProductLgbks());
        tableView.sort();
    }

    public TableView<ProductLgbk> getTableView() {
        return tableView;
    }
}
