package ui_windows.login_window;

import database.UsersDB;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import ui.Dialogs;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.profile_editor.Profile;
import ui_windows.options_window.user_editor.User;
import ui_windows.options_window.user_editor.Users;
import utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginWindowController implements Initializable {

    @FXML
    PasswordField pfPassword;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pfPassword.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) actionOK();
        });
    }

    public void actionOK() {
        User currUser = Users.getInstance().getCurrentUser();
        Profile currProfile = currUser.getProfile();

        User newUser = Users.getInstance().checkCurrentUser(pfPassword.getText().trim());
        Profile newProfile = newUser.getProfile();

        LoginWindow.getStage().close();

        if (currUser.getSurname().equals(newUser.getSurname())) {
            Dialogs.showMessage("Изменение уровня доступа", "Уровень доступа не изменился");
        } else {
            MainWindow.applyProfile(newProfile);

            String message = "Уровень доступа изменился на \n- " + newProfile.getName() + " (" +
                    newUser.getName() + " " + newUser.getSurname() + ")";

            if (!newProfile.getName().equals("Общий доступ") && !newProfile.getName().equals("тест") &&
                    !Users.getInstance().isPcNameUsed(Utils.getComputerName())) {
                message += "\n\nЖелаете привязать аккаунт к компьютеру, чтобы не было " +
                        "необходимости каждый раз вводить пароль?\n";

                if (Dialogs.confirm("Изменение уровня доступа", message)) {
                    newUser.addPcname(Utils.getComputerName());
                    new UsersDB().updateData(newUser);
                }

            } else {
                Dialogs.showMessage("Изменение уровня доступа", message);
            }
        }
    }

    public void actionCancel() {
        LoginWindow.getStage().close();
    }


}
