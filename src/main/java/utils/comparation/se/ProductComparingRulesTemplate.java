package utils.comparation.se;

import javafx.util.Callback;
import ui_windows.product.Product;

import static ui_windows.product.data.DataItem.*;

public abstract class ProductComparingRulesTemplate implements ComparingRules<Product> {
    protected Callback<Param<Product>, Param<Product>> customComparisonRule;

    public ProductComparingRulesTemplate() {
        customComparisonRule = null;
    }

    public ProductComparingRulesTemplate(Callback<Param<Product>, Param<Product>> customComparisonRule) {
        this.customComparisonRule = customComparisonRule;
    }

    @Override
    public boolean isTheSameItem(Param<Product> params) {
        String prod1 = params.getObject1().getMaterial().replaceAll("(\\-)*(\\:)*(VBPZ)*(BPZ)*", "");
        String prod2 = params.getObject2().getMaterial().replaceAll("(\\-)*(\\:)*(VBPZ)*(BPZ)*", "");

        if (prod2.matches("^0+\\d+$")) {
            prod2 = prod2.replaceAll("^0+", "");
        }

        return prod1.equals(prod2);
    }

    @Override
    public boolean isCanBeSkipped(Param<Product> params) {
        if (params.getField() == DATA_ORDER_NUMBER.getField()) {
            return true;
        }

        if (params.getField() == DATA_DESCRIPTION_RU.getField()) {
            String desc1 = (String) DATA_DESCRIPTION.getValue((Product) params.getObject1());
            String desc2 = (String) DATA_DESCRIPTION.getValue((Product) params.getObject2());
            if (desc1.trim().equals(desc2.trim())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void addHistoryComment(ObjectsComparatorResultSe<Product> result) {

    }

    @Override
    public Param<Product> applyCustomRule(Param<Product> params) {
        return customComparisonRule != null ? customComparisonRule.call(params) : params;
    }
}
