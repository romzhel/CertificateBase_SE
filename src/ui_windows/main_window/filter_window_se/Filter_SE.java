package ui_windows.main_window.filter_window_se;

import core.CoreModule;
import core.Module;
import core.SharedData;
import javafx.application.Platform;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.Product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.regex.Pattern;

import static core.SharedData.*;
import static ui_windows.main_window.filter_window_se.FilterParameters_SE.*;
import static ui_windows.main_window.filter_window_se.ItemsSelection.ALL_ITEMS;
import static ui_windows.main_window.filter_window_se.ItemsSelection.PRICE_ITEMS;
import static ui_windows.product.data.DataItem.DATA_EMPTY;

public class Filter_SE implements Module {
//    private ExecutorService filterExecutors;

    public Filter_SE() {
        SHD_DATA_SET.subscribe(this);
        SHD_FILTER_PARAMETERS.subscribe(this);
//        filterExecutors = Executors.newFixedThreadPool(2);
    }

    public void apply() {
        Collection<Product> dataSet = SHD_DATA_SET.getData();
        FilterParameters_SE parameters = SHD_FILTER_PARAMETERS.getData();

        if (dataSet == null || parameters == null) {
            return;
        }

        parameters.getFamilies().clear();
        parameters.getFamilies().add(ALL_FAMILIES);

        parameters.getLgbks().clear();
        parameters.getLgbks().add(ALL_LGBKS);

        parameters.getHierarchies().clear();
        parameters.getHierarchies().add(ALL_LGBKS);

        List<Product> result = new ArrayList<>();

        Pattern pattern = Pattern.compile(
                String.format(".*%s.*", parameters.getSearchText().replaceAll("\\*", ".*")),
                Pattern.CASE_INSENSITIVE);

//        long t1 = System.currentTimeMillis();

        boolean searchTextMatches;
        boolean priceMatches;
        boolean familyMatches;
        boolean lgbkMatches;
        boolean hierarchyMatch;
        boolean customMatches;


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

                parameters.getFamilies().add(product.getProductFamilyDefValue(FAMILY_NOT_ASSIGNED));

                ProductLgbk lgbk = CoreModule.getProductLgbks().getGroupLgbkByName(product.getLgbk());
                parameters.getLgbks().add(lgbk == null || lgbk.getLgbk().isEmpty() ? LGBK_NO_DATA : lgbk);

                ProductLgbk hier = CoreModule.getProductLgbks().getLgbkByProduct(product);
                parameters.getHierarchies().add(hier == null || hier.getHierarchy().isEmpty() ? LGBK_NO_DATA : hier);
            }
        }

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
