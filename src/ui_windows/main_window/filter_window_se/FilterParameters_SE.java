package ui_windows.main_window.filter_window_se;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.data.DataItem;

import java.util.Arrays;
import java.util.TreeSet;

import static ui_windows.main_window.filter_window_se.CustomValueMatcher.START_WITH;
import static ui_windows.main_window.filter_window_se.ItemsSelection.ALL_ITEMS;
import static ui_windows.product.data.DataItem.*;

public class FilterParameters_SE {
    public final static String TEXT_TEMPLATE = "--- %s ---";
    public final static String TEXT_ALL_ITEMS = String.format(TEXT_TEMPLATE, "Все");
    public final static String TEXT_NOT_ASSIGNED = String.format(TEXT_TEMPLATE, "Не назначено");
    public final static String TEXT_NO_DATA = String.format(TEXT_TEMPLATE, "Не присвоено");
    public final static String TEXT_NO_SELECTED = String.format(TEXT_TEMPLATE, "Не выбрано");
    public final static ProductFamily ALL_FAMILIES = new ProductFamily(TEXT_ALL_ITEMS);
    public final static ProductFamily FAMILY_NOT_ASSIGNED = new ProductFamily(TEXT_NOT_ASSIGNED);
    public final static ProductLgbk ALL_LGBKS = new ProductLgbk(TEXT_ALL_ITEMS, TEXT_ALL_ITEMS);
    public final static ProductLgbk LGBK_NO_DATA = new ProductLgbk(TEXT_NO_DATA, TEXT_NO_DATA);
    public final static ProductLgbk LGBK_NOT_ASSIGNED = new ProductLgbk(TEXT_NOT_ASSIGNED, TEXT_NOT_ASSIGNED);
    public final static int CHANGE_NONE = -1;
    public final static int CHANGE_PRICE = 0;
    public final static int CHANGE_SEARCH_TEXT = 1;
    public final static int CHANGE_FAMILY = 2;
    public final static int CHANGE_LGBK = 3;
    public final static int CHANGE_HIERARCHY = 4;
    public final static int CHANGE_CUSTOM_PROPERTY = 5;
    public final static int CHANGE_CUSTOM_VALUE = 6;
    public final static int CHANGE_CUSTOM_VALUE_MATCHER = 7;

    private ItemsSelection filterItems;
    private ProductFamily family;
    private ProductLgbk lgbk;
    private ProductLgbk hierarchy;
    private DataItem customProperty;
    private String customValue;
    private CustomValueMatcher customValueMatcher;
    private String searchText;
    private IntegerProperty lastChange;
    private TreeSet<ProductFamily> families;
    private TreeSet<ProductLgbk> lgbks;
    private TreeSet<ProductLgbk> hierarchies;
    private TreeSet<DataItem> customProperties;

    public FilterParameters_SE() {
        filterItems = ALL_ITEMS;
        family = ALL_FAMILIES;
        lgbk = ALL_LGBKS;
        hierarchy = ALL_LGBKS;
        customProperty = DATA_EMPTY;
        customValue = "";
        customValueMatcher = START_WITH;
        searchText = "";
        lastChange = new SimpleIntegerProperty(-1);
//        lastChange.addListener((observable, oldValue, newValue) -> System.out.println("filter last change value = " + (int) newValue));

        families = new TreeSet<>((o1, o2) -> o1.getName().compareTo(o2.getName()));
        lgbks = new TreeSet<>((o1, o2) -> o1.getLgbk().compareTo(o2.getLgbk()));
        hierarchies = new TreeSet<>((o1, o2) -> o1.getHierarchy().compareTo(o2.getHierarchy()));
        customProperties = new TreeSet<>(Arrays.asList(DATA_EMPTY, DATA_COUNTRY, DATA_DCHAIN, DATA_RESPONSIBLE,
                DATA_CERTIFICATE, DATA_IN_WHICH_PRICE_LIST, DATA_TYPE_DESCRIPTION));
    }

    public FilterParameters_SE setItems(ItemsSelection selection) {
        filterItems = selection;
//        lastChange = CHANGE_PRICE;
        lastChange.set(CHANGE_PRICE);
        return this;
    }

    public FilterParameters_SE setProductFamily(ProductFamily family) {
        this.family = family;
//        lastChange = CHANGE_FAMILY;
        lastChange.set(CHANGE_FAMILY);
        return this;
    }

    public FilterParameters_SE setLgbk(ProductLgbk lgbk) {
        this.lgbk = lgbk;
//        lastChange = CHANGE_LGBK;
        lastChange.set(CHANGE_LGBK);
        return this;
    }

    public FilterParameters_SE setHierarchy(ProductLgbk lgbk) {
        hierarchy = lgbk;
//        lastChange = CHANGE_HIERARCHY;
        lastChange.set(CHANGE_HIERARCHY);
        return this;
    }

    public FilterParameters_SE setCustomProperty(DataItem dataItem) {
        customProperty = dataItem;
//        lastChange = CHANGE_CUSTOM_PROPERTY;
        lastChange.set(CHANGE_CUSTOM_PROPERTY);
        return this;
    }

    public FilterParameters_SE setCustomValue(String value) {
        customValue = value;
//        lastChange = CHANGE_CUSTOM_VALUE;
        lastChange.set(CHANGE_CUSTOM_VALUE);
        return this;
    }


    public FilterParameters_SE setCustomValueMatcher(CustomValueMatcher matcher) {
        customValueMatcher = matcher;
//        lastChange = CHANGE_CUSTOM_VALUE_MATCHER;
        lastChange.set(CHANGE_CUSTOM_VALUE_MATCHER);
        return this;
    }

    public FilterParameters_SE setSearchText(String text) {
        searchText = text;
        lastChange.set(CHANGE_SEARCH_TEXT);
        return this;
    }

    public ItemsSelection getFilterItems() {
        return filterItems;
    }

    public ProductFamily getFamily() {
        return family;
    }

    public ProductLgbk getLgbk() {
        return lgbk;
    }

    public ProductLgbk getHierarchy() {
        return hierarchy;
    }

    public DataItem getCustomProperty() {
        return customProperty;
    }

    public String getCustomValue() {
        return customValue;
    }

    public CustomValueMatcher getCustomValueMatcher() {
        return customValueMatcher;
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

    public int getLastChange() {
        return lastChange.get();
    }

    public TreeSet<ProductLgbk> getHierarchies() {
        return hierarchies;
    }

    public TreeSet<DataItem> getCustomProperties() {
        return customProperties;
    }

    @Override
    public String toString() {
        return String.format("items: %s, family: %s, lgbk: %s, hierarchy: %s, customPar: %s, customValue: %s, customCondition: %s," +
                        "searchText: %s",
                filterItems.toString(), family, lgbk, hierarchy, customProperty,
                customValue, customValueMatcher, searchText);
    }
}
