package utils.comparation.se;

import ui_windows.product.Product;

import java.lang.reflect.Field;
import java.util.List;

public class ComparingRulesProductEditor extends ProductComparingRulesTemplate implements ComparingRules<Product> {

    @Override
    public boolean isTheSameItem(Param<Product> params) {
        return false;
    }

    @Override
    public boolean isCanBeSkipped(Param<Product> params) {
        return false;
    }

    @Override
    public void addHistoryComment(ObjectsComparatorResultSe<Product> result) {
        super.addHistoryComment(result);
    }

    @Override
    public boolean addNewItem(Product item, List<Field> fields) {
        return false;
    }
}
