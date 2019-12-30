package database.se;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DataExchanger<T> {
    private Class<T> clazz;
    private ArrayList<Field> fields;

    public DataExchanger(Class<T> clazz, ArrayList<Field> fields) {
        this.clazz = clazz;
        this.fields = fields;
    }

    public T fromDbToObject(ResultSet resultSet) throws Exception {
        T object = clazz.newInstance();

        for (Field field : fields) {
            String fieldType = field.getType().getSimpleName();

            try {
                if (fieldType.equals("int")) {
                    field.set(object, resultSet.getInt(field.getName()));
                } else if (fieldType.equals("double")) {
                    field.set(object, resultSet.getDouble(field.getName()));
                } else if (fieldType.equals("String")) {
                    field.set(object, resultSet.getString(field.getName()));
                } else if (fieldType.equals("boolean")) {
                    field.set(object, resultSet.getBoolean(field.getName()));
                } else {
                    Class<?> compoundClass = Class.forName(field.getType().getName());
                    for (Class<?> interface1 : compoundClass.getInterfaces()) {
                        if (interface1.getSimpleName().equals("DbCompound")) {
                            Constructor<?> constructor = null;
                            try {
                                constructor = compoundClass.getDeclaredConstructor(String.class);
                            } catch (Exception e) {
//                                System.out.println(e.getMessage());
                            }

                            if (constructor != null) {
                                String rawValue = resultSet.getString(field.getName());
                                if (rawValue != null) {
                                    field.set(object, constructor.newInstance(rawValue));
                                }
                            } else {
                                System.out.println("Constructor " + compoundClass.getSimpleName() + "(String) not found");
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return object;
    }

    public int fromObjectToDb(T item, PreparedStatement prepStat) throws Exception {
        int parameter = 1;
        for (Field field : fields) {
            if (field.getName().equals("id")) continue;

            String fieldType = field.getType().getSimpleName();

            if (fieldType.equals("int")) {
                prepStat.setInt(parameter, (int) field.get(item));
            } else if (fieldType.equals("double")) {
                prepStat.setDouble(parameter, (double) field.get(item));
            } else if (fieldType.equals("String")) {
                prepStat.setString(parameter, (String) field.get(item));
            } else if (fieldType.equals("boolean")) {
                prepStat.setBoolean(parameter, (boolean) field.get(item));
            } else if (field.get(item) instanceof DbCompound) {
                prepStat.setString(parameter, ((DbCompound) field.get(item)).getStringForDb());
            }

            parameter++;
        }
        return parameter;
    }
}
