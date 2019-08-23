package ui_windows.options_window;

import core.CoreModule;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui_windows.OrdinalWindow;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.profile_editor.Profile;
import utils.Utils;

import static ui_windows.options_window.profile_editor.SimpleRight.*;

public class OptionsWindow extends OrdinalWindow {
    private static TabPane tpOptions;

    public OptionsWindow() {
        super(MainWindow.getMainStage(), Modality.APPLICATION_MODAL,
                null, "optionsWindow.fxml", "Настройки");

        applyProfile();

        stage.setResizable(true);
        stage.show();
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
    }


    public static Stage getStage() {
        return stage;
    }

    public static TabPane getTpOptions() {
        return tpOptions;
    }

    public static void setTpOptions(TabPane tpOptions) {
        OptionsWindow.tpOptions = tpOptions;
    }

    private void applyProfile() {
        Profile profile = CoreModule.getUsers().getCurrentUser().getProfile();

        Utils.getTabById("tabCertificates").setDisable(profile.getCertificates() == HIDE);
        Utils.disableEditing(Utils.getTabById("tabCertificates"), profile.getCertificates() == DISPLAY);

        Utils.getTabById("tabFamilies").setDisable(profile.getFamilies() == HIDE);
        Utils.disableEditing(Utils.getTabById("tabFamilies"), profile.getFamilies() == DISPLAY);

        Utils.getTabById("tabOrderable").setDisable(profile.getOrderAccessible() == HIDE);
        Utils.disableEditing(Utils.getTabById("tabOrderable"), profile.getOrderAccessible() == DISPLAY);

        Utils.getTabById("tabUsers").setDisable(profile.getUsers() == HIDE);
        Utils.disableEditing(Utils.getTabById("tabUsers"), profile.getUsers() == DISPLAY);

        for (Tab tab : tpOptions.getTabs()) {
            if (!tab.isDisabled()) {
                tpOptions.getSelectionModel().select(tab);
                break;
            }
        }

        boolean ownRights = profile.getCertificates() == OWN;
        if (ownRights) {
            Utils.disableMenuItemsButton(Utils.getTabById("tabCertificates"), "cmCertTypes");
            Utils.disableMenuItemsButton(Utils.getTabById("tabCertificates"), "cmCertificates", "miAdd1");
        }
    }
}
