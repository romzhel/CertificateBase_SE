package ui_windows.main_window.file_import_window.te.conflicts_preprocessing;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.product.Product;
import ui_windows.product.Products;
import ui_windows.product.data.DataItem;

import java.util.List;
import java.util.stream.Collectors;

public class ConflictItemsTable {
    private static final Logger logger = LogManager.getLogger(ConflictItemsTable.class);

    private ConflictItemsTable() {
    }

    public static void init(TreeView<Object> tvConflictItems, List<ConflictItem> conflictItems) {
        ConflictItemsTable table = new ConflictItemsTable();
        table.initTable(tvConflictItems, conflictItems);
    }

    private void initTable(TreeView<Object> tvConflictItems, List<ConflictItem> conflictItems) throws RuntimeException {
        logger.trace("init Conflict items Table");
        tvConflictItems.setShowRoot(false);
        TreeItem<Object> root = new TreeItem<>("root");

        List<TreeItem<Object>> items = conflictItems.stream()
                .map(item -> {
                    logger.debug("preparing treeItem for {}", item);
                    TreeItem<Object> itemInfo = new TreeItem<>(item);

                    List<TreeItem<Object>> properties = item.getConflictPropertyMap().entrySet().stream()
                            .peek(entry -> logger.debug("entry: {}", entry))
                            .map(entry -> {
                                TreeItem<Object> itemProperty = new TreeItem<>(entry.getKey());
                                itemProperty.getChildren().addAll(entry.getValue().stream()
                                        .map(value -> new TreeItem<Object>(value))
                                        .collect(Collectors.toList()));
                                return itemProperty;
                            })
                            .peek(itemProperty -> logger.debug("treeItem: {}", itemProperty))
                            .collect(Collectors.toList());

                    logger.debug("add props {} to product {}", properties, item.getId());
                    itemInfo.getChildren().addAll(properties);

                    return itemInfo;
                })
                .collect(Collectors.toList());

        tvConflictItems.setCellFactory(new Callback<TreeView<Object>, TreeCell<Object>>() {
            @Override
            public TreeCell<Object> call(TreeView<Object> param) {
                return new TreeCell<Object>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || isEmpty()) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            if (item instanceof ConflictItem) {
                                setText(Products.getInstance().getProductByMaterial(((ConflictItem) item).getId()).toString());
                                setGraphic(null);
                            } else if (item instanceof DataItem) {
                                setText(((DataItem) item).getDisplayingName());
                                setGraphic(null);
                            } else if (item instanceof ConflictProperty) {
                                String id = ((ConflictItem) getTreeItem().getParent().getParent().getValue()).getId();
                                Product product = Products.getInstance().getProductByMaterial(id);
                                DataItem dataItem = (DataItem) getTreeItem().getParent().getValue();

                                ConflictProperty value = (ConflictProperty) item;
                                HBox cellBox = new HBox(10);
                                Label label = new Label(String.format("%s => %s (%s)", dataItem.getValue(product),
                                        value.getProperty().getValue().toString(),
                                        value.getProperty().getSource().toString()));
                                CheckBox checkBox = new CheckBox();
                                checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                                    logger.info("{} => {}", value, newValue);
                                    value.setSelected(newValue);
                                });

                                cellBox.getChildren().addAll(checkBox, label);
                                setText(null);
                                setGraphic(cellBox);
                            } else {
                                logger.warn("Unknown treeItem type '{}'", getTreeItem().getClass());
                            }
                        }
                    }
                };
            }
        });

        logger.debug("add conflict products {}", items);
        root.getChildren().addAll(items);

        tvConflictItems.setRoot(root);

        for (TreeItem<?> prop : root.getChildren()) {
            expandTreeView(prop);
        }
    }

    private void expandTreeView(TreeItem<?> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(true);
            for (TreeItem<?> child : item.getChildren()) {
                expandTreeView(child);
            }
        }
    }
}
