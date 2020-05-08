package ui_windows.options_window.price_lists_editor;


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

        TableColumn<PriceList, String> fileNameCol = new TableColumn<>("Название файла");
        fileNameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fileNameCol.setPrefWidth(300);

        tableView.getColumns().addAll(nameCol, fileNameCol);
        tableView.getItems().addAll(PriceLists.getInstance().getItems());
    }

    public TableView<PriceList> getTableView() {
        return tableView;
    }

    public PriceList getSelectedItem(){
        return tableView.getSelectionModel().getSelectedItem();
    }
}
