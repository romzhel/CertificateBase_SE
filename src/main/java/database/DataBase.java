package database;

import org.sqlite.SQLiteConfig;
import ui_windows.ExecutionIndicator;

import java.io.File;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

public class DataBase {
    private static DataBase instance;
    private Connection dbConnection;
    private File dataBaseFile;
    private Timer timer;
    private boolean firstStart = true;
    private Supplier<Boolean> disconnectLink;

    private DataBase() {
        disconnectLink = this::disconnect;
    }

    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

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
            ExecutionIndicator.getInstance().start();
            if (firstStart) {
                System.out.println(getDbJournalingMode());
            }
            return true;
        } catch (SQLException e2) {
            System.out.println("can't connect to DB file " + e2.getMessage());
        }

        return false;
    }

    public void requestToDisconnect() {
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                disconnectLink.get();
            }
        }, 3000);
    }

    public boolean disconnect() {
        try {
            if (!dbConnection.isClosed()) {
                dbConnection.close();
                System.out.println("DB disconnected");
                ExecutionIndicator.getInstance().stop();
            }
            return true;
        } catch (Exception e2) {
            System.out.println("error of DB closing " + e2.getMessage());
            return false;
        }
    }

    public Connection reconnect() {
        if (timer != null) {
            timer.cancel();
        }

        try {
            if (dbConnection.isClosed()) {
                connect(dataBaseFile);

                new DbBackuper();
            }
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

    public File getDataBaseFile() {
        return dataBaseFile;
    }
}
