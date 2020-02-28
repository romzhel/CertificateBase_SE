package ui_windows.options_window.user_editor;

import core.CoreModule;
import core.Dialogs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import utils.Utils;
import utils.comparation.products.ObjectsComparator;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.TreeSet;

import static ui_windows.Mode.*;

public class userEditorWindowController implements Initializable {

    @FXML
    ListView<String> lvFamilies;

    @FXML
    ListView<String> lvSelectedFamilies;

    @FXML
    ComboBox<String> cbProfile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvFamilies.getItems().addAll(CoreModule.getProductFamilies().getFamiliesNames());
        cbProfile.getItems().addAll(CoreModule.getProfiles().getProfilesName());

        lvFamilies.setOnMouseClicked(event -> {//double click on product
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    moveFamily();
                }
            }
        });

        lvSelectedFamilies.setOnMouseClicked(event -> {//double click on product
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    removeFamily();
                }
            }
        });
    }

    public void actionApply() {
        if (Utils.hasEmptyControls(UserEditorWindow.getRootAnchorPane(),
                "lvFamilies", "tfPassword", "lvPcNames")) return;

        User newUser = null;

        if (UserEditorWindow.getMode() == ADD) {
            newUser = new User(UserEditorWindow.getRootAnchorPane());

            if (!newUser.getPassword().isEmpty()) {
                if (!CoreModule.getUsers().isPasswordUsed(newUser.getPassword()))
                    CoreModule.getUsers().addItem(new User(UserEditorWindow.getRootAnchorPane()));
                else {
                    Dialogs.showMessage("Добавление пользователя", "Такой пароль уже используется");
                    return;
                }
            } else CoreModule.getUsers().addItem(new User(UserEditorWindow.getRootAnchorPane()));

        } else if (UserEditorWindow.getMode() == EDIT) {

            User editedUser = CoreModule.getUsers().getTable().getSelectedItem();
            newUser = new User(UserEditorWindow.getRootAnchorPane());

            if (newUser.getPassword() == null || newUser.getPassword().trim().isEmpty())
                newUser.setPassword(editedUser.getPassword());
            else {
                if (CoreModule.getUsers().isPasswordUsed(newUser.getPassword())) {
                    Dialogs.showMessage("Добавление пользователя", "Такой пароль уже используется");
                    return;
                }
//                else newUser.setPassword(editedUser.getPassword());
            }

            ObjectsComparator oc = new ObjectsComparator(editedUser, newUser, false);

            if (!editedUser.getProfile().equals(newUser.getProfile()) || oc.getResult().isNeedUpdateInDB()) {
                editedUser.setProfile(newUser.getProfile());
                CoreModule.getUsers().editItem(editedUser);
            }
        }

        actionClose();
    }

    public void actionClose() {
        ((Stage) lvFamilies.getScene().getWindow()).close();
    }

    public void moveFamily() {
        lvFamilies.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        for (int ind : lvFamilies.getSelectionModel().getSelectedIndices()) {
            lvSelectedFamilies.getItems().add(lvFamilies.getItems().remove(ind));
        }

        sortItems(lvFamilies);
        sortItems(lvSelectedFamilies);
    }

    public void removeFamily() {
        lvSelectedFamilies.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        for (int ind : lvSelectedFamilies.getSelectionModel().getSelectedIndices()) {
            lvFamilies.getItems().add(lvSelectedFamilies.getItems().remove(ind));
        }

        sortItems(lvFamilies);
        sortItems(lvSelectedFamilies);
    }

    public void moveAllFamilies() {
        lvSelectedFamilies.getItems().addAll(lvFamilies.getItems());
        lvFamilies.getItems().clear();

        sortItems(lvFamilies);
        sortItems(lvSelectedFamilies);
    }

    public void removeAllFamilies() {
        lvFamilies.getItems().addAll(lvSelectedFamilies.getItems());
        lvSelectedFamilies.getItems().clear();

        sortItems(lvFamilies);
        sortItems(lvSelectedFamilies);
    }

    public void sortItems(ListView<String> listView) {
        TreeSet<String> items = new TreeSet<>(listView.getItems());
        listView.getItems().clear();
        listView.getItems().addAll(items);
    }
}
