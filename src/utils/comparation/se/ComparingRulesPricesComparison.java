package utils.comparation.se;

import javafx.util.Callback;
import ui_windows.product.Product;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ComparingRulesPricesComparison extends ProductComparingRulesTemplate implements ComparingRules<Product> {

    public ComparingRulesPricesComparison() {
    }

    public ComparingRulesPricesComparison(Callback<Param<Product>, Param<Product>> customComparisonRule) {
        super(customComparisonRule);
    }

    @Override
    public boolean addNewItem(Product item, ArrayList<Field> fields) {
        return true;
    }
}
