package database;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui.Dialogs;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DbRequest {
    protected PreparedStatement getData, addData, updateData, deleteData;
    protected Connection connection;
    private static final Logger logger = LogManager.getLogger(DbRequest.class);

    public DbRequest() {
        connection = DataBase.getInstance().reconnect();
    }

    public void logAndMessage(String text, Throwable ex) {
        Platform.runLater(() -> Dialogs.showMessage("Ошибка работы с базой данных: ", text + "\n" + ex.getMessage()));
        logger.error("db error {}", ex.getMessage(), ex);
    }

    public void finalActions() {
        try {
            logger.trace("final actions");
            connection.setAutoCommit(true);
            DataBase.getInstance().requestToDisconnect();
        } catch (Exception e) {
            logger.error("ошибка БД {}", e.getMessage(), e);
        }
    }
}
