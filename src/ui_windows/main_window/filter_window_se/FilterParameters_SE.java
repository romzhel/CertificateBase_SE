package ui_windows.main_window.filter_window_se;

import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.data.DataItem;

import java.util.TreeSet;

import static ui_windows.main_window.filter_window_se.CustomValueCondition.START_WITH;
import static ui_windows.main_window.filter_window_se.ItemsSelection.ALL_ITEMS;

public class FilterParameters_SE {
    public final static String TEXT_ALL_ITEMS = "--- Все ---";
    public final static ProductFamily ALL_FAMILIES = new ProductFamily(TEXT_ALL_ITEMS);
    public final static ProductLgbk ALL_LGBKS = new ProductLgbk(TEXT_ALL_ITEMS, TEXT_ALL_ITEMS);
    public final static int CHANGE_PRICE = 0;
    public final static int CHANGE_SEARCH_TEXT = 1;
    public final static int CHANGE_FAMILY = 2;
    public final static int CHANGE_LGBK = 3;
    public final static int CHANGE_HIERARCHY = 4;

    private ItemsSelection filterItems;
    private ProductFamily filterProductFamily;
    private ProductLgbk filterProductLgbk;
    private ProductLgbk filterProductHierarchy;
    private DataItem filterCustomProperty;
    private String filterCustomValue;
    private CustomValueCondition filterCustomCondition;
    private String searchText;
    private int lastChange;
    private TreeSet<ProductFamily> families;
    private TreeSet<ProductLgbk> lgbks;

    public FilterParameters_SE() {
        filterItems = ALL_ITEMS;
        filterProductFamily = ALL_FAMILIES;
        filterProductLgbk = null;
        filterProductHierarchy = null;
        filterCustomProperty = null;
        filterCustomValue = "";
        filterCustomCondition = START_WITH;
        searchText = "";
        lastChange = -1;
        families = new TreeSet<>((o1, o2) -> o1.getName().compareTo(o2.getName()));
        lgbks = new TreeSet<>((o1, o2) -> o1.getLgbk().compareTo(o2.getLgbk()));
    }

    public FilterParameters_SE setItems(ItemsSelection selection) {
        filterItems = selection;
        lastChange = CHANGE_PRICE;
        return this;
    }

    public FilterParameters_SE setProductFamily(ProductFamily family) {
        filterProductFamily = family;
        lastChange = CHANGE_FAMILY;
        return this;
    }

    public FilterParameters_SE setLgbk(ProductLgbk lgbk) {
        filterProductLgbk = lgbk;
        lastChange = CHANGE_LGBK;
        return this;
    }

    public FilterParameters_SE setHierarchy(ProductLgbk lgbk) {
        filterProductHierarchy = lgbk;
        lastChange = CHANGE_HIERARCHY;
        
        return this;
    }

    public FilterParameters_SE setCustomProperty(DataItem dataItem) {
        filterCustomProperty = dataItem;
        return this;
    }

    public FilterParameters_SE setCustomValue(String value) {
        filterCustomValue = value;
        return this;
    }

    public FilterParameters_SE setCustomCondition(CustomValueCondition condition) {
        filterCustomCondition = condition;
        return this;
    }

    public FilterParameters_SE setSearchText(String text) {
        searchText = text;
        return this;
    }

    public ItemsSelection getFilterItems() {
        return filterItems;
    }

    public ProductFamily getFilterProductFamily() {
        return filterProductFamily;
    }

    public ProductLgbk getFilterProductLgbk() {
        return filterProductLgbk;
    }

    public ProductLgbk getFilterProductHierarchy() {
        return filterProductHierarchy;
    }

    public DataItem getFilterCustomProperty() {
        return filterCustomProperty;
    }

    public String getFilterCustomValue() {
        return filterCustomValue;
    }

    public CustomValueCondition getFilterCustomCondition() {
        return filterCustomCondition;
    }

    public String getSearchText() {
        return searchText;
    }

    public TreeSet<ProductFamily> getFamilies() {
        return families;
    }

    public TreeSet<ProductLgbk> getLgbks() {
        return lgbks;
    }

    @Override
    public String toString() {
        return String.format("items: %s, family: %s, lgbk: %s, hierarchy: %s, customPar: %s, customValue: %s, customCondition: %s," +
                        "searchText: %s",
                filterItems.toString(), filterProductFamily, filterProductLgbk, filterProductHierarchy, filterCustomProperty,
                filterCustomValue, filterCustomCondition, searchText);
    }
}
