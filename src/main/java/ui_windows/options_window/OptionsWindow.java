package ui_windows.options_window;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.OrdinalWindow;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.profile_editor.Profile;
import ui_windows.options_window.user_editor.Users;
import utils.Utils;

import static ui_windows.options_window.profile_editor.SimpleRight.*;

public class OptionsWindow extends OrdinalWindow {
    private static TabPane tpOptions;
    private static OptionsWindowController controller;

    private static final Logger logger = LogManager.getLogger(OptionsWindow.class);

    public OptionsWindow() {
        super(MainWindow.getMainStage(), Modality.APPLICATION_MODAL,
                null, "/fxml/optionsWindow.fxml", "Настройки");

        logger.trace("Getting controller");
        controller = loader.getController();
        logger.trace("Controller was got");
        applyProfile();
        logger.trace("Profiles applied");

        stage.setResizable(true);
        logger.trace("Start to show Options window");
        stage.show();
        logger.trace("Options window shown");
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
        Profile profile = Users.getInstance().getCurrentUser().getProfile();

        Utils.getTabById("tabCertificates").setDisable(profile.getCertificates() == HIDE);
        Utils.disableEditing(Utils.getTabById("tabCertificates"), profile.getCertificates() == DISPLAY);
        profile.getCertificates().apply(controller.cmCertTypes);
        profile.getCertificates().apply(controller.cmCertificates);
        profile.disableDeleteItem(controller.cmCertTypes);
        profile.disableDeleteItem(controller.cmCertificates);

        Utils.getTabById("tabFamilies").setDisable(profile.getFamilies() == HIDE);
        Utils.disableEditing(Utils.getTabById("tabFamilies"), profile.getFamilies() == DISPLAY);
        profile.getFamilies().apply(controller.cmFamilies);
        profile.getFamilies().apply(controller.cmLgbkHierarchy);
        profile.disableDeleteItem(controller.cmFamilies);
        profile.disableDeleteItem(controller.cmLgbkHierarchy);

        Utils.getTabById("tabOrderable").setDisable(profile.getOrderAccessible() == HIDE);
        Utils.disableEditing(Utils.getTabById("tabOrderable"), profile.getOrderAccessible() == DISPLAY);
        profile.getOrderAccessible().apply(controller.cmOrderable);
        profile.disableDeleteItem(controller.cmOrderable);

        Utils.getTabById("tabUsers").setDisable(profile.getUsers() == HIDE);
        Utils.disableEditing(Utils.getTabById("tabUsers"), profile.getUsers() == DISPLAY);

        Utils.getTabById("tabPriceLists").setDisable(profile.getPriceLists() == HIDE);
        profile.getPriceLists().apply(controller.cmPriceListsTable);


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
