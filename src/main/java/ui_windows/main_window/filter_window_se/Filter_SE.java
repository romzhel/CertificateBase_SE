package ui_windows.main_window.filter_window_se;

import core.InitModule;
import core.Initializable;
import core.Module;
import core.SharedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.product.Product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;

import static core.SharedData.*;

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

        logger.debug("start filtering, try to find '{}' ...", FilterParameters_SE.searchBox.getText());
        long t1 = System.currentTimeMillis();

        List<Product> result = new ArrayList<>();

        parameters.calcFilterPredicate();
        if (parameters.getFilterPredicate() == null) {
            result.addAll(dataSet);
        } else {
            for (Product product : dataSet) {
                if (parameters.getFilterPredicate().check(product)) {
                    result.add(product);
                }
            }
        }

        logger.debug("finding process finished, results: {}, time elapsed: {} ms", result.size(), System.currentTimeMillis() - t1);

//        System.out.println(Utils.getExactTime() + " result arraylist filled");
//        print("families: ", parameters.getFamilies(), (f) -> f.getName());
//        print("lgbks: ", parameters.getLgbks(), (l) -> l.getLgbk());
//        print("hierarchies: ", parameters.getHierarchies(), (h) -> h.getHierarchy());
//        System.out.println(String.format("%s; prop: %s, value: %s, matcher: %s", this.getClass().getSimpleName(), parameters.getCustomProperty().name(),
//                parameters.getCustomValue(), parameters.getCustomValueMatcher().name()));

        SHD_DISPLAYED_DATA.setData(this.getClass(), result);
//        SHD_FILTER_PARAMETERS.setData(this.getClass(), parameters, this);

        FilterParameters_SE.searchBox.getTextBox().requestFocus();
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
