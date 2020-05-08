package ui_windows.options_window.families_editor;

import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProductFamiliesTable {
    private TableView<ProductFamily> tableView;

    public ProductFamiliesTable(TableView<ProductFamily> tableView){
        this.tableView = tableView;

        tableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
        tableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("responsible"));

        tableView.getItems().addAll(ProductFamilies.getInstance().getItems());
    }

    public TableView<ProductFamily> getTableView() {
        return tableView;
    }
}
