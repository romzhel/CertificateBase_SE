package ui_windows.main_window.filter_window_se;

import core.CoreModule;
import core.Module;
import core.SharedData;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static core.SharedData.*;
import static ui_windows.main_window.filter_window_se.FilterParameters_SE.*;
import static ui_windows.main_window.filter_window_se.ItemsSelection.ALL_ITEMS;
import static ui_windows.main_window.filter_window_se.ItemsSelection.PRICE_ITEMS;

public class Filter_SE implements Module {
//    private ExecutorService filterExecutors;

    public Filter_SE() {
        SHD_DATA_SET.subscribe(this);
        SHD_FILTER_PARAMETERS.subscribe(this);
//        filterExecutors = Executors.newFixedThreadPool(2);
    }

    public void apply() {
        List<Product> dataSet = SHD_DATA_SET.getData();
        FilterParameters_SE parameters = SHD_FILTER_PARAMETERS.getData();

        if (dataSet == null || parameters == null) {
            return;
        }

        System.out.println(getClass().getName() + ", data set = " + dataSet.size());
        System.out.println("prod fam in start = " + parameters.getFamily());

//        ProductFamily prevFamily = parameters.getFilterProductFamily() == null ? ALL_FAMILIES : parameters.getFilterProductFamily();
        parameters.getFamilies().clear();
        parameters.getFamilies().add(ALL_FAMILIES);

        parameters.getLgbks().clear();
        parameters.getLgbks().add(ALL_LGBKS);

        parameters.getHierarchies().clear();
        parameters.getHierarchies().add(ALL_LGBKS);

        if (dataSet == null || parameters == null) return;

        List<Product> result = new ArrayList<>();

        Pattern pattern = Pattern.compile(
                String.format(".*%s.*", parameters.getSearchText().replaceAll("\\*", ".*")),
                Pattern.CASE_INSENSITIVE);

        long t1 = System.currentTimeMillis();

        boolean searchTextMatches;
        boolean priceMatches;
        boolean familyMatches;
        boolean lgbkMatches;
        boolean hierarchyMatch;
        boolean customMatches;


        for (Product product : dataSet) {
            searchTextMatches = pattern.matcher(product.getArticle()).matches();
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
                    product.getHierarchy().contains(parameters.getHierarchy().getHierarchy().replaceAll("\\.",""));
            customMatches = parameters.getCustomCondition() == null || parameters.getCustomValueMatcher().matches(
                    parameters.getCustomCondition().getValue(product).toString(), parameters.getCustomValue());

            if (searchTextMatches && priceMatches && familyMatches && lgbkMatches && hierarchyMatch && customMatches) {
                result.add(product);

                parameters.getFamilies().add(product.getProductFamilyDefValue(FAMILY_NOT_ASSIGNED));

                ProductLgbk lgbk = CoreModule.getProductLgbks().getGroupLgbkByName(product.getLgbk());
                parameters.getLgbks().add(lgbk == null || lgbk.getLgbk().isEmpty() ? LGBK_NO_DATA : lgbk);

//                parameters.getLgbks().add(product.getLgbk() == null || product.getLgbk().isEmpty() ? TEXT_NO_DATA : product.getLgbk());

                ProductLgbk hier = CoreModule.getProductLgbks().getLgbkByProduct(product);
                parameters.getHierarchies().add(hier == null || hier.getHierarchy().isEmpty() ? LGBK_NO_DATA : hier);

                /*parameters.getHierarchies().add(product.getHierarchy() == null || product.getHierarchy().isEmpty() ?
                        TEXT_NO_DATA : product.getHierarchy());*/
            }
        }

//        System.out.println(String.format("curr family = %s, prev = %s", parameters.getFilterProductFamily(), prevFamily));
        printFamilies(parameters);
        printLgbk(parameters);
        printHierarchies(parameters);

        System.out.println(System.currentTimeMillis() - t1);
        SHD_DISPLAYED_DATA.setData(result);
        SHD_FILTER_PARAMETERS.setData(parameters, this);
    }

    private void printFamilies(FilterParameters_SE parameters) {
        System.out.print("families list = ");
        for (ProductFamily fam : parameters.getFamilies()) {
            System.out.print(fam.getName() + ", ");
        }
        System.out.println();
    }

    private void printLgbk(FilterParameters_SE parameters) {
        System.out.print("lgbk list = ");
        for (ProductLgbk lgbk : parameters.getLgbks()) {
            System.out.print(lgbk + ", ");
        }
        System.out.println();
    }

    private void printHierarchies(FilterParameters_SE parameters) {
        System.out.print("hierarchy list = ");
        for (ProductLgbk hierarchy : parameters.getHierarchies()) {
            System.out.print(hierarchy + ", ");
        }
        System.out.println();
    }

    @Override
    public void refreshSubscribedData(SharedData sharedData, Object data) {
        apply();
    }

}
