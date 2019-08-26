package ui_windows.options_window.price_lists_editor;


import core.CoreModule;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

public class PriceListsTable {
    private TableView<PriceList> tableView;

    public PriceListsTable(TableView<PriceList> tableView) {
        this.tableView = tableView;
        tableView.setPlaceholder(new Text("Нет данных для отображения"));

        TableColumn<PriceList, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(300);

        tableView.getColumns().add(nameCol);
        tableView.getItems().addAll(CoreModule.getPriceLists().getItems());
    }

    public TableView<PriceList> getTableView() {
        return tableView;
    }

    public PriceList getSelectedItem(){
        return tableView.getSelectionModel().getSelectedItem();
    }
}
