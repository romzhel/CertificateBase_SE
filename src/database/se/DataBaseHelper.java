package database.se;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class DataBaseHelper<T extends DBSynced> {
    private Class<T> clazz;
    private DataExchanger<T> dataExchanger;
    private DataBase dataBase;
    private RequestFactory requestFactory;
    private PreparedStatement prepStat;

    public DataBaseHelper(DataBase dataBase, Class<T> clazz) {
        this.clazz = clazz;
        ArrayList<Field> fields = getTreatedFields();
        dataExchanger = new DataExchanger<>(clazz, fields);
        this.dataBase = dataBase;
        requestFactory = new RequestFactory(clazz, fields);
    }

    public DataBaseHelper<T> connect() throws Exception {
        dataBase.connect();
        dataBase.getDbConnection().setAutoCommit(false);
        return this;
    }

    public void disconnect() throws Exception {
        prepStat.close();
        dataBase.getDbConnection().setAutoCommit(true);
        dataBase.disconnect();
    }

    public ArrayList<T> getFromDbAllItems() throws Exception {
        prepStat = dataBase.getDbConnection().prepareStatement(requestFactory.createRequestGetAll());
        ResultSet rs = prepStat.executeQuery();

        ArrayList<T> result = new ArrayList<>();
        while (rs.next()) {
            result.add(dataExchanger.fromDbToObject(rs));
        }

        rs.close();

        return result;
    }

    /*public ArrayList<T> refreshFromDbItems(ArrayList<T> items) throws Exception {
        dataBase.connect();
        prepStat = dataBase.getDbConnection().prepareStatement(requestFactory.createRequestGetAll(clazz));
        ResultSet rs = prepStat.executeQuery();

        ArrayList<T> result = dataExchanger.getObjectsFromDb(rs);
        rs.close();
        dataBase.disconnect();
        return result;
    }*/

    public DataBaseHelper<T> add(ArrayList<T> items) throws Exception {
        if (items.size() < 1) return this;

        prepStat = dataBase.getDbConnection().prepareStatement(requestFactory.createRequestAdd(),
                Statement.RETURN_GENERATED_KEYS);

        for (T item : items) {
            dataExchanger.fromObjectToDb(item, prepStat);
            prepStat.addBatch();
        }

        int[] result = prepStat.executeBatch();
        dataBase.getDbConnection().commit();

        ResultSet rs = prepStat.getGeneratedKeys();
        if (rs.next()) {
            int index = rs.getInt(1) - items.size();
            for (T item : items) {
                item.setId(++index);
            }
        }
        rs.close();
        return this;
    }

    public DataBaseHelper<T> update(ArrayList<T> items) throws Exception {
        if (items.size() < 1) return this;

        prepStat = dataBase.getDbConnection().prepareStatement(requestFactory.createRequestUpdate(items));
        for (T item : items) {
            prepStat.setInt(dataExchanger.fromObjectToDb(item, prepStat), item.getId());
            prepStat.addBatch();
        }

        int[] result = prepStat.executeBatch();
        dataBase.getDbConnection().commit();
        return this;
    }

    private ArrayList<Field> getTreatedFields() {
        ArrayList<Field> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            boolean ignore = false;
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation instanceof DbFieldIgnore) {
                    System.out.println("field ignored: " + clazz.getSimpleName() + ", " + field.getName());
                    ignore = true;
                    break;
                }
            }
            if (!ignore) {
                field.setAccessible(true);
                fields.add(field);
            }
        }
        return fields;
    }
}
