package ui_windows.options_window.price_lists_editor.se;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.util.Callback;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.product_lgbk.ProductLgbkGroups;
import ui_windows.options_window.product_lgbk.ProductLgbks;

import java.util.*;
import java.util.stream.Collectors;

import static ui_windows.options_window.product_lgbk.ProductLgbk.GROUP_NODE;
import static ui_windows.options_window.product_lgbk.ProductLgbk.ROOT_NODE;

@Log4j2
public class PriceListContentTable {
    public static final int CONTENT_MODE_FAMILY = 0;
    public static final int CONTENT_MODE_LGBK = 1;
    private static final String FAMILY_ITEM = "0";
    private static final String LGBK_ITEM = "1";
    private int contentMode;
    private TreeTableView<PriceListContentTableItem> treeTableView;
    private Map<ProductLgbk, Boolean> gbkInPriceMap = new HashMap<>();

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
                        int lgbkGroupSize = ProductLgbkGroups.getInstance().getTreeItem((ProductLgbk) lgbk.getValue().getContent()).getChildren().size();
                        int currentGroupSize = lgbk.getChildren().size();

                        if (lgbkGroupSize == currentGroupSize) {
                            lgbk.getValue().setPrice(newValue);
                            gbkInPriceMap.put((ProductLgbk) lgbk.getValue().getContent(), true);
                        } else {
                            for (TreeItem<PriceListContentTableItem> hierarchy : lgbk.getChildren()) {
                                hierarchy.getValue().setPrice(newValue);
                                gbkInPriceMap.put((ProductLgbk) hierarchy.getValue().getContent(), true);
                            }
                        }
                    }
                } else if (currItem.getContent() instanceof ProductLgbk) {
                    currItem.setPrice(newValue);
                    gbkInPriceMap.put((ProductLgbk) currItem.getContent(), true);
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

    public void initContentMode(int contentMode) {
       /* if (contentMode == CONTENT_MODE_FAMILY) {
            treeTableView.setRoot(new FamilyTree(new FamilyGroups()));
            contentMode = CONTENT_MODE_FAMILY;
        } else*/
        if (contentMode == CONTENT_MODE_LGBK) {
            treeTableView.setRoot(new ConverterToPriceTable<>(ProductLgbkGroups.getInstance().getFullTreeSet()));
            contentMode = CONTENT_MODE_LGBK;
        } else {
            treeTableView.setRoot(new FamilyTree(new FamilyGroups()));
            contentMode = CONTENT_MODE_FAMILY;
        }
    }

    public void switchContentMode(int newMode) {
//        if (newMode != contentMode) {
//        String content = exportToString();
        initContentMode(newMode);
//        importFromString(content);
//        }
        fillCompletePriceStructure();
    }

    public String exportContentToString() {
        List<String> priceContent = gbkInPriceMap.entrySet().stream()
                .filter(entry -> entry.getKey() != null)
                .filter(Map.Entry::getValue)
                .map(entry -> "1,".concat(String.valueOf(entry.getKey().getId())))
                .collect(Collectors.toList());
        String result = Strings.join(priceContent, ';');
        return result;
    }

    public void fillGbkPriceMapFromString(String text) {
        if (text == null || text.isEmpty()) return;

        Arrays.stream(text.split(";"))
                .filter(item -> item.split(",")[0].equals(LGBK_ITEM))
                .map(item -> ProductLgbks.getInstance().getLgbkById(Integer.parseInt(item.split(",")[1])))
                .filter(Objects::nonNull)
                .forEach(lgbk -> gbkInPriceMap.put(lgbk, true));
    }

    public void fillPriceStructure(TreeItem<PriceListContentTableItem> root) {
        for (TreeItem<PriceListContentTableItem> treeItem : root.getChildren()) {
            if (!treeItem.isLeaf()) {
                fillPriceStructure(treeItem);
            }

            if (treeItem.getValue().getContent() instanceof ProductLgbk) {
                boolean pr = gbkInPriceMap.getOrDefault((ProductLgbk) treeItem.getValue().getContent(), false);
                treeItem.getValue().setPrice(pr);
            }
        }

        treeTableView.refresh();
    }

    public void fillCompletePriceStructure() {
        fillPriceStructure(treeTableView.getRoot());
    }

    public void refresh() {
        initContentMode(CONTENT_MODE_LGBK);
        fillCompletePriceStructure();
    }

    public TreeTableView<PriceListContentTableItem> getTreeTableView() {
        return treeTableView;
    }

    public int getContentMode() {
        return contentMode;
    }

    public void setContentMode(int contentMode) {
        this.contentMode = contentMode;
    }

    public Map<ProductLgbk, Boolean> getGbkInPriceMap() {
        return gbkInPriceMap;
    }
}
