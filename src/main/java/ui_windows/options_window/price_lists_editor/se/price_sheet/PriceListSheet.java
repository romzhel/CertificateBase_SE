package ui_windows.options_window.price_lists_editor.se.price_sheet;

import files.price_to_excel.HierarchyGroup;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.util.Callback;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;
import ui_windows.options_window.order_accessibility_editor.OrdersAccessibility;
import ui_windows.options_window.price_lists_editor.se.PriceListContentTable;
import ui_windows.options_window.product_lgbk.LgbkAndParent;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.product_lgbk.ProductLgbkGroups;
import ui_windows.options_window.product_lgbk.ProductLgbks;
import ui_windows.product.Product;
import ui_windows.product.data.DataItem;
import ui_windows.product.data.DataSets;
import utils.twin_list_views.TwinListViews;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static ui_windows.options_window.price_lists_editor.se.PriceListContentTable.CONTENT_MODE_FAMILY;
import static ui_windows.options_window.price_lists_editor.se.PriceListContentTable.CONTENT_MODE_LGBK;

@Data
@Log4j2
public class PriceListSheet extends Tab {
    public static final int LANG_RU = 0;
    public static final int LANG_EN = 1;
    private TwinListViews<DataItem> columnsSelector;
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
    private boolean checkCert;
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
//            contentTable.importFromString(rs.getString("content_enums"));

            contentTable.initContentMode(CONTENT_MODE_LGBK);
            controller.rmiByLgbk.setSelected(true);
//            contentTable.importFromString(rs.getString("content_enums"));
            contentTable.fillGbkPriceMapFromString(rs.getString("content_enums"));
            contentTable.fillCompletePriceStructure();

            dchainSelector.setSelectedItemsFromString(rs.getString("dchain_enums"));
            discount = rs.getInt("discount");
            sortOrder = rs.getInt("sort_order") == 0 ? HierarchyGroup.SORT_MATERIAL : HierarchyGroup.SORT_ARTICLE;
            checkCert = rs.getBoolean("check_cert");

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
        contentTable.fillGbkPriceMapFromString(anotherInstance.contentTable.exportContentToString());
        contentTable.fillCompletePriceStructure();
        discount = anotherInstance.discount;
        sortOrder = anotherInstance.sortOrder;
        checkCert = anotherInstance.checkCert;

        initMainOptions();
    }

    private void getUi() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/priceSheet.fxml"));
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
        controller.cbxCheckCert.setSelected(checkCert);
    }

    private void initColumnsSelector() {
        List<DataItem> columns = new ArrayList<>();
        columns.addAll(Arrays.asList(DataSets.getDataItemsForPriceList()));

        columnsSelector = new TwinListViews<>(controller.pPriceColumns, columns);
        columnsSelector.setListViewsCellFactory(new Callback<ListView<DataItem>, ListCell<DataItem>>() {
            @Override
            public ListCell call(ListView<DataItem> param) {
                return new ListCell<DataItem>() {
                    @Override
                    protected void updateItem(DataItem item, boolean empty) {
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
            for (DataItem item : param) {
                result = result.concat(String.valueOf(item.getId())).concat(",");
            }
            return result.replaceAll("\\,$", "");
        });
        columnsSelector.setConvertFromText(param -> {
            ArrayList<DataItem> result = new ArrayList<>();
            for (String dataItemId : param.split("\\,")) {
                if (dataItemId.trim().matches("\\d+")) {
                    result.add(DataItem.getDataItemById(Integer.parseInt(dataItemId.trim())));
                }
            }
            return result;
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
        dchainSelector = new TwinListViews<>(controller.pPriceDchain, OrdersAccessibility.getInstance().getOrdersAccessibilityMap().values());
        dchainSelector.setListViewsCellFactory(new Callback<ListView<OrderAccessibility>, ListCell<OrderAccessibility>>() {
            @Override
            public ListCell<OrderAccessibility> call(ListView<OrderAccessibility> param) {
                return new ListCell<OrderAccessibility>() {
                    @Override
                    protected void updateItem(OrderAccessibility item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null && !empty) {
                            String description = item.getDescription();
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
            List<OrderAccessibility> result = new ArrayList<>();
            if (param != null && !param.isEmpty()) {
                for (String item : param.split("\\,")) {
                    result.add(OrdersAccessibility.getInstance().getOrderAccessibilityByStatusCode(item));
                }
            }
            return result;
        });
        dchainSelector.setConvertToText(param -> {
            String result = "";
            for (OrderAccessibility oa : param) {
                result = result.concat(oa.getStatusCode()).concat(",");
            }
            return result.replaceAll("\\,$", "");
        });
    }

    public boolean isInPrice(Product product) {
        contentTable.switchContentMode(CONTENT_MODE_LGBK);

        ProductLgbk productLgbk = null;
        ProductLgbk parentLgbk = null;
        try {
            productLgbk = ProductLgbks.getInstance().getLgbkByProduct(product);
            parentLgbk = ProductLgbks.getInstance().getGroupLgbkByName(productLgbk.getLgbk());
            Map<ProductLgbk, Boolean> priceMap = contentTable.getGbkInPriceMap();

            if (priceMap.getOrDefault(productLgbk, priceMap.getOrDefault(parentLgbk, false))) {
                boolean dchainMatchesSP = product.getDchain().trim().isEmpty() && product.isSpProduct();
                boolean dchainMatchesSets = product.getDchain().trim().isEmpty() && product.getLgbk().startsWith("RU5");

//                if (dchainMatchesSP || dchainMatchesSets) {
//                    return true;
//                } else {
                return dchainSelector.getSelectedItems().stream()
                        .anyMatch(oa -> product.getDchain().equals(oa.getStatusCode()));
//                }
            }
        } catch (Exception e) {
            log.error("in price looking error for {}, gbk/parent {}/{}", product, productLgbk, parentLgbk);
        }

        return false;
    }

    public boolean isGbkStructureAddedToPrice(ProductLgbk lgbk) {
        LgbkAndParent lap = ProductLgbkGroups.getInstance().getLgbkAndParent(lgbk);
        boolean isParentPrice = contentTable.getGbkInPriceMap().getOrDefault(lap.getLgbkParent(), false);
        boolean isItemPrice = contentTable.getGbkInPriceMap().getOrDefault(lap.getLgbkItem(), false);

        return isParentPrice || isItemPrice;
    }

    public void uploadFromUI() {
        sheetName = controller.tfSheetName.getText();
        String initRow = controller.tfInitialRow.getText();
        initialRow = initRow.matches("^\\d+$") ? Integer.parseInt(initRow) : -1;
        language = controller.rbLangRu.isSelected() ? LANG_RU : LANG_EN;
        String costDiscount = controller.tfDiscount.getText();
        discount = costDiscount.matches("^\\d+$") ? Integer.parseInt(costDiscount) : 0;
        sortOrder = controller.rbOrderMaterial.isSelected() ? HierarchyGroup.SORT_MATERIAL : HierarchyGroup.SORT_ARTICLE;
        checkCert = controller.cbxCheckCert.isSelected();
    }

    @Override
    public String toString() {
        return "PriceListSheet{" +
                "sheetName='" + sheetName + '\'' +
                '}';
    }

    public void refreshContent() {
        contentTable.refresh();
    }
}
