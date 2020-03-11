package ui_windows.main_window.filter_window_se;

import core.CoreModule;
import core.Module;
import core.SharedData;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.product.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static core.SharedData.*;
import static ui_windows.main_window.filter_window_se.FilterParameters_SE.ALL_FAMILIES;
import static ui_windows.main_window.filter_window_se.FilterParameters_SE.ALL_LGBKS;
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

        System.out.println("prod fam in start = " + parameters.getFilterProductFamily());

        ProductFamily prevFamily = parameters.getFilterProductFamily() == null ? ALL_FAMILIES : parameters.getFilterProductFamily();
        parameters.getFamilies().clear();
        parameters.getFamilies().add(ALL_FAMILIES);

        parameters.getLgbks().clear();
        parameters.getLgbks().add(ALL_LGBKS);

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
            familyMatches = parameters.getFilterProductFamily() == null || parameters.getFilterProductFamily() == ALL_FAMILIES ||
                    parameters.getFilterProductFamily() == product.getProductFamily();
            lgbkMatches = parameters.getFilterProductLgbk() == null || parameters.getFilterProductLgbk() == ALL_LGBKS ||
                    parameters.getFilterProductLgbk().getLgbk().equals(product.getLgbk());
            hierarchyMatch = parameters.getFilterProductHierarchy() == null ||
                    parameters.getFilterProductLgbk().getHierarchy().equals(product.getHierarchy());
            customMatches = parameters.getFilterCustomProperty() == null || parameters.getFilterCustomCondition().matches(
                    parameters.getFilterCustomProperty().getValue(product).toString(), parameters.getFilterCustomValue());

            if (searchTextMatches && priceMatches && familyMatches && lgbkMatches && hierarchyMatch && customMatches) {
                result.add(product);

                parameters.getFamilies().add(product.getProductFamilyDefValue(ALL_FAMILIES));
                parameters.getLgbks().add(CoreModule.getProductLgbks().getByLgbkNameDefValue(product.getLgbk(), ALL_LGBKS));
            }
        }

        System.out.println(String.format("curr family = %s, prev = %s", parameters.getFilterProductFamily(), prevFamily));
        System.out.print("families list = ");
        for (ProductFamily fam:parameters.getFamilies()             ) {
            System.out.print(fam.getName() + ", ");
        }
        System.out.println();

        if (parameters.getFamilies().contains(prevFamily)) {
            parameters.setProductFamily(prevFamily);
        }


        System.out.println(System.currentTimeMillis() - t1);
        SHD_DISPLAYED_DATA.setData(result);
        SHD_FILTER_PARAMETERS.setData(parameters, this);
    }

    @Override
    public void refreshSubscribedData(SharedData sharedData, Object data) {
        apply();
    }
}
