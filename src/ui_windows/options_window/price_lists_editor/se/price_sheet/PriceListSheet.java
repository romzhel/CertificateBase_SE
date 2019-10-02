package ui_windows.options_window.price_lists_editor.se.price_sheet;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.util.Callback;
import utils.twin_list_views.TwinListViews;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static ui_windows.main_window.file_import_window.NamesMapping.*;

public class PriceListSheet extends Tab {
    private int sheetId;
    private static final int LANG_RU = 0;
    private static final int LANG_EN = 1;
    private int language;
    private String sheetName;
    private int initialRow = -1;
    private int contentMode = -1;
    private int leadTimeCorrection;
    private boolean groupNameDisplaying = true;
    private PriceListSheetController controller;
    TwinListViews<String> columnsSelector;


    public PriceListSheet(String title) {
        super();
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("priceSheet.fxml"));
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

        setText(title);
        init();
    }

    public PriceListSheet(ResultSet rs) {
        try {
            sheetName = rs.getString("name");
            language = rs.getInt("language");
            initialRow = rs.getInt("init_row");
            contentMode = rs.getInt("content_mode");
            leadTimeCorrection = rs.getInt("lead_time_correction");
            groupNameDisplaying = rs.getBoolean("group_names_displaying");
            columnsSelector.setSelectedItems(rs.getString("column_enums").split("\\,"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void init() {
        ArrayList<String> columns = new ArrayList<>();
        columns.add(DESC_ORDER_NUMBER);
        columns.add(DESC_ARTICLE);
        columns.add(DESC_DESCRIPTION_RU);
        columns.add(DESC_DESCRIPTION_EN);
        columns.add(DESC_LOCAL_PRICE);
        columns.add(DESC_LEADTIME);
        columns.add(DESC_MIN_ORDER);
        columns.add(DESC_LGBK);
        columns.add(DESC_WEIGHT);

        columnsSelector = new TwinListViews<>(controller.pPriceColumns, columns);
        columnsSelector.setListViewsCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null && !empty) {
                            setText(item);
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        columnsSelector.setListViewsAllComparator((o1, o2) -> o1.compareToIgnoreCase(o2));
        columnsSelector.setListViewsSelectedComparator(null);
        columnsSelector.setConvertToText(param -> {
            String result = "";
            for (String item : param) {
                result = result.concat(item).concat(",");
            }
            result.replaceAll("\\,$", "");

            return result;
        });


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

    public TwinListViews<String> getColumnsSelector() {
        return columnsSelector;
    }

    public void setColumnsSelector(TwinListViews<String> columnsSelector) {
        this.columnsSelector = columnsSelector;
    }

    public int getSheetId() {
        return sheetId;
    }

    public void setSheetId(int sheetId) {
        this.sheetId = sheetId;
    }
}
