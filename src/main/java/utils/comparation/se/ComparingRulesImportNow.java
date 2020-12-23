package utils.comparation.se;

import javafx.util.Callback;
import ui_windows.product.Product;
import ui_windows.product.data.DataItem;
import utils.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static ui_windows.product.data.DataItem.*;

public class ComparingRulesImportNow extends ProductComparingRulesTemplate implements ComparingRules<Product> {

    public ComparingRulesImportNow() {
    }

    public ComparingRulesImportNow(Callback<Param<Product>, Param<Product>> customRule) {
        super(customRule);
    }

    @Override
    public boolean isCanBeSkipped(Param<Product> params) {
        if (params.getField() == DATA_ORDER_NUMBER.getField()) return true;

        if (DataItem.getDataItemByField(params.getField()) == DATA_ARTICLE) {
            String value1s = params.getObject1().getArticle().replaceAll("\\s", "");
            String value2s = params.getObject2().getArticle().replaceAll("\\s", "");
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
    public void addHistoryComment(ObjectsComparatorResultSe<Product> result) {
        String comment = Utils.getDateTime().concat(", ");
        StringBuilder consoleComment = new StringBuilder(comment);

        if (result.getItem() == null) {//new
            result.getItem_after().addHistory(comment.concat("new (file)"));
            consoleComment.append("new (file)");
            result.getItem_after().addLastImportCodes("new");
        } else if (result.getItem_after() == null) {//gone

        } else {//changed
            String impCodes = "";
            for (Field field : result.getChangedFields()) {
                try {
                    field.setAccessible(true);
                    String infoPart = String.format(" %s: %s -> %s, ", field.getName(),
                            field.get(result.getItem_before()), field.get(result.getItem_after()));
                    comment = comment.concat(infoPart);
                    consoleComment.append(result.getItem().getArticle()).append(" (").
                            append(result.getItem().getMaterial()).append(")").append(infoPart);
                    impCodes = impCodes.concat(field.getName()).concat(",");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            result.getItem().addHistory(comment.concat("file"));
            result.getItem().addLastImportCodes(impCodes);

            System.out.println(consoleComment.append("file").toString());
        }
    }

    @Override
    public boolean addNewItem(Product item, ArrayList<Field> fields) {
        if (!fields.contains(DATA_ORDER_NUMBER.getField()) || !fields.contains(DATA_ARTICLE.getField())) {
            System.out.println(String.format("item %s wasn't added, it haven't main fields", item.toString()));
            return false;
        }

        String material = item.getMaterial();
        String article = item.getArticle();
        if (material == null || material.isEmpty() || article == null || article.isEmpty()) {
            System.out.println(String.format("item %s wasn't added, it haven't main values", item.toString()));
            return false;
        }

        return true;
    }
}
