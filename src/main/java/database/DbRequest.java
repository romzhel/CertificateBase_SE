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
        connection = DataBase.getInstance().getDbConnection();
    }

    public void logAndMessage(String text) {
        Platform.runLater(() -> Dialogs.showMessage("Ошибка работы с базой данных", text));
        logger.error("db error {}", text);
    }

    public void finalActions() {
        try {
            connection.setAutoCommit(true);
            DataBase.getInstance().requestToDisconnect();
        } catch (Exception e) {
            logger.error("ошибка БД {}", e.getMessage(), e);
        }
    }
}
