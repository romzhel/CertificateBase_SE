package ui_windows.options_window.price_lists_editor.se;

import core.CoreModule;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.util.Callback;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;

import static ui_windows.options_window.product_lgbk.ProductLgbk.GROUP_NODE;
import static ui_windows.options_window.product_lgbk.ProductLgbk.ROOT_NODE;

public class PriceListContentTable {
    private TreeTableView<PriceListContentTableItem> treeTableView;


    public PriceListContentTable(TreeTableView<PriceListContentTableItem> treeTableView) {
        this.treeTableView = treeTableView;
        treeTableView.setEditable(true);

        initColumns();

        test();
    }

    public void initColumns() {
        TreeTableColumn<PriceListContentTableItem, String> familyColumn = new TreeTableColumn<>("Направление");
        familyColumn.setCellValueFactory(param -> {
            PriceListContentItem item = param.getValue().getValue().getContent();
            if (item instanceof ProductFamily) {
                return new SimpleStringProperty(((ProductFamily) item).getName());
            } else if (item instanceof ProductLgbk) {
                return new SimpleStringProperty(((ProductLgbk) item).getLgbk());
            }
            return null;
        });
        familyColumn.setPrefWidth(200);

        TreeTableColumn<PriceListContentTableItem, String> hierarchyColumn = new TreeTableColumn<>("Иерархия");
        hierarchyColumn.setCellValueFactory(param -> {
            PriceListContentItem item = param.getValue().getValue().getContent();
            if (item instanceof ProductLgbk) {
                return new SimpleStringProperty(((ProductLgbk) item).getHierarchy());
            } else {
                return null;
            }
        });
        hierarchyColumn.setPrefWidth(75);

        TreeTableColumn<PriceListContentTableItem, String> descriptionColumn = new TreeTableColumn<>("Описание");
        descriptionColumn.setCellValueFactory(param -> {
            PriceListContentItem item = param.getValue().getValue().getContent();
            if (item instanceof ProductLgbk) {
                return new SimpleStringProperty(((ProductLgbk) item).getDescriptionRuEn());
            } else {
                return null;
            }
        });
        descriptionColumn.setPrefWidth(400);

        TreeTableColumn<PriceListContentTableItem, Boolean> priceColumn = new TreeTableColumn<>("Прайс");
        priceColumn.setCellValueFactory(param -> {
            TreeItem<PriceListContentTableItem> treeItem = param.getValue();
            PriceListContentTableItem currItem = treeItem.getValue();
            boolean result = false;

            if (currItem.getContent() instanceof ProductFamily) {
                if (hasAllCheckedChildren(treeItem)) result = true;
            } else if (currItem.getContent() instanceof ProductLgbk) {
                result = currItem.isPrice();
            }

            SimpleBooleanProperty priceProperty = new SimpleBooleanProperty(result);
            priceProperty.addListener((observable, oldValue, newValue) -> {
                if (currItem.getContent() instanceof ProductFamily) {
                    for (TreeItem<PriceListContentTableItem> lgbk : treeItem.getChildren()) {
                        int lgbkGroupSize = CoreModule.getProductLgbkGroups().getTreeItem((ProductLgbk) lgbk.getValue().getContent()).getChildren().size();
                        int currentGroupSize = lgbk.getChildren().size();

                        if (lgbkGroupSize == currentGroupSize) {
                            lgbk.getValue().setPrice(newValue);
                        } else {
                            for (TreeItem<PriceListContentTableItem> hierarchy : lgbk.getChildren()) {
                                hierarchy.getValue().setPrice(newValue);
                            }
                        }
                    }
                } else if (currItem.getContent() instanceof ProductLgbk) {
                    currItem.setPrice(newValue);
                }

                treeTableView.refresh();
            });

            return priceProperty;
        });
        priceColumn.setCellFactory(new Callback<TreeTableColumn<PriceListContentTableItem, Boolean>, TreeTableCell<PriceListContentTableItem, Boolean>>() {
            @Override
            public TreeTableCell<PriceListContentTableItem, Boolean> call(TreeTableColumn<PriceListContentTableItem, Boolean> param) {
                return new CheckBoxTreeTableCell<PriceListContentTableItem, Boolean>() {
                   /* @Override
                    public void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null && !empty) {
//                            boolean childNormsDefined = false;

                            TreeItem<PriceListContentTableItem> currItem = getTreeTableView().getTreeItem(getIndex());

                            if (currItem.getValue().getContent() instanceof ProductFamily) {
                                boolean childChecked = false;
                                for (TreeItem<PriceListContentTableItem> lgbkTreeItem : currItem.getChildren()) {
                                    if (lgbkTreeItem.getValue().isPrice()) {
                                        childChecked = true;
                                        break;
                                    }
                                }
                                getStyleClass().removeAll("standard-cell", "one-color-cell", "two-color-cell", "root-cell");
                                if (((ProductLgbk) currItem.getValue().getContent()).getNodeType() == GROUP_NODE) {
                                    if (childChecked) getStyleClass().add("two-color-cell");
                                    else getStyleClass().add("one-color-cell");
                                } else if (((ProductLgbk) currItem.getValue().getContent()).getNodeType() == ROOT_NODE) {
                                    getStyleClass().add("root-cell");
                                } else {
                                    getStyleClass().add("standard-cell");
                                }
                            } else if (currItem.getValue().getContent() instanceof ProductLgbk) {
                                boolean childChecked = false;
                                for (TreeItem<PriceListContentTableItem> lgbkTreeItem : currItem.getChildren()) {
                                    if (lgbkTreeItem.getValue().isPrice()) {
                                        childChecked = true;
                                        break;
                                    }
                                }
                                getStyleClass().removeAll("standard-cell", "one-color-cell", "two-color-cell", "root-cell");
                                if (((ProductLgbk) currItem.getValue().getContent()).getNodeType() == GROUP_NODE) {
                                    if (childChecked) getStyleClass().add("two-color-cell");
                                    else getStyleClass().add("one-color-cell");
                                } else if (((ProductLgbk) currItem.getValue().getContent()).getNodeType() == ROOT_NODE) {
                                    getStyleClass().add("root-cell");
                                } else {
                                    getStyleClass().add("standard-cell");
                                }

                            }


                            *//*for (TreeItem<PriceListContentTableItem> plgbk : currItem.getChildren()) {
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

                            }*//*
                            setAlignment(Pos.CENTER);
                        }


                    }*/
                };
            }
        });
        priceColumn.setPrefWidth(100);
        priceColumn.setEditable(true);


        treeTableView.getColumns().addAll(familyColumn, hierarchyColumn, descriptionColumn, priceColumn);
    }

    private boolean hasCheckedChildren(TreeItem<PriceListContentTableItem> treeNode) {
        for (TreeItem<PriceListContentTableItem> treeItem : treeNode.getChildren()) {
            if (treeItem.getValue().isPrice()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAllCheckedChildren(TreeItem<PriceListContentTableItem> treeNode) {
        for (TreeItem<PriceListContentTableItem> treeItem : treeNode.getChildren()) {
            if (!treeItem.getValue().isPrice()) {
                return false;
            }
        }
        return true;
    }

    public void test() {


//        treeTableView.setRoot(new ConverterToPriceTable<>(CoreModule.getProductLgbkGroups().getFullTreeSet()));
        treeTableView.setRoot(new ConverterToPriceTable<PriceListContentItem>(new FamilyTree(new FamilyGroups())));
    }


}
