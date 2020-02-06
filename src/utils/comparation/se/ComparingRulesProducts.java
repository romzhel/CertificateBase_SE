package utils.comparation.se;

import ui_windows.product.Product;
import ui_windows.product.data.DataItem;
import utils.Utils;

import java.lang.reflect.Field;

import static ui_windows.product.data.DataItem.*;

public class ComparingRulesProducts implements ComparingRules {
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
        if (params.getField() == DATA_ORDER_NUMBER.getField()) return true;

        if (DataItem.getDataItemByField(params.getField()) == DATA_ARTICLE) {
            String value1s = ((Product) params.getObject1()).getArticle().replaceAll("\\s", "");
            String value2s = ((Product) params.getObject2()).getArticle().replaceAll("\\s", "");
            if (value1s.equals(value2s)) return true;
        }

        String value2 = params.getObject2().toString().trim();
        boolean isNotDataTypeProperty = DataItem.getDataItemByField(params.getField()) != DATA_TYPE;
        boolean isNotNull = params.getObject2() != null;
        boolean stringValueEmpty = value2.isEmpty();
        boolean intValueZero = value2.equals("0");
        if (isNotDataTypeProperty && isNotNull && (stringValueEmpty || intValueZero)) {
            try {
                System.out.println("skipped " + params.getField().getName() + ": " + params.getField().get(params.getObject1())
                        + " -> " + params.getField().get(params.getObject2()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }

    @Override
    public void addHistoryComment(ObjectsComparatorResultSe<?> result) {
        String comment = Utils.getDateTime().concat(", ");
        if (result.getItem() == null) {//new
            ((Product) result.getItem_after()).addHistory(comment.concat("new (file"));
        } else if (result.getItem_after() == null) {//gone

        } else {//changed
            for (Field field : result.getChangedFields()) {
                try {
                    comment = comment
                            .concat(field.getName()).concat(": ")
                            .concat(field.get(result.getItem_before()).toString()).concat(" -> ")
                            .concat(field.get(result.getItem_after()).toString()).concat(", ");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            ((Product) result.getItem()).addHistory(comment.concat(" file"));

            System.out.println(comment);
        }
    }
}
