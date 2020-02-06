package utils.comparation.se;

import ui_windows.options_window.product_lgbk.NormsList;

import java.lang.reflect.Field;

public class PropertyValueGetter {

    public Object getPropertyValue(Object object, Field field) {
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
}
