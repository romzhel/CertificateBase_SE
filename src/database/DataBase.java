package database;

import java.io.File;
import java.sql.*;

public class DataBase {
    private Connection dbConnection;
    private File dataBaseFile;

    public boolean connect(File dbFile) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e1) {
            System.out.println("DB class not found " + e1.getMessage());
        }

        try {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getPath());
            dataBaseFile = dbFile;
            System.out.println("DB connected");
            return true;
        } catch (SQLException e2) {
            System.out.println("can't connect to DB file " + e2.getMessage());
        }

        return false;
    }

    public boolean disconnect() {
        try {
            dbConnection.close();
            System.out.println("DB disconnected");
            return true;
        } catch (SQLException e2) {
            System.out.println("error of DB closing " + e2.getMessage());
        }

        return false;
    }

    public boolean reconnect(){
        boolean result = false;

        try {
            if (dbConnection.isClosed()) result = connect(dataBaseFile);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    public ResultSet getData(String request) {
        try {
            return dbConnection.prepareStatement(request).executeQuery();
        } catch (SQLException e) {
            System.out.println("error of getting data from DB");
        }
        return null;
    }

    public Connection getDbConnection() {
        return dbConnection;
    }
}
