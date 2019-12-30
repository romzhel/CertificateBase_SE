package database.se;

import org.sqlite.SQLiteConfig;

import java.io.File;
import java.sql.*;

public class DataBase {
    private Connection dbConnection;
    private File dataBaseFile;
    private boolean firstStart = true;

    public DataBase(File dataBaseFile) {
        this.dataBaseFile = dataBaseFile;
    }

    public boolean connect() {
        SQLiteConfig config = null;
        try {
            Class.forName("org.sqlite.JDBC");
            config = new SQLiteConfig();
            config.setJournalMode(SQLiteConfig.JournalMode.TRUNCATE);
        } catch (ClassNotFoundException e1) {
            System.out.println("DB class not found " + e1.getMessage());
        }

        try {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:" + dataBaseFile.getPath(), config.toProperties());

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

    public String getDbJournalingMode() {
        firstStart = false;
        String result = "";
        try {
            dbConnection.setAutoCommit(true);
            Statement stat = dbConnection.createStatement();
            ResultSet rs = stat.executeQuery("PRAGMA journal_mode");
            result = rs.getString(1);
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return "jMode = " + result;
    }

    public Connection getDbConnection() {
        return dbConnection;
    }
}
