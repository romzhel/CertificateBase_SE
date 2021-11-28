package ui_windows.main_window.filter_window_se;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import ui.components.SearchBox;
import ui_windows.options_window.families_editor.ProductFamilies;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.data.DataItem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static ui_windows.main_window.filter_window_se.CustomValueMatcher.START_WITH;
import static ui_windows.main_window.filter_window_se.ItemsSelection.PRICE_ITEMS;
import static ui_windows.options_window.families_editor.ProductFamilies.UNKNOWN;
import static ui_windows.product.data.DataItem.*;

@Data
@Log4j2
public class FilterParameters_SE {
    public final static String TEXT_TEMPLATE = "--- %s ---";
    public final static String TEXT_ALL_ITEMS = String.format(TEXT_TEMPLATE, "Все");
    public final static ProductFamily ALL_FAMILIES = new ProductFamily(TEXT_ALL_ITEMS);
    public final static ProductLgbk ALL_LGBKS = new ProductLgbk(TEXT_ALL_ITEMS, TEXT_ALL_ITEMS);
    public final static String TEXT_NOT_ASSIGNED = String.format(TEXT_TEMPLATE, "Не назначено");
    public final static ProductFamily FAMILY_NOT_ASSIGNED = new ProductFamily(TEXT_NOT_ASSIGNED);
    public final static ProductLgbk LGBK_NOT_ASSIGNED = new ProductLgbk(TEXT_NOT_ASSIGNED, TEXT_NOT_ASSIGNED);
    public final static String TEXT_NO_DATA = String.format(TEXT_TEMPLATE, "Не присвоено");
    public final static ProductLgbk LGBK_NO_DATA = new ProductLgbk(TEXT_NO_DATA, TEXT_NO_DATA);
    public final static String TEXT_NO_SELECTED = String.format(TEXT_TEMPLATE, "Не выбрано");
    public final static int CHANGE_NONE = -1;
    public final static int CHANGE_GLOBAL = 0;
    public final static int CHANGE_PRICE = 1;
    public final static int CHANGE_SEARCH_TEXT = 2;
    public final static int CHANGE_FAMILY = 3;
    public final static int CHANGE_LGBK = 4;
    public final static int CHANGE_HIERARCHY = 5;
    public final static int CHANGE_CUSTOM_PROPERTY = 6;
    public final static int CHANGE_CUSTOM_VALUE = 7;
    public final static int CHANGE_CUSTOM_VALUE_MATCHER = 8;
    public static SearchBox searchBox = new SearchBox();
    private ItemsSelection filterItems;
    private ProductFamily family;
    private ProductLgbk lgbk;
    private ProductLgbk hierarchy;
    private DataItem customProperty;
    private String customValue;
    private CustomValueMatcher customValueMatcher;
    private IntegerProperty lastChange;
    private Set<ProductFamily> families;
    private Set<ProductLgbk> lgbks;
    private Set<ProductLgbk> hierarchies;
    private Set<DataItem> customProperties;

    private FilterPredicate filterPredicate;

    public FilterParameters_SE() {
        filterItems = PRICE_ITEMS;
        family = ALL_FAMILIES;
        lgbk = ALL_LGBKS;
        hierarchy = ALL_LGBKS;
        customProperty = DATA_EMPTY;
        customValue = "";
        customValueMatcher = START_WITH;
        lastChange = new SimpleIntegerProperty(-1);
//        lastChange.addListener((observable, oldValue, newValue) -> System.out.println("filter last change value = " + (int) newValue));

        families = new HashSet<>();
        lgbks = new TreeSet<>((o1, o2) -> o1.getLgbk().compareToIgnoreCase(o2.getLgbk()));
        hierarchies = new TreeSet<>((o1, o2) -> o1.getHierarchy().compareToIgnoreCase(o2.getHierarchy()));
        customProperties = new HashSet<>(Arrays.asList(DATA_EMPTY, DATA_COUNTRY, DATA_DCHAIN, DATA_RESPONSIBLE,
                /*DATA_CERTIFICATE,*/ DATA_IN_WHICH_PRICE_LIST, DATA_TYPE_DESCRIPTION, DATA_DESCRIPTION_RU, DATA_DESCRIPTION_EN,
                DATA_IS_BLOCKED, DATA_IS_PRICE_HIDDEN, DATA_COMMENT_PRICE, DATA_VENDOR));
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
        lastChange.set(CHANGE_SEARCH_TEXT);
        return this;
    }

    public void clearComboBoxItems() {
        families.clear();
        families.add(ALL_FAMILIES);

        lgbks.clear();
        lgbks.add(ALL_LGBKS);

        hierarchies.clear();
        hierarchies.add(ALL_LGBKS);
    }

    public void calcFilterPredicate() {
        this.filterPredicate = null;

        String searchText = searchBox.getText();
        if (!searchText.isEmpty()) {
            Pattern pattern;
            try {
                pattern = Pattern.compile(
                        String.format(".*%s.*", searchText.replaceAll("\\*", ".*")),
                        Pattern.CASE_INSENSITIVE);
            } catch (PatternSyntaxException e) {
                log.warn("find text regex parse error {}", e.getMessage());
                pattern = Pattern.compile(
                        String.format(".*%s.*", searchText.replaceAll("[*()\\[\\]]", ".*")),
                        Pattern.CASE_INSENSITIVE);
            }

            Pattern finalPattern = pattern;
            addFilterPredicate(new FilterPredicate(product -> finalPattern.matcher(product.getArticle()).matches() || finalPattern.matcher(product.getMaterial()).matches()));
        }

        if (filterItems == PRICE_ITEMS) {
            addFilterPredicate(new FilterPredicate(product -> product.getPrice() && !product.getBlocked()));
        }

        if (family != ALL_FAMILIES) {
            addFilterPredicate(new FilterPredicate(product -> {
                ProductFamily pf = ProductFamilies.getInstance().getProductFamily(product);
                return family == pf || family == FAMILY_NOT_ASSIGNED && pf == UNKNOWN;
            }));
        }

        if (!lgbk.getLgbk().equals(TEXT_ALL_ITEMS)) {
            addFilterPredicate(new FilterPredicate(product -> lgbk.getLgbk().equals(product.getLgbk()) ||
                    lgbk == LGBK_NO_DATA && (product.getLgbk() == null || product.getLgbk().isEmpty())));
        }

        if (!hierarchy.getHierarchy().equals(TEXT_ALL_ITEMS)) {
            addFilterPredicate(new FilterPredicate(product -> product.getHierarchy().contains(hierarchy.getHierarchy().replaceAll("\\.", "")) ||
                    hierarchy == LGBK_NO_DATA && (product.getHierarchy() == null || product.getHierarchy().isEmpty())));
        }

        if (customProperty != null && !customValue.isEmpty()) {
            addFilterPredicate(new FilterPredicate(product -> customValueMatcher.matches(
                    customProperty.getValue(product).toString(), customValue)));
        }
    }

    public void addFilterPredicate(FilterPredicate filterPredicate) {
        if (this.filterPredicate == null) {
            this.filterPredicate = filterPredicate;
        } else {
            this.filterPredicate.addPredicate(filterPredicate);
        }
    }

    /*public ItemsSelection getFilterItems() {
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
        return searchBox.getText();
    }*/

    public static SearchBox getSearchBox() {
        return searchBox;
    }

    /*public Set<ProductFamily> getFamilies() {
        return families;
    }

    public Set<ProductLgbk> getLgbks() {
        return lgbks;
    }*/

    public int getLastChange() {
        return lastChange.get();
    }

    /*public Set<ProductLgbk> getHierarchies() {
        return hierarchies;
    }

    public Set<DataItem> getCustomProperties() {
        return customProperties;
    }

    public void setLastChange(int lastChange) {
        this.lastChange.set(lastChange);
    }*/

    @Override
    public String toString() {
        return String.format("items: %s, family: %s, lgbk: %s, hierarchy: %s, customPar: %s, customValue: %s, customCondition: %s," +
                        "searchText: %s",
                filterItems.toString(), family, lgbk, hierarchy, customProperty,
                customValue, customValueMatcher, searchBox.getText());
    }
}
