package ui_windows.options_window.price_lists_editor.se.price_sheet;

import core.CoreModule;
import files.price_to_excel.HierarchyGroup;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.util.Callback;
import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;
import ui_windows.options_window.price_lists_editor.se.PriceListColumn;
import ui_windows.options_window.price_lists_editor.se.PriceListColumns;
import ui_windows.options_window.price_lists_editor.se.PriceListContentTable;
import ui_windows.options_window.price_lists_editor.se.PriceListContentTableItem;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.Product;
import utils.twin_list_views.TwinListViews;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;

import static ui_windows.options_window.price_lists_editor.se.PriceListContentTable.CONTENT_MODE_FAMILY;
import static ui_windows.options_window.price_lists_editor.se.PriceListContentTable.CONTENT_MODE_LGBK;

public class PriceListSheet extends Tab {
    public static final int LANG_RU = 0;
    public static final int LANG_EN = 1;
    private TwinListViews<PriceListColumn> columnsSelector;
    private TwinListViews<OrderAccessibility> dchainSelector;
    private PriceListContentTable contentTable;
    private int sheetId = -1;
    private int priceListId = -1;
    private int language;
    private String sheetName;
    private int initialRow = -1;
    private int contentMode = 0;
    private int leadTimeCorrection;
    private boolean groupNameDisplaying = true;
    private int discount;
    private PriceListSheetController controller;
    private Comparator<Product> sortOrder;

    public PriceListSheet(String title) {
        super();

        setText(title);
        init();
        contentTable.initContentMode(CONTENT_MODE_FAMILY);
    }

    public PriceListSheet(ResultSet rs) {
        init();
        try {
            sheetId = rs.getInt("id");
            sheetName = rs.getString("name");
            priceListId = rs.getInt("price_list_id");
            language = rs.getInt("language");
            initialRow = rs.getInt("init_row");
            contentMode = rs.getInt("content_mode");
            contentTable.initContentMode(rs.getInt("content_mode"));
            leadTimeCorrection = rs.getInt("lead_time_correction");
            groupNameDisplaying = rs.getBoolean("group_names_displaying");
            columnsSelector.setSelectedItemsFromString(rs.getString("column_enums"));
//            contentString = rs.getString("content_enums");
            contentTable.importFromString(rs.getString("content_enums"));

            contentTable.initContentMode(contentMode);
            if (contentMode == CONTENT_MODE_LGBK) {
                controller.rmiByLgbk.setSelected(true);
            } else {
                controller.rmiByFamily.setSelected(true);
            }
            contentTable.importFromString(rs.getString("content_enums"));

            dchainSelector.setSelectedItemsFromString(rs.getString("dchain_enums"));
            discount = rs.getInt("discount");
            sortOrder = rs.getInt("sort_order") == 0 ? HierarchyGroup.SORT_MATERIAL : HierarchyGroup.SORT_ARTICLE;

            initMainOptions();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public PriceListSheet(PriceListSheet anotherInstance) {
        init();
        sheetId = anotherInstance.sheetId;
        priceListId = anotherInstance.priceListId;
        language = anotherInstance.language;
        sheetName = anotherInstance.sheetName;
        initialRow = anotherInstance.initialRow;
        contentMode = anotherInstance.contentMode;
        leadTimeCorrection = anotherInstance.leadTimeCorrection;
        groupNameDisplaying = anotherInstance.groupNameDisplaying;
        columnsSelector.setSelectedItemsFromString(anotherInstance.columnsSelector.getSelectedItemsAsString());
        dchainSelector.setSelectedItemsFromString(anotherInstance.dchainSelector.getSelectedItemsAsString());

        contentTable.initContentMode(anotherInstance.getContentMode());
        if (anotherInstance.contentMode == CONTENT_MODE_LGBK) {
            controller.rmiByLgbk.setSelected(true);
        } else {
            controller.rmiByFamily.setSelected(true);
        }
        contentTable.importFromString(anotherInstance.contentTable.exportToString());
        discount = anotherInstance.discount;
        sortOrder = anotherInstance.sortOrder;

        initMainOptions();
    }

    private void getUi() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("priceSheet.fxml"));
        fxmlLoader.setControllerFactory(param -> {
            controller = new PriceListSheetController();
            return controller;
        });

        try {
            super.setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        getUi();
        initColumnsSelector();
        initContentTable();
        initDchainSelector();
    }

    private void initMainOptions() {
        if (language == LANG_RU) {
            controller.rbLangRu.setSelected(true);
        } else {
            controller.rbLangEn.setSelected(true);
        }
        controller.tfSheetName.setText(sheetName);
        if (initialRow > 0) {
            controller.tfInitialRow.setText(String.valueOf(initialRow));
        }
        if (discount > 0) {
            controller.tfDiscount.setText(String.valueOf(discount));
        }

        controller.rbOrderMaterial.setSelected(sortOrder == HierarchyGroup.SORT_MATERIAL);
        controller.rbOrderArticle.setSelected(sortOrder == HierarchyGroup.SORT_ARTICLE);
    }

    private void initColumnsSelector() {
        ArrayList<PriceListColumn> columns = new PriceListColumns().getColumns();

        columnsSelector = new TwinListViews<>(controller.pPriceColumns, columns);
        columnsSelector.setListViewsCellFactory(new Callback<ListView<PriceListColumn>, ListCell<PriceListColumn>>() {
            @Override
            public ListCell call(ListView<PriceListColumn> param) {
                return new ListCell<PriceListColumn>() {
                    @Override
                    protected void updateItem(PriceListColumn item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null && !empty) {
                            setText(item.getDisplayingName());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        columnsSelector.setListViewsAllComparator((o1, o2) -> o1.getDisplayingName().compareToIgnoreCase(o2.getDisplayingName()));
        columnsSelector.setListViewsSelectedComparator(null);
        columnsSelector.setConvertToText(param -> {
            String result = "";
            for (PriceListColumn item : param) {
                result = result.concat(item.getDisplayingName()).concat(",");
            }
            result = result.replaceAll("\\,$", "");

            return result;
        });
        columnsSelector.setConvertFromText(new Callback<String, ArrayList<PriceListColumn>>() {
            @Override
            public ArrayList<PriceListColumn> call(String param) {
                ArrayList<PriceListColumn> result = new ArrayList<>();
                for (String columnName : param.split("\\,")) {
                    for (PriceListColumn plc : columns) {
                        if (plc.getDisplayingName().equals(columnName)) {
                            result.add(plc);
                            break;
                        }
                    }
                }
                return result;
            }
        });
    }

    private void initContentTable() {
        contentTable = new PriceListContentTable(controller.ttvPriceContent);
        controller.rmiByLgbk.selectedProperty().addListener((observable, oldValue, newValue) -> {
            int newMode = newValue ? CONTENT_MODE_LGBK : CONTENT_MODE_FAMILY;
            contentMode = newMode;
            contentTable.switchContentMode(newMode);
        });
    }

    private void initDchainSelector() {
        dchainSelector = new TwinListViews<>(controller.pPriceDchain, CoreModule.getOrdersAccessibility().getItems());
        dchainSelector.setListViewsCellFactory(new Callback<ListView<OrderAccessibility>, ListCell<OrderAccessibility>>() {
            @Override
            public ListCell<OrderAccessibility> call(ListView<OrderAccessibility> param) {
                return new ListCell<OrderAccessibility>() {
                    @Override
                    protected void updateItem(OrderAccessibility item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null && !empty) {
                            String description = item.getDescriptionRu().isEmpty() ? item.getDescriptionEn() : item.getDescriptionRu();
                            setText("[" + item.getStatusCode() + "] " + description);
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        Comparator<OrderAccessibility> dchainComparator = (o1, o2) -> o1.getStatusCode().compareTo(o2.getStatusCode());
        dchainSelector.setListViewsAllComparator(dchainComparator);
        dchainSelector.setListViewsSelectedComparator(dchainComparator);
        dchainSelector.setConvertFromText(param -> {
            ArrayList<OrderAccessibility> result = new ArrayList<>();
            if (param != null && !param.isEmpty()) {
                for (String item : param.split("\\,")) {
                    result.add(CoreModule.getOrdersAccessibility().getOrderAccessibilityByStatusCode(item));
                }
            }
            return result;
        });
        dchainSelector.setConvertToText(param -> {
            String result = "";
            for (OrderAccessibility oa : param) {
                result = result.concat(oa.getStatusCode()).concat(",");
            }
            result = result.replaceAll("\\,$", "");

            return result;
        });
    }

    public boolean isInPrice(Product product) {
        for (TreeItem<PriceListContentTableItem> groupTreeItem : contentTable.getTreeTableView().getRoot().getChildren()) {
            if (groupTreeItem.getValue().getContent() instanceof ProductLgbk) {
                if (((ProductLgbk) groupTreeItem.getValue().getContent()).getLgbk().equals(product.getLgbk())) {

                    for (TreeItem<PriceListContentTableItem> treeItem : groupTreeItem.getChildren()) {
                        if (((ProductLgbk) treeItem.getValue().getContent()).compare(new ProductLgbk(product))) {
                            boolean isLgbkMatch = treeItem.getValue().isPrice() || treeItem.getParent().getValue().isPrice();
                            boolean isDchainMatch = false;

                            for (OrderAccessibility oa : dchainSelector.getSelectedItems()) {
                                boolean dchainMatches = product.getDchain().equals(oa.getStatusCode());
                                boolean dchainMatchesSP = product.getDchain().trim().isEmpty() && oa.getStatusCode().trim().isEmpty() &&
                                        isSpProduct(product);

                                if (dchainMatches || dchainMatchesSP) {
                                    isDchainMatch = true;
                                    break;
                                }
                            }

                            if (isLgbkMatch && isDchainMatch) {
                                return true;
                            }
                        }
                    }

                }
            }
        }

        return false;
    }

    private boolean isSpProduct(Product product) {
        int id = CoreModule.getProductLgbks().getFamilyIdByLgbk(new ProductLgbk(product.getLgbk(), product.getHierarchy()));
        boolean productFamId = product.getFamily() == 24;
        return id == 24 || productFamId;
    }

    public void uploadFromUI() {
        sheetName = controller.tfSheetName.getText();
        String initRow = controller.tfInitialRow.getText();
        initialRow = initRow.matches("^\\d+$") ? Integer.parseInt(initRow) : -1;
        language = controller.rbLangRu.isSelected() ? LANG_RU : LANG_EN;
        String costDiscount = controller.tfDiscount.getText();
        discount = costDiscount.matches("^\\d+$") ? Integer.parseInt(costDiscount) : 0;
        sortOrder = controller.rbOrderMaterial.isSelected() ? HierarchyGroup.SORT_MATERIAL : HierarchyGroup.SORT_ARTICLE;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public int getInitialRow() {
        return initialRow;
    }

    public void setInitialRow(int initialRow) {
        this.initialRow = initialRow;
    }

    public int getContentMode() {
        return contentMode;
    }

    public void setContentMode(int contentMode) {
        this.contentMode = contentMode;
    }

    public int getLeadTimeCorrection() {
        return leadTimeCorrection;
    }

    public void setLeadTimeCorrection(int leadTimeCorrection) {
        this.leadTimeCorrection = leadTimeCorrection;
    }

    public boolean isGroupNameDisplaying() {
        return groupNameDisplaying;
    }

    public void setGroupNameDisplaying(boolean groupNameDisplaying) {
        this.groupNameDisplaying = groupNameDisplaying;
    }

    public PriceListSheetController getController() {
        return controller;
    }

    public void setController(PriceListSheetController controller) {
        this.controller = controller;
    }

    public TwinListViews<PriceListColumn> getColumnsSelector() {
        return columnsSelector;
    }

    public void setColumnsSelector(TwinListViews<PriceListColumn> columnsSelector) {
        this.columnsSelector = columnsSelector;
    }

    public int getSheetId() {
        return sheetId;
    }

    public void setSheetId(int sheetId) {
        this.sheetId = sheetId;
    }

    public int getPriceListId() {
        return priceListId;
    }

    public void setPriceListId(int priceListId) {
        this.priceListId = priceListId;
    }

    public TwinListViews<OrderAccessibility> getDchainSelector() {
        return dchainSelector;
    }

    public PriceListContentTable getContentTable() {
        return contentTable;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public Comparator<Product> getSortOrder() {
        return sortOrder;
    }
}
