package utils.comparation.se;

import ui_windows.product.Product;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static ui_windows.product.data.DataItem.*;

public class ComparingRulesPricesComparison implements ComparingRules {
    @Override
    public boolean isTheSameItem(Param<?> params) {
        String prod1 = ((Product) params.getObject1()).getMaterial().replaceAll("(\\-)*(\\:)*(VBPZ)*(BPZ)*", "");
        String prod2 = ((Product) params.getObject2()).getMaterial().replaceAll("(\\-)*(\\:)*(VBPZ)*(BPZ)*", "");

        if (prod2.matches("^0+\\d+$")) {
            prod2 = prod2.replaceAll("^0+", "");
        }

        return prod1.equals(prod2);
    }

    @Override
    public boolean isCanBeSkipped(Param<?> params) {
        if (params.getField() == DATA_ORDER_NUMBER.getField()) {
            return true;
        }

        if (params.getField() == DATA_DESCRIPTION_RU.getField()) {
            String desc1 = (String) DATA_DESCRIPTION.getValue((Product)params.getObject1());
            String desc2 = (String) DATA_DESCRIPTION.getValue((Product)params.getObject2());
            return desc1.trim().equals(desc2.trim());
        }

        return false;
    }

    @Override
    public void addHistoryComment(ObjectsComparatorResultSe<?> result) {

    }

    @Override
    public boolean addNewItem(Object object, ArrayList<Field> fields) {
        return true;
    }
}
