package utils.comparation.se;

import javafx.util.Callback;
import ui_windows.product.Product;

import java.lang.reflect.Field;
import java.util.List;

public class ComparingRulesPricesComparison extends ProductComparingRulesTemplate implements ComparingRules<Product> {

    public ComparingRulesPricesComparison() {
    }

    public ComparingRulesPricesComparison(Callback<Param<Product>, Param<Product>> customComparisonRule) {
        super(customComparisonRule);
    }

    @Override
    public boolean addNewItem(Product item, List<Field> fields) {
        return true;
    }
}
