package database;

import core.Initializable;
import files.Folders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;
import ui.Dialogs;
import ui_windows.ExecutionIndicator;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

public class DataBase implements Initializable {
    private static final Logger logger = LogManager.getLogger(DataBase.class);
    private static DataBase instance;
    private Connection dbConnection;
    private File dbFile;
    private Timer timer;
    private boolean firstStart;
    private SQLiteConfig config;
    private SQLiteDataSource sqLiteDataSource;

    private DataBase() throws Exception {
        try {
            SQLiteConfig sqLiteConfig = new SQLiteConfig();
            sqLiteConfig.setJournalMode(SQLiteConfig.JournalMode.TRUNCATE);
            sqLiteDataSource = new SQLiteDataSource(sqLiteConfig);
//            Class.forName("org.sqlite.JDBC");
//            config = new SQLiteConfig();
//            config.setJournalMode(SQLiteConfig.JournalMode.TRUNCATE);
        } catch (Exception e) {
            logger.fatal(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static DataBase getInstance() {
        if (instance == null) {
            try {
                instance = new DataBase();
            } catch (Exception e) {
                logger.fatal("error DB class creating {}", e.getMessage(), e);
            }
        }
        return instance;
    }

    @Override
    public void init() throws Exception {
        firstStart = true;
        Path cashedBdFile = Folders.getInstance().getCashedDbFile();
        try {
//            dbConnection = DriverManager.getConnection("jdbc:sqlite:" + cashedBdFile.getPath(), config.toProperties());

            sqLiteDataSource.setUrl("jdbc:sqlite:" + cashedBdFile.toString());
            dbConnection = sqLiteDataSource.getConnection();

            dbFile = Folders.getInstance().getMainDbFile().toFile();

            logger.debug("cashed db file {} is connected, journaling mode {}", cashedBdFile, getDbJournalingMode());
            logger.debug("main db file is {}", dbFile);
        } catch (SQLException e2) {
            logger.fatal("can't connect to DB file {}: {}", cashedBdFile, e2.getMessage());
            throw new RuntimeException(e2);
        }
    }

    public Connection reconnect() {
        logger.debug("reconnecting to db, disconnection timer {}", timer);
        if (timer != null) {
            timer.cancel();
            timer = null;
            logger.debug("db delayed disconnection timer {} is cancelled", timer);
        }

        try {
            if (dbConnection.isClosed()) {
//                dbConnection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getPath(), config.toProperties());

                sqLiteDataSource.setUrl("jdbc:sqlite:" + dbFile.getPath());
                dbConnection = sqLiteDataSource.getConnection();

                logger.debug("main db file {} is connected", dbFile);

                ExecutionIndicator.getInstance().start();
            }
        } catch (SQLException e) {
            logger.error("db reconnect error: {}", e.getMessage(), e);
        }

        return dbConnection;
    }

    public void requestToDisconnect() {
        timer = new Timer("DB Disconnector Thread", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.debug("timer {} expired", timer);
                disconnect();
            }
        }, 5000);
        logger.debug("operation completed, db delayed disconnection timer started {}", timer);
    }

    public boolean disconnect() {
        timer = null;
        try {
            if (!dbConnection.isClosed()) {
                dbConnection.close();
                logger.debug("db disconnected");

                if (firstStart) {
                    firstStart = false;
                } else {
//                    DbBackuper.run(); //TODO перенести до изменений в базе
                }

                ExecutionIndicator.getInstance().stop();
            }
            return true;
        } catch (Exception e2) {
            logger.warn("error of DB closing {}", e2.getMessage(), e2);
            return false;
        }
    }

    /*public ResultSet getData(String request) {
        try {
            return dbConnection.prepareStatement(request).executeQuery();
        } catch (SQLException e) {
            System.out.println("error of getting data from DB");
        }
        return null;
    }*/

    public String getDbJournalingMode() {
        String result = "";
        try {
            dbConnection.setAutoCommit(true);
            Statement stat = dbConnection.createStatement();
            ResultSet rs = stat.executeQuery("PRAGMA journal_mode");
            result = rs.getString(1);
            rs.close();
        } catch (SQLException e) {
            logger.warn("error getting db journaling mode {}", e.getMessage());
        }
        return "jMode = " + result;
    }

    public Connection getDbConnection() {
        try {
            return dbConnection.isClosed() ? reconnect() : dbConnection;
        } catch (SQLException e) {
            logger.fatal("error getting db connection {}", e.getMessage(), e);
            Dialogs.showMessageTS("Обращение к базе данных", "Ошибка подключения");
            throw new RuntimeException("error getting db connection");
        }
    }

    public File getDbFile() {
        return dbFile;
    }
}
