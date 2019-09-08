package database;

import org.sqlite.SQLiteConfig;

import java.io.File;
import java.sql.*;

public class DataBase {
    private Connection dbConnection;
    private File dataBaseFile;
    private boolean firstStart = true;

    public boolean connect(File dbFile) {
        SQLiteConfig config = null;
        try {
            Class.forName("org.sqlite.JDBC");
            config = new SQLiteConfig();
            config.setJournalMode(SQLiteConfig.JournalMode.TRUNCATE);
        } catch (ClassNotFoundException e1) {
            System.out.println("DB class not found " + e1.getMessage());
        }

        try {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getPath(), config.toProperties());
            dataBaseFile = dbFile;



            System.out.println("DB connected");
            if (firstStart) System.out.println(getDbJournalingMode());
            return true;
        } catch (SQLException e2) {
            System.out.println("can't connect to DB file " + e2.getMessage());
        }

        return false;
    }

    public boolean disconnect() {
        try {
            if (!dbConnection.isClosed()) {
                dbConnection.close();
                System.out.println("DB disconnected");
            }
            return true;
        } catch (SQLException e2) {
            System.out.println("error of DB closing " + e2.getMessage());
            return false;
        }
    }

    public Connection reconnect() {
        try {
            if (dbConnection.isClosed()) connect(dataBaseFile);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return dbConnection;
    }

    public ResultSet getData(String request) {
        try {
            return dbConnection.prepareStatement(request).executeQuery();
        } catch (SQLException e) {
            System.out.println("error of getting data from DB");
        }
        return null;
    }

    public String getDbJournalingMode() {
        firstStart = false;
        String result = "";
        try {
            dbConnection.setAutoCommit(true);
            Statement stat = dbConnection.createStatement();
            ResultSet rt = stat.executeQuery("PRAGMA journal_mode");
            result = rt.getString(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return "jMode = " + result;
    }

    public Connection getDbConnection() {
        return dbConnection;
    }
}
