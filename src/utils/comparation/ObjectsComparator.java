package utils.comparation;

import javafx.beans.property.StringProperty;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.product.Product;
import utils.Utils;

import java.lang.reflect.Field;

public class ObjectsComparator {
    private ObjectsComparatorResult comparatorResult;

    public ObjectsComparator() {
    }

    public ObjectsComparator(Object obj1, Object obj2, boolean emptyTextProtection, String... exceptions) {
        comparatorResult = new ObjectsComparatorResult();
        Field[] obj1Fields = obj1.getClass().getDeclaredFields();//fields of product1
        Field[] obj2Fields = obj2.getClass().getDeclaredFields();//fields of product2
        Object value1;
        Object value2;

        try {
//                System.out.print("item " + getProperty(pr1, pr1Fields[1]) + ": ");

            boolean isException;
            for (int i = 0; i < obj1Fields.length; i++) {//check all the fields
//                System.out.println(pr1Fields[i].getName().toLowerCase() + ", " + pr1Fields[i].getType().getName());
                isException = false;//treat excepted fields
                for (String s : exceptions) {
                    if (obj1Fields[i].getName().toLowerCase().matches(s.toLowerCase())) {
                        isException = true;
                        break;
                    }
                }
                if (isException) continue;

                value1 = getProperty(obj1, obj1Fields[i]);//existing value
                value2 = getProperty(obj2, obj2Fields[i]);//new value

                if (obj1Fields[i].getName().matches(".*article.*")){
                    String value1s = ((String)value1).replaceAll("\\s", "");
                    String value2s = ((String)value2).replaceAll("\\s", "");
                    if (value1s.equals(value2s)) continue;
                }

                if (obj1Fields[i].getName().matches(".*price.*")){
                    continue;
                }

                boolean notZero = true;//avoiding reset of Product type value to 0
                if (value2 != null && value2.getClass().getName().endsWith("Integer") && !obj1Fields[i].getName().matches(".*type_id.*")) {
                    notZero = ((Integer) value2) != 0;
                }

                boolean emptyTextChecker = false;
                if (value2 != null) {
                    if (emptyTextProtection && value2.toString().trim().length() > 0) emptyTextChecker = true;
                    else if (!emptyTextProtection) emptyTextChecker = true;
                }

                if (emptyTextChecker && notZero) {//there is a new value
                    if (value1 == null || !value1.equals(value2)) {//field value was changed or was empty

                        obj1Fields[i].setAccessible(true);
                        obj2Fields[i].setAccessible(true);
                        obj1Fields[i].set(obj1, obj2Fields[i].get(obj2));//copy new value

                        comparatorResult.setNeedUpdateInDB(true);

                        if (value1 != null && isValueNoDefault(value1)) {
                            String historyResult = comparatorResult.getHistoryComment();
                            if (obj1Fields[i].getName().toLowerCase().contains("description")) {//description changes
                                historyResult += ", " + obj1Fields[i].getName() + "-> " + value2.toString();
                            } else {
                                historyResult += ", " + obj1Fields[i].getName() + ": " + (value1 == null ? "" : value1.toString()) +
                                        " -> " + value2.toString();
                            }
                            comparatorResult.setHistoryComment(historyResult);
                        }

                        if (obj1 instanceof Product) {
//                            ((Product) obj1).setChangecodes(((Product) obj1).getChangecodes().concat(obj1Fields[i].getName()));
                            ((Product) obj1).setChangecodes(Utils.addTextWithCommas(((Product) obj1).getChangecodes(), obj1Fields[i].getName()));

//                            ((Product) obj1).setLastImportcodes(((Product) obj1).getLastImportcodes().concat(obj1Fields[i].getName()));
                            ((Product) obj1).setLastImportcodes(Utils.addTextWithCommas(((Product) obj1).getLastImportcodes(), obj1Fields[i].getName()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("exception object comparator " + e.getMessage());
        }
    }

    public ObjectsComparatorResult getResult() {
        return comparatorResult;
    }

    public Object getProperty(Object object, Field field) {
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

            } else if (field.getType().getName().toLowerCase().contains("normslist")) {//for norm editor only

                methodName = "get" + methodName;
                NormsList tempValue = (NormsList) object.getClass().getMethod(methodName).invoke(object);
                value = tempValue.toString();

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
