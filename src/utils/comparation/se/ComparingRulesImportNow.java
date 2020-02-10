package utils.comparation.se;

import core.CoreModule;
import ui_windows.product.Product;
import ui_windows.product.data.DataItem;
import utils.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static ui_windows.product.data.DataItem.*;

public class ComparingRulesImportNow implements ComparingRules {
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

        try {
            params.getField().setAccessible(true);
            String value2 = params.getField().get(params.getObject2()).toString().trim();

            boolean isNotDataTypeProperty = DataItem.getDataItemByField(params.getField()) != DATA_TYPE;
            boolean isNotNull = params.getObject2() != null;
            boolean stringValueEmpty = value2.isEmpty();
            boolean intValueZero = value2.equals("0");

            if (isNotDataTypeProperty && isNotNull && (stringValueEmpty || intValueZero)) {
                /*System.out.println(String.format("skipped %s: %s -> %s", params.getField().getName(),
                        params.getField().get(params.getObject1()), params.getField().get(params.getObject2())));*/

                return true;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void addHistoryComment(ObjectsComparatorResultSe<?> result) {
        String comment = Utils.getDateTime().concat(", ");
        if (result.getItem() == null) {//new
            ((Product) result.getItem_after()).addHistory(comment.concat("new (file)"));
            ((Product) result.getItem_after()).addLastImportCodes("new");
        } else if (result.getItem_after() == null) {//gone

        } else {//changed
            String impCodes = "";
            for (Field field : result.getChangedFields()) {
                try {
                    comment = comment.concat(String.format("%s: %s -> %s, ", field.getName(),
                            field.get(result.getItem_before()), field.get(result.getItem_after())));
                    impCodes = impCodes.concat(field.getName()).concat(",");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            ((Product) result.getItem()).addHistory(comment.concat("file"));
            ((Product) result.getItem()).addLastImportCodes(impCodes);

            System.out.println(comment.concat("file"));
        }
    }

    @Override
    public boolean addNewItem(Object item, ArrayList<Field> fields) {
        if (!fields.contains(DATA_ORDER_NUMBER.getField()) || !fields.contains(DATA_ARTICLE.getField())) {
            System.out.println(String.format("item %s wasn't added, it haven't main fields", item.toString()));
            return false;
        }

        String material = ((Product) item).getMaterial();
        String article = ((Product) item).getArticle();
        if (material == null || material.isEmpty() || article == null || article.isEmpty()) {
            System.out.println(String.format("item %s wasn't added, it haven't main values", item.toString()));
            return false;
        }

//        CoreModule.getProducts().getItems().add((Product) item);
        return true;
    }
}
