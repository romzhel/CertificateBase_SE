package ui_windows.main_window.file_import_window.te.conflicts_preprocessing;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.product.Product;
import ui_windows.product.Products;
import ui_windows.product.data.DataItem;
import utils.comparation.products.ProductNameResolver;

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
                    Product realProduct = Products.getInstance().getProductByResolvedMaterial(
                            ProductNameResolver.resolve(item.getCalculatedItem().getMaterial()));
                    TreeItem<Object> itemInfo = new TreeItem<>(realProduct);

                    List<TreeItem<Object>> properties = item.getConflictValues().entrySet().stream()
                            .peek(entry -> logger.debug("entry: {}", entry))
                            .map(entry -> {
                                TreeItem<Object> itemProperty = new TreeItem<>(entry.getKey());
                                itemProperty.getChildren().addAll(entry.getValue().stream()
                                        .map(value -> {
                                            value.setSource(item.getCalculatedItem().getImportDataSheet());
                                            return new TreeItem<Object>(value);
                                        })
                                        .collect(Collectors.toList()));
                                return itemProperty;
                            })
                            .peek(itemProperty -> logger.debug("treeItem: {}", itemProperty))
                            .collect(Collectors.toList());

                    logger.debug("add props {} to product {}", properties, realProduct);
                    itemInfo.getChildren().addAll(properties);

                    return itemInfo;
                })
                .collect(Collectors.toList());

        logger.debug("add conflict products {}", items);
        root.getChildren().addAll(items);

        tvConflictItems.setRoot(root);

        /*for (ConflictItem conflictItem : conflictItems) {
            ItemInfo itemInfo = new ItemInfo(conflictItem);

            for (Map.Entry<DataItem, List<ConflictItemValue>> dataItemEntry : conflictItem.getConflictValues().entrySet()) {
                ItemProperty itemProperty = new ItemProperty(conflictItem);

                for (ConflictItemValue value : dataItemEntry.getValue()) {
                    ItemValue itemValue = new ItemValue(conflictItem);
                    itemProperty.getChildren().add(itemValue);
                }

                itemInfo.getChildren().add(itemProperty);
            }


            root.getChildren().add(itemInfo);
        }*/

        tvConflictItems.setCellFactory(new Callback<TreeView<Object>, TreeCell<Object>>() {
            @Override
            public TreeCell<Object> call(TreeView<Object> param) {
                return new TreeCell<Object>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);

                        if (isEmpty()) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            if (item instanceof Product) {
                                setText(item.toString());
                                setGraphic(null);
                            } else if (item instanceof DataItem) {
                                setText(((DataItem) item).getDisplayingName());
                                setGraphic(null);
                            } else if (item instanceof ConflictItemValue) {
                                Product product = (Product) getTreeItem().getParent().getParent().getValue();
                                DataItem dataItem = (DataItem) getTreeItem().getParent().getValue();

                                ConflictItemValue value = (ConflictItemValue) item;
                                HBox cellBox = new HBox(10);
                                Label label = new Label(String.format("%s => %s (%s/%s)", dataItem.getValue(product),
                                        value.getValue().toString(), value.getSource().getFileName(), value.getSource().getSheetName()));
                                CheckBox checkBox = new CheckBox();
                                checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                                    logger.info("{} => {}", value, newValue);
                                    value.setSelected(newValue);
                                });

                                cellBox.getChildren().addAll(label, checkBox);
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
    }
}
