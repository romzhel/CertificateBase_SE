package database;

import core.CoreModule;
import core.Dialogs;
import javafx.application.Platform;
import ui_windows.main_window.MainWindow;

import java.sql.*;

public class DbRequest {
    protected PreparedStatement getData, addData, updateData, deleteData;
    protected Connection connection;

    public DbRequest() {
        connection = CoreModule.getDataBase().reconnect();
    }

    public void logAndMessage(String text){
        Platform.runLater(() -> Dialogs.showMessage("Ошибка работы с базой данных", text));
        System.out.println("Ошибка работы с базой данных: " + text);
    }

    public void finalActions(){
        MainWindow.setProgress(0.0);
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        CoreModule.getDataBase().requestToDisconnect();
    }


}
