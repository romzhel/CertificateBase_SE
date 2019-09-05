package database;

import core.CoreModule;

import java.sql.Connection;
import java.util.ArrayList;

public interface Request {
    ArrayList getData();

    boolean putData(Object object);

    boolean updateData(Object object);

    boolean deleteData(Object object);
}
