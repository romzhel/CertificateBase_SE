package ui_windows.main_window.filter_window_se;

import core.InitModule;
import core.Initializable;
import core.Module;
import core.SharedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.product_lgbk.ProductLgbkGroups;
import ui_windows.product.Product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static core.SharedData.*;
import static ui_windows.main_window.filter_window_se.FilterParameters_SE.*;
import static ui_windows.main_window.filter_window_se.ItemsSelection.ALL_ITEMS;
import static ui_windows.main_window.filter_window_se.ItemsSelection.PRICE_ITEMS;
import static ui_windows.product.data.DataItem.DATA_EMPTY;

public class Filter_SE implements Module, Initializable {
    private static final Logger logger = LogManager.getLogger(Filter_SE.class);
    private static Filter_SE instance;

    private Filter_SE() {
        SHD_DATA_SET.subscribe(this);
        SHD_FILTER_PARAMETERS.subscribe(this);
    }

    public static Filter_SE getInstance() {
        if (instance == null) {
            instance = new Filter_SE();
        }
        return instance;
    }

    @Override
    public void init() throws Exception {
        SHD_FILTER_PARAMETERS.setData(InitModule.class, new FilterParameters_SE(), NOT_NOTIFY);
    }

    public void apply() {
        Collection<Product> dataSet = SHD_DATA_SET.getData();
        FilterParameters_SE parameters = SHD_FILTER_PARAMETERS.getData();

        if (dataSet == null || parameters == null) {
            logger.warn("can't filtering - no dataset or parameters");
            return;
        }

        logger.debug("start filtering, try to find '{}' ...", parameters.getSearchText());
        long t1 = System.currentTimeMillis();

        parameters.clearComboBoxItems();

        List<Product> result = new ArrayList<>();
        Pattern pattern;
        try {
            pattern = Pattern.compile(
                    String.format(".*%s.*", parameters.getSearchText().replaceAll("\\*", ".*")),
                    Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            logger.warn("find text regex parse error {}", e.getMessage());
            pattern = Pattern.compile(
                    String.format(".*%s.*", parameters.getSearchText().replaceAll("[*()\\[\\]]", ".*")),
                    Pattern.CASE_INSENSITIVE);
        }

        boolean searchTextMatches;
        boolean priceMatches;
        boolean familyMatches;
        boolean lgbkMatches;
        boolean hierarchyMatch;
        boolean customMatches;

        long tMin = 999L;
        long tMax = 0L;
        long tAvr = 10L;

        for (Product product : dataSet) {
            searchTextMatches = pattern.matcher(product.getArticle()).matches() || pattern.matcher(product.getMaterial()).matches();
            priceMatches = parameters.getFilterItems() == null || parameters.getFilterItems() == ALL_ITEMS ||
                    parameters.getFilterItems() == PRICE_ITEMS && product.isPrice();
            familyMatches = parameters.getFamily() == ALL_FAMILIES ||
                    parameters.getFamily() == FAMILY_NOT_ASSIGNED && product.getProductFamily() == null ||
                    parameters.getFamily() == product.getProductFamily();
            lgbkMatches = parameters.getLgbk().getLgbk().equals(TEXT_ALL_ITEMS) ||
                    parameters.getLgbk() == LGBK_NO_DATA && (product.getLgbk() == null || product.getLgbk().isEmpty()) ||
                    parameters.getLgbk().getLgbk().equals(product.getLgbk());
            hierarchyMatch = parameters.getHierarchy().getHierarchy().equals(TEXT_ALL_ITEMS) ||
                    parameters.getHierarchy() == LGBK_NO_DATA && (product.getHierarchy() == null || product.getHierarchy().isEmpty()) ||
                    product.getHierarchy().contains(parameters.getHierarchy().getHierarchy().replaceAll("\\.", ""));
            customMatches = parameters.getCustomProperty() == DATA_EMPTY || parameters.getCustomValueMatcher().matches(
                    parameters.getCustomProperty().getValue(product).toString(), parameters.getCustomValue());

            if (searchTextMatches && priceMatches && familyMatches && lgbkMatches && hierarchyMatch && customMatches) {
                result.add(product);

                long t = System.currentTimeMillis();
                parameters.getFamilies().add(product.getProductFamilyDefValue(FAMILY_NOT_ASSIGNED));

                ProductLgbk productLgbk = new ProductLgbk(product);
                ProductLgbk lgbk = ProductLgbkGroups.getInstance().getLgbkAndParent(productLgbk).getLgbkItem();
                parameters.getLgbks().add(lgbk == null || lgbk.getLgbk().isEmpty() ? LGBK_NO_DATA : lgbk);
                parameters.getHierarchies().add(lgbk == null || lgbk.getHierarchy().isEmpty() ? LGBK_NO_DATA : lgbk);

                long tEl = System.currentTimeMillis() - t;
                tMin = Math.min(tMin, tEl);
                tMax = Math.max(tMin, tEl);
                tAvr = (tAvr + tEl) / 2;
            }
        }
        logger.debug("t filter parameters filling min/avg/max : {}/{}/{}", tMin, tAvr, tMax);
        logger.debug("finding process finished, results: {}, time elapsed: {} ms", result.size(), System.currentTimeMillis() - t1);

//        System.out.println(Utils.getExactTime() + " result arraylist filled");

//        print("families: ", parameters.getFamilies(), (f) -> f.getName());
//        print("lgbks: ", parameters.getLgbks(), (l) -> l.getLgbk());
//        print("hierarchies: ", parameters.getHierarchies(), (h) -> h.getHierarchy());

//        System.out.println(String.format("%s; prop: %s, value: %s, matcher: %s", this.getClass().getSimpleName(), parameters.getCustomProperty().name(),
//                parameters.getCustomValue(), parameters.getCustomValueMatcher().name()));

//        System.out.println(System.currentTimeMillis() - t1);
        SHD_DISPLAYED_DATA.setData(this.getClass(), result);
//        SHD_FILTER_PARAMETERS.setData(this.getClass(), parameters, this);

        FilterParameters_SE.getSearchBox().getTextBox().requestFocus();
    }

    private <T> void print(String title, TreeSet<T> items, Function<T, String> converter) {
        System.out.print(title + " list: ");
        for (T item : items) {
            System.out.print(converter.apply(item) + ", ");
        }
        System.out.println();
    }

    @Override
    public void refreshSubscribedData(SharedData sharedData, Object data) {
        apply();
    }

}
