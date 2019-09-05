package ui_windows.options_window.product_lgbk;

import core.CoreModule;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

import java.util.TreeSet;

public class ProductLgbksTable {
    private TreeTableView<ProductLgbk> tableView;

    public ProductLgbksTable(TreeTableView<ProductLgbk> tableView) {
        this.tableView = tableView;

        tableView.getColumns().get(0).setCellValueFactory(new TreeItemPropertyValueFactory<>("lgbk"));
        tableView.getColumns().get(0).setPrefWidth(125);
        tableView.getColumns().get(1).setCellValueFactory(new TreeItemPropertyValueFactory<>("hierarchy"));
        tableView.getColumns().get(1).setPrefWidth(125);
        tableView.getColumns().get(2).setCellValueFactory(new TreeItemPropertyValueFactory<>("description_en"));
        tableView.getColumns().get(2).setPrefWidth(250);
        tableView.getColumns().get(3).setCellValueFactory(new TreeItemPropertyValueFactory<>("description_ru"));
        tableView.getColumns().get(3).setPrefWidth(250);

        TreeTableColumn<ProductLgbk, String> col = new TreeTableColumn<>("Направление");
        col.setPrefWidth(225);
        col.setCellValueFactory(param -> new SimpleStringProperty(
                CoreModule.getProductFamilies().getFamilyNameById(param.getValue().getValue().getFamilyId())));

        tableView.getColumns().add(col);

        TreeTableColumn<ProductLgbk, Boolean> colB = new TreeTableColumn<>("Нормы");
        colB.setCellValueFactory(param -> {
            boolean isNormsExists = param.getValue().getValue().getNormsList().getIntegerItems().size() > 0;
            return new SimpleBooleanProperty(isNormsExists);
        });
        colB.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(colB));
        tableView.getColumns().add(colB);

        tableView.setRoot(CoreModule.getProductLgbkGroups().getFullTreeSet());
    }

    public TreeTableView<ProductLgbk> getTableView() {
        return tableView;
    }
}
