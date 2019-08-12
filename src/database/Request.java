package database;

import core.CoreModule;

import java.util.ArrayList;

public interface Request {
//    DataBase database = CoreModule.getDataBase();
    ArrayList getData();
    boolean putData(Object object);
    boolean updateData(Object object);
    boolean deleteData(Object object);
}
