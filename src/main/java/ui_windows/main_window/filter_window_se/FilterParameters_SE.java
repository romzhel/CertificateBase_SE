package ui_windows.main_window.filter_window_se;

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
    public static SearchBox searchBox = new SearchBox();
    private Selector<?> changedSelector;
    private ItemsSelection filterItems;
    private ProductFamily family;
    private ProductLgbk lgbk;
    private ProductLgbk hierarchy;
    private DataItem customProperty;
    private String customValue;
    private CustomValueMatcher customValueMatcher;
    private Set<ProductFamily> families = new HashSet<>();
    private Set<ProductLgbk> lgbks = new TreeSet<>((o1, o2) -> o1.getLgbk().compareToIgnoreCase(o2.getLgbk()));
    private Set<ProductLgbk> hierarchies = new TreeSet<>((o1, o2) -> o1.getHierarchy().compareToIgnoreCase(o2.getHierarchy()));
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

        customProperties = new HashSet<>(Arrays.asList(DATA_EMPTY, DATA_COUNTRY, DATA_DCHAIN, DATA_RESPONSIBLE,
                /*DATA_CERTIFICATE,*/ DATA_IN_WHICH_PRICE_LIST, DATA_TYPE_DESCRIPTION, DATA_DESCRIPTION_RU, DATA_DESCRIPTION_EN,
                DATA_IS_BLOCKED, DATA_IS_PRICE_HIDDEN, DATA_COMMENT_PRICE, DATA_VENDOR));
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

        if (customProperty != DATA_EMPTY && !customValue.isEmpty()) {
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

    public static SearchBox getSearchBox() {
        return searchBox;
    }

    @Override
    public String toString() {
        return String.format("items: %s, family: %s, lgbk: %s, hierarchy: %s, customPar: %s, customValue: %s, customCondition: %s," +
                        "searchText: %s",
                filterItems.toString(), family, lgbk, hierarchy, customProperty,
                customValue, customValueMatcher, searchBox.getText());
    }
}
