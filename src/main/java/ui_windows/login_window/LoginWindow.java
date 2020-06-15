package ui_windows.login_window;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui_windows.main_window.MainWindow;

public class LoginWindow {
    private static Stage stage;

    public LoginWindow() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/loginWindow.fxml"));
        } catch (Exception e) {
            System.out.println("exception" + e.getMessage());
        }

        stage = new Stage();
        stage.setTitle("Авторизация");
        stage.setScene(new Scene(root, 300, 115));
        stage.initOwner(MainWindow.getMainStage());
        stage.initModality(Modality.APPLICATION_MODAL);

//        primaryStage.setResizable(false);
//        primaryStage.setAlwaysOnTop(true);
        stage.show();
    }

    public static Stage getStage() {
        return stage;
    }
}
