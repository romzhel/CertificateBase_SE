package ui_windows.options_window.order_accessibility_editor;

import core.CoreModule;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

public class OrdersAccessibilityTable {
    private TableView<OrderAccessibility> tableView;


    public OrdersAccessibilityTable(TableView<OrderAccessibility> tableView) {
        this.tableView = tableView;
        String[] cols = new String[]{"statusCode", "sesCode", "descriptionEn", "descriptionRu", "f1", "f2",
                "status", "orderable"};
        String[] titles = new String[]{"Код статуса", "Код SES", "Описание (англ)", "Описание (руск)", "#1", "#2",
                "Комментарий", "Доступность"};
        int[] width = new int[]{100, 100, 250, 250, 30, 30, 250, 100};

        for (int i = 0; i < cols.length; i++) {
            if (cols[i] == "orderable") {
                TableColumn<OrderAccessibility, Boolean> colB = new TableColumn<>(titles[i]);

                colB.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue().isOrderable()));
                colB.setCellFactory(CheckBoxTableCell.forTableColumn(colB));
                colB.setPrefWidth(width[i]);

                tableView.getColumns().add(colB);
            } else {
                TableColumn<OrderAccessibility, String> colS = new TableColumn<>(titles[i]);

                colS.setCellValueFactory(new PropertyValueFactory<>(cols[i]));
                colS.setPrefWidth(width[i]);

                tableView.getColumns().add(colS);
            }
        }
        tableView.getItems().addAll(CoreModule.getOrdersAccessibility().getItems());
    }

    public TableView<OrderAccessibility> getTableView() {
        return tableView;
    }

    public void setTableView(TableView<OrderAccessibility> tableView) {
        this.tableView = tableView;
    }
}
