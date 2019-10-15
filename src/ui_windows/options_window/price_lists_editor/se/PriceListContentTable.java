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
import org.apache.poi.ss.formula.functions.T;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.Product;

import java.util.ArrayList;

import static ui_windows.options_window.product_lgbk.ProductLgbk.GROUP_NODE;
import static ui_windows.options_window.product_lgbk.ProductLgbk.ROOT_NODE;

public class PriceListContentTable {
    private static final String FAMILY_ITEM = "0";
    private static final String LGBK_ITEM = "1";
    public static final int CONTENT_MODE_FAMILY = 0;
    public static final int CONTENT_MODE_LGBK = 1;
    private TreeTableView<PriceListContentTableItem> treeTableView;

    public PriceListContentTable(TreeTableView<PriceListContentTableItem> treeTableView) {
        this.treeTableView = treeTableView;
        treeTableView.setEditable(true);

        initColumns();
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
                result = hasAllCheckedChildren(treeItem);
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
                    @Override
                    public void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null && !empty) {
                            TreeItem<PriceListContentTableItem> currItem = getTreeTableView().getTreeItem(getIndex());
                            if (currItem.equals(getTreeTableView().getRoot())) {

                            } else if (currItem.getValue().getContent() instanceof ProductFamily) {
                                boolean childChecked = false;
                                for (TreeItem<PriceListContentTableItem> lgbkGroupTreeItem : currItem.getChildren()) {
                                    if (lgbkGroupTreeItem.getValue().isPrice()) {
                                        childChecked = true;
                                        break;
                                    } else {
                                        for (TreeItem<PriceListContentTableItem> lgbkTreeItem : lgbkGroupTreeItem.getChildren()) {
                                            if (lgbkTreeItem.getValue().isPrice()) {
                                                childChecked = true;
                                                break;
                                            }
                                            if (childChecked) break;
                                        }
                                    }
                                }
                                getStyleClass().removeAll("standard-cell", "one-color-cell", "two-color-cell", "root-cell", "root-two-color-cell");
                                if (childChecked) getStyleClass().add("root-two-color-cell");
                                else getStyleClass().add("root-cell");

                            } else if (currItem.getValue().getContent() instanceof ProductLgbk) {
                                boolean childChecked = false;
                                for (TreeItem<PriceListContentTableItem> lgbkTreeItem : currItem.getChildren()) {
                                    if (lgbkTreeItem.getValue().isPrice()) {
                                        childChecked = true;
                                        break;
                                    }
                                }
                                getStyleClass().removeAll("standard-cell", "one-color-cell", "two-color-cell", "root-cell", "root-two-color-cell");
                                if (((ProductLgbk) currItem.getValue().getContent()).getNodeType() == GROUP_NODE) {
                                    if (childChecked) getStyleClass().add("two-color-cell");
                                    else getStyleClass().add("one-color-cell");
                                } else if (((ProductLgbk) currItem.getValue().getContent()).getNodeType() == ROOT_NODE) {
                                    getStyleClass().add("root-cell");
                                } else {
                                    getStyleClass().add("standard-cell");
                                }

                            }
                            setAlignment(Pos.CENTER);
                        }
                    }
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

    public void setContentMode(int contentMode) {
        if (contentMode == CONTENT_MODE_FAMILY) {
            treeTableView.setRoot(new FamilyTree(new FamilyGroups()));
        } else if (contentMode == CONTENT_MODE_LGBK) {
            treeTableView.setRoot(new ConverterToPriceTable<>(CoreModule.getProductLgbkGroups().getFullTreeSet()));
        } else {
            treeTableView.setRoot(new FamilyTree(new FamilyGroups()));
        }
    }

    public String exportToString() {
        String result = "";
        for (TreeItem<PriceListContentTableItem> groupTreeItem : treeTableView.getRoot().getChildren()) {
            if (groupTreeItem.getValue().isPrice()) {
                result = exportTreeItemToString(result, groupTreeItem);
            } else {
                for (TreeItem<PriceListContentTableItem> subGroupTreeItem : groupTreeItem.getChildren()) {
                    if (subGroupTreeItem.getValue().isPrice()) {
                        result = exportTreeItemToString(result, subGroupTreeItem);
                    } else {
                        for (TreeItem<PriceListContentTableItem> treeItem : subGroupTreeItem.getChildren()) {
                            result = exportTreeItemToString(result, treeItem);
                        }
                    }
                }
            }
        }
        return result;
    }

    private String exportTreeItemToString(String result, TreeItem<PriceListContentTableItem> groupTreeItem) {
        PriceListContentItem plci = groupTreeItem.getValue().getContent();
        if (groupTreeItem.getValue().isPrice()) {
            if (plci instanceof ProductLgbk) {
                result = result.concat(LGBK_ITEM).concat(",").concat(String.valueOf(((ProductLgbk) plci).getId()));
            } else {
                result = result.concat(FAMILY_ITEM).concat(",").concat(String.valueOf(((ProductFamily) plci).getId()));
            }
            result = result.concat(";");
        }
        return result;
    }

    public void importFromString(String text) {
        if (text == null || text.isEmpty()) return;

        String[] items = text.split("\\;");
        for (String item : items) {
            String[] contentItem = item.split("\\,");

            if (!contentItem[1].matches("^\\d+$")) continue;
            PriceListContentItem plci;
            int id = Integer.parseInt(contentItem[1].trim());

            if (contentItem[0].equals(LGBK_ITEM)) {
                plci = CoreModule.getProductLgbks().getLgbkById(id);
            } else {
                plci = CoreModule.getProductFamilies().getFamilyById(id);
            }

//            boolean result = false;
            for (TreeItem<PriceListContentTableItem> treeItem : treeTableView.getRoot().getChildren()) {
                if (setPrice(treeItem, plci)) {
//                    result = true;
                    break;
                }

                for (TreeItem<PriceListContentTableItem> treeItem2 : treeItem.getChildren()) {
                    if (setPrice(treeItem2, plci)) {
//                        result = true;
                        break;
                    }
                }
            }
        }
    }

    private boolean setPrice(TreeItem<PriceListContentTableItem> treeItem, PriceListContentItem contentItem) {
        if (contentItem.equals(treeItem.getValue().getContent())) {
            treeItem.getValue().setPrice(true);
            return true;
        }

        for (TreeItem<PriceListContentTableItem> item : treeItem.getChildren()) {
            if (contentItem.equals(item.getValue().getContent())) {
                item.getValue().setPrice(true);
                return true;
            }
        }
        return false;
    }

    public TreeTableView<PriceListContentTableItem> getTreeTableView() {
        return treeTableView;
    }

    public ArrayList<PriceListContentItem> getPriceTreeItems(Product product) {


        return new ArrayList<>();
    }
}
