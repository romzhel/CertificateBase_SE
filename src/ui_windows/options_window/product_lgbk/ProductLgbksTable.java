package ui_windows.options_window.product_lgbk;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.util.Callback;
import ui_windows.options_window.families_editor.ProductFamilies;

import static ui_windows.options_window.product_lgbk.ProductLgbk.GROUP_NODE;
import static ui_windows.options_window.product_lgbk.ProductLgbk.ROOT_NODE;

public class ProductLgbksTable {
    private TreeTableView<ProductLgbk> tableView;

    public ProductLgbksTable(TreeTableView<ProductLgbk> tableView) {
        this.tableView = tableView;

        tableView.getColumns().get(0).setCellValueFactory(new TreeItemPropertyValueFactory<>("lgbk"));
        tableView.getColumns().get(0).setPrefWidth(110);
        tableView.getColumns().get(1).setCellValueFactory(new TreeItemPropertyValueFactory<>("hierarchy"));
        tableView.getColumns().get(1).setPrefWidth(110);
        tableView.getColumns().get(2).setCellValueFactory(new TreeItemPropertyValueFactory<>("description_en"));
        tableView.getColumns().get(2).setPrefWidth(250);
        tableView.getColumns().get(3).setCellValueFactory(new TreeItemPropertyValueFactory<>("description_ru"));
        tableView.getColumns().get(3).setPrefWidth(250);

        TreeTableColumn<ProductLgbk, String> col = new TreeTableColumn<>("Направление");
        col.setPrefWidth(225);
        col.setCellValueFactory(param -> new SimpleStringProperty(
                ProductFamilies.getInstance().getFamilyNameById(param.getValue().getValue().getFamilyId())));

        tableView.getColumns().add(col);

        TreeTableColumn<ProductLgbk, Boolean> colB = new TreeTableColumn<>("Нормы");
        colB.setCellValueFactory(param -> {
            boolean isNormsExists = param.getValue().getValue().getNormsList().getIntegerItems().size() > 0;
            return new SimpleBooleanProperty(isNormsExists);
        });
        colB.setCellFactory(new Callback<TreeTableColumn<ProductLgbk, Boolean>, TreeTableCell<ProductLgbk, Boolean>>() {
            @Override
            public TreeTableCell<ProductLgbk, Boolean> call(TreeTableColumn<ProductLgbk, Boolean> param) {
                return new CheckBoxTreeTableCell<ProductLgbk, Boolean>() {
                    @Override
                    public void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!empty) {
                            boolean childNormsDefined = false;
                            TreeItem<ProductLgbk> currItem = getTableView().getTreeItem(getIndex());
                            for (TreeItem<ProductLgbk> plgbk : currItem.getChildren()) {
                                if (plgbk.getValue().getNormsList().getIntegerItems().size() > 0) childNormsDefined = true;
                            }

                            getStyleClass().removeAll("standard-cell", "one-color-cell", "two-color-cell", "root-cell");
                            if (currItem.getValue().getNodeType() == GROUP_NODE) {
                                if (childNormsDefined) getStyleClass().add("two-color-cell");
                                else getStyleClass().add("one-color-cell");
                            } else if (currItem.getValue().getNodeType() == ROOT_NODE) {
                                getStyleClass().add("root-cell");
                            } else {
                                getStyleClass().add("standard-cell");

                            }
                            setAlignment(Pos.CENTER);
                        }
                    }
                };
            }
        });
        tableView.getColumns().add(colB);

        /*TreeTableColumn<ProductLgbk, Boolean> colExcPrice = new TreeTableColumn<>("Искл. из прайса");
        colExcPrice.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue().getValue().isNotUsed()));

        colExcPrice.setCellFactory(new Callback<TreeTableColumn<ProductLgbk, Boolean>, TreeTableCell<ProductLgbk, Boolean>>() {
            @Override
            public TreeTableCell<ProductLgbk, Boolean> call(TreeTableColumn<ProductLgbk, Boolean> param) {
                return new CheckBoxTreeTableCell<ProductLgbk, Boolean>() {
                    @Override
                    public void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!empty) {
                            boolean childExcluded = false;
                            TreeItem<ProductLgbk> currItem = getTableView().getTreeItem(getIndex());
                            for (TreeItem<ProductLgbk> plgbk : currItem.getChildren()) {
                                if (plgbk.getValue().isNotUsed()) childExcluded = true;
                            }

                            getStyleClass().removeAll("standard-cell", "one-color-cell", "two-color-cell", "root-cell");
                            if (currItem.getValue().getNodeType() == GROUP_NODE) {
                                if (childExcluded) getStyleClass().add("two-color-cell");
                                else getStyleClass().add("one-color-cell");
                            } else if (currItem.getValue().getNodeType() == ROOT_NODE) {
                                getStyleClass().add("root-cell");
                            } else {
                                getStyleClass().add("standard-cell");

                            }
                            setAlignment(Pos.CENTER);
                        }
                    }
                };
            }
        });

        tableView.getColumns().add(colExcPrice);*/

        tableView.setRoot(ProductLgbkGroups.getInstance().getFullTreeSet());
    }

    public TreeTableView<ProductLgbk> getTableView() {
        return tableView;
    }
}
