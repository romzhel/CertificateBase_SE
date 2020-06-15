package database;

import core.Dialogs;
import javafx.application.Platform;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbRequest {
    protected PreparedStatement getData, addData, updateData, deleteData;
    protected Connection connection;

    public DbRequest() {
        connection = DataBase.getInstance().reconnect();
    }

    public void logAndMessage(String text) {
        Platform.runLater(() -> Dialogs.showMessage("Ошибка работы с базой данных", text));
        System.out.println("Ошибка работы с базой данных: " + text);
    }

    public void finalActions() {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        DataBase.getInstance().requestToDisconnect();
    }


}
