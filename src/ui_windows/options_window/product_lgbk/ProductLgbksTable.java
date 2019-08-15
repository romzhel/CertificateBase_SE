package ui_windows.options_window.product_lgbk;

import core.CoreModule;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.util.Callback;

public class ProductLgbksTable {
    private TreeTableView<ProductLgbk> tableView;

    public ProductLgbksTable(TreeTableView<ProductLgbk> tableView) {
        this.tableView = tableView;

        tableView.getColumns().get(0).setCellValueFactory(new TreeItemPropertyValueFactory<>("lgbk"));
        tableView.getColumns().get(1).setCellValueFactory(new TreeItemPropertyValueFactory<>("hierarchy"));
        tableView.getColumns().get(2).setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));

        TreeTableColumn<ProductLgbk, String> col = new TreeTableColumn<>("Направление");
        col.setPrefWidth(343);
        col.setCellValueFactory(param -> new SimpleStringProperty(
                CoreModule.getProductFamilies().getFamilyNameById(param.getValue().getValue().getFamilyId())));

        tableView.getColumns().add(col);

        TreeTableColumn<ProductLgbk, Boolean> colB = new TreeTableColumn<>("Не используется");
        colB.setCellValueFactory(param -> {
            return new SimpleBooleanProperty(param.getValue().getValue().isNotUsed());
        });
        colB.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(colB));
        tableView.getColumns().add(colB);


//        tableView.  getItems().addAll(CoreModule.getProductLgbks().getProductLgbks());
        tableView.setRoot(CoreModule.getProductLgbkGroups().getLgbkTreeSet());
    }

    public TreeTableView<ProductLgbk> getTableView() {
        return tableView;
    }
}
