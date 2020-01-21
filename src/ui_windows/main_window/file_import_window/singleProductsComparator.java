package ui_windows.main_window.file_import_window;

import ui_windows.product.Product;
import utils.Utils;
import utils.comparation.products.ObjectsComparatorResult;

import java.lang.reflect.Field;

import static ui_windows.product.data.DataItem.*;

public class SingleProductsComparator {
    private ObjectsComparatorResult comparatorResult;

    public SingleProductsComparator(Product obj1, Product obj2, boolean emptyTextProtection, FileImportParameter... parameters) {
        comparatorResult = new ObjectsComparatorResult();
        Object value1;
        Object value2;
        try {
            for (FileImportParameter parameter : parameters) {
                if (!parameter.isImportValue()) continue;

                value1 = parameter.getDataItem().getValue(obj1);
                value2 = parameter.getDataItem().getValue(obj2);

                if (parameter.getDataItem() == DATA_ARTICLE) {
                    String value1s = ((String) value1).replaceAll("\\s", "");
                    String value2s = ((String) value2).replaceAll("\\s", "");
                    if (value1s.equals(value2s)) continue;
                }

                boolean notZero = true;//avoiding reset of Product type value to 0
                if (value2 != null && value2.getClass().getName().endsWith("Integer") && parameter.getDataItem() != DATA_TYPE) {
                    notZero = ((Integer) value2) != 0;
                }

                boolean emptyTextChecker = false;
                if (value2 != null) {
                    if (emptyTextProtection && !value2.toString().trim().isEmpty()) emptyTextChecker = true;
                    else if (!emptyTextProtection) emptyTextChecker = true;
                }

                if (emptyTextChecker && notZero) {//there is a new value
                    if (value1 == null || !value1.equals(value2)) {//field value was changed or was empty

                        Field field = parameter.getDataItem().getField();

                        field.setAccessible(true);
                        field.set(obj1, field.get(obj2));//copy new value

                        comparatorResult.setNeedUpdateInDB(true);

                        if (parameter.isLogHistory()) {
                            String historyResult = comparatorResult.getHistoryComment();
                            if (parameter.getDataItem().name().toLowerCase().contains("description")) {//description changes
                                historyResult += ", " + field.getName();
                            } else {
                                historyResult += ", " + field.getName() + ": " + value1.toString() + " -> " + value2.toString();
                            }
                            comparatorResult.setHistoryComment(historyResult);
                        }
                        String logComment = comparatorResult.getLogComment();
                        comparatorResult.setLogComment(logComment + ", " + parameter.getDataItem().getDisplayingName() + ": " +
                                (value1 == null ? "" : value1.toString()) + " -> " + value2.toString());

                        if (parameter.isLogLastImportCodes()) {
                            obj1.setLastImportcodes(Utils.addTextWithCommas(obj1.getLastImportcodes(), field.getName()));
                        }

                        comparatorResult.addToReport(obj1, "changed", parameter.getDataItem().getDisplayingName(),
                                value1.toString(), " -> ", value2.toString());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("exception" + e.getMessage());
        }
    }

    public ObjectsComparatorResult getResult() {
        return comparatorResult;
    }
}
