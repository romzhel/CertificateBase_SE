package database;

import core.CoreModule;

import java.sql.Connection;
import java.util.ArrayList;

public interface Request {
    //    DataBase database = CoreModule.getDataBase();
    Connection connection = CoreModule.getDataBase().getDbConnection();

    ArrayList getData();

    boolean putData(Object object);

    boolean updateData(Object object);

    boolean deleteData(Object object);
}
