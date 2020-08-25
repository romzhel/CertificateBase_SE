package database;

import core.Initializable;
import files.Folders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sqlite.SQLiteConfig;
import ui_windows.ExecutionIndicator;

import java.io.File;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

public class DataBase implements Initializable {
    private static final Logger logger = LogManager.getLogger(DataBase.class);
    private static DataBase instance;
    private Connection dbConnection;
    private File dbFile;
    private Timer timer;
    private boolean firstStart;
    private Supplier<Boolean> disconnectLink;
    private SQLiteConfig config;

    private DataBase() throws Exception {
        try {
            Class.forName("org.sqlite.JDBC");
            config = new SQLiteConfig();
            config.setJournalMode(SQLiteConfig.JournalMode.TRUNCATE);
            disconnectLink = this::disconnect;
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
        File cashedBdFile = Folders.getInstance().getCashedDbFile().toFile();
        try {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:" + cashedBdFile.getPath(), config.toProperties());
            dbFile = Folders.getInstance().getMainDbFile().toFile();

            logger.debug("cashed db file {} is connected, journaling mode {}", cashedBdFile, getDbJournalingMode());
            logger.debug("main db file is {}", dbFile);
        } catch (SQLException e2) {
            logger.fatal("can't connect to DB file {}: {}", cashedBdFile, e2.getMessage());
            throw new RuntimeException(e2);
        }
    }

    public Connection reconnect() {
        if (timer != null) {
            timer.cancel();
            logger.debug("db delayed disconnection timer is cancelled");
        }

        try {
            if (dbConnection.isClosed()) {
                dbConnection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getPath(), config.toProperties());

                logger.debug("main db file {} is connected", dbFile);

                ExecutionIndicator.getInstance().start();
            }
        } catch (SQLException e) {
            logger.error("db reconnect error: {}", e.getMessage(), e);
        }

        return dbConnection;
    }

    public void requestToDisconnect() {
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                disconnectLink.get();
            }
        }, 3000);
        logger.debug("db delayed disconnection timer started");
    }

    public boolean disconnect() {
        try {
            if (!dbConnection.isClosed()) {
                if (firstStart) {
                    firstStart = false;
                } else {
                    new DbBackuper();
                }

                dbConnection.close();
                logger.debug("db disconnected");

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
        return dbConnection;
    }

    public File getDbFile() {
        return dbFile;
    }
}
