package database.se;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class RequestFactory {
    private Class<?> clazz;
    private ArrayList<Field> fields;
    private String tableName;

    public RequestFactory(Class<?> clazz, ArrayList<Field> fields) {
        this.clazz = clazz;
        this.fields = fields;
        String suffix = clazz.getSimpleName().endsWith("s") ? "es" : "s";
        tableName = clazz.getSimpleName().toLowerCase() + suffix;
    }

    public String createRequestGetAll() {
        return "SELECT * FROM " + tableName;
    }

    public String createRequestAdd() {
        String request1 = "INSERT INTO " + tableName + " (";
        String request2 = " VALUES (";

        //INSERT INTO table (prop1, prop2) VALUES (?, ?)

        for (Field field : fields) {
            if (field.getName().equals("id")) continue;

            request1 = request1.concat(field.getName()).concat(", ");
            request2 = request2.concat("?, ");
        }

        request1 = request1.replaceAll("(\\,\\s)$", "").concat(")");
        request2 = request2.replaceAll("(\\,\\s)$", "").concat(")");

        return request1.concat(request2);
    }

    public <T extends DBSynced> String createRequestUpdate(ArrayList<T> items) {
        T object = items.get(0);
        Class<?> clazz = object.getClass();
        String request1 = "";
        String request2 = "";
        String request3 = "";

//        UPDATE products SET article = ?, hierarchy = ? WHERE id = a

        for (T item : items) {
            request1 = "UPDATE " + tableName;
            request2 = " SET ";
            request3 = " WHERE id = ?";

            for (Field field : fields) {
                if (field.getName().equals("id")) continue;

                request2 = request2.concat(field.getName().concat(" = ?, "));
            }

            request2 = request2.replaceAll("(\\,\\s)$", "");
        }

        return request1.concat(request2).concat(request3);
    }
}
