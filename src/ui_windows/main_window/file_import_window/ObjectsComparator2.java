package ui_windows.main_window.file_import_window;

import javafx.beans.property.StringProperty;
import ui_windows.product.Product;
import utils.Utils;
import utils.comparation.ObjectsComparatorResult;

import java.lang.reflect.Field;

import static ui_windows.product.data.ProductProperties.FIELD_ORDER_NUMBER;

public class ObjectsComparator2 {
    private ObjectsComparatorResult comparatorResult;

    public ObjectsComparator2(Object obj1, Object obj2, boolean emptyTextProtection, ColumnsMapper.FieldForImport... fieldsForImport) {
        comparatorResult = new ObjectsComparatorResult();
        Object value1;
        Object value2;
        try {
            for (ColumnsMapper.FieldForImport field : fieldsForImport) {

                if (field.getField().getName().toLowerCase().equals(FIELD_ORDER_NUMBER.toLowerCase())) continue;

                if (field.getFileImportTableItem().isImportValue()) {
                    Field existingField = obj1.getClass().getDeclaredField(field.getField().getName());
                    Field upgradedField = obj2.getClass().getDeclaredField(field.getField().getName());

                    value1 = getProperty(obj1, existingField);
                    value2 = getProperty(obj2, upgradedField);



                    if (field.getField().getName().toLowerCase().matches(".*article.*")) {
                        String value1s = ((String) value1).replaceAll("\\s", "");
                        String value2s = ((String) value2).replaceAll("\\s", "");
                        if (value1s.equals(value2s)) continue;
                    }

                    boolean notZero = true;//avoiding reset of Product type value to 0
                    if (value2 != null && value2.getClass().getName().endsWith("Integer") && !existingField.getName().matches(".*type_id.*")) {
                        notZero = ((Integer) value2) != 0;
                    }

                    boolean emptyTextChecker = false;
                    if (value2 != null) {
                        if (emptyTextProtection && !value2.toString().trim().isEmpty()) emptyTextChecker = true;
                        else if (!emptyTextProtection) emptyTextChecker = true;
                    }

                    if (emptyTextChecker && notZero) {//there is a new value
                        if (value1 == null || !value1.equals(value2)) {//field value was changed or was empty

                            existingField.setAccessible(true);
                            upgradedField.setAccessible(true);
                            existingField.set(obj1, upgradedField.get(obj2));//copy new value

                            comparatorResult.setNeedUpdateInDB(true);

                            if (/*value1 != null && isValueNoDefault(value1) &&*/ field.getFileImportTableItem().isLogHistory()) {
                                String historyResult = comparatorResult.getHistoryComment();
                                if (existingField.getName().toLowerCase().contains("description")) {//description changes
                                    historyResult += ", " + existingField.getName();
                                } else {
                                    historyResult += ", " + existingField.getName() + ": " + value1.toString() + " -> " + value2.toString();
                                }
                                comparatorResult.setHistoryComment(historyResult);
                            }
                            String logComment = comparatorResult.getLogComment();
                            comparatorResult.setLogComment(logComment + ", " + existingField.getName() + ": " +
                                    (value1 == null ? "" : value1.toString()) + " -> " + value2.toString());

                            if (obj1 instanceof Product && field.getFileImportTableItem().isLogLastImportCodes()) {
//                            ((Product) obj1).setChangecodes(((Product) obj1).getChangecodes().concat(obj1Fields[i].getName()));
//                                ((Product) obj1).setChangecodes(Utils.addTextWithCommas(((Product) obj1).getChangecodes(), existingField.getName()));


//                            ((Product) obj1).setLastImportcodes(((Product) obj1).getLastImportcodes().concat(obj1Fields[i].getName()));
                                ((Product) obj1).setLastImportCodes(Utils.addTextWithCommas(((Product) obj1).getLastImportCodes(), existingField.getName()));
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("exception"+ e.getMessage());
        }
    }

    public ObjectsComparatorResult getResult() {
        return comparatorResult;
    }

    private Object getProperty(Object object, Field field) {
        Object value = null;
        String methodName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);

        try {
            if (field.getType().getName().endsWith("StringProperty")) {//check only property

                methodName = "get" + methodName;
                value = object.getClass().getMethod(methodName).invoke(object);

            } else if (field.getType().getName().endsWith("BooleanProperty")) {

                methodName = "is" + methodName;
                value = (Boolean) object.getClass().getMethod(methodName).invoke(object);
//
            } else if (field.getType().getName().endsWith("String")) {

                methodName = "get" + methodName;
                value = (String) object.getClass().getMethod(methodName).invoke(object);

            } else if (field.getType().getName().endsWith("int")) {

                methodName = "get" + methodName;
                value = (Integer) object.getClass().getMethod(methodName).invoke(object);

            } else if (field.getType().getName().endsWith("boolean")) {

                methodName = "is" + methodName;
                value = (Boolean) object.getClass().getMethod(methodName).invoke(object);

            } else if (field.getType().getName().endsWith("double")) {

                methodName = "get" + methodName;
                value = (Double) object.getClass().getMethod(methodName).invoke(object);
            }

        } catch (Exception e) {
            System.out.println("reflection exception: " + e.getMessage());
        }
//        System.out.println("method " + methodName + " = " + value);

        return value;
    }

    private boolean isValueNoDefault(Object value) {
        try {
            if (value instanceof StringProperty) {
                return ((StringProperty) value).getValue() != null && !((StringProperty) value).getValue().isEmpty();
            } else if (value instanceof String) {
                return value != null && !((String) value).isEmpty();
            } else if (value instanceof Integer) {
                return value != null && ((Integer) value).intValue() != 0;
            } else if (value instanceof Double) {
                return value != null && ((Double) value).doubleValue() != 0.0;
            } else {
                return true;
            }
        } catch (Exception e) {
            System.out.println("value default checking exception: " + e.getMessage());
            return false;
        }
    }
}
