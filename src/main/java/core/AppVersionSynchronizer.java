package core;

import database.DataBase;
import database.DbUtils;
import lombok.extern.log4j.Log4j2;
import ui_windows.options_window.user_editor.Users;

import java.sql.Connection;

@Log4j2
public class AppVersionSynchronizer implements Runnable {

    public void run() {
        if (Users.getInstance().getCurrentUser().getProfile().getId() != 2 || !DbUtils.getInstance().isAppVersionNeedUpdateInDb()) {
            return;
        }

        try {
            Connection dbConnection = DataBase.getInstance().reconnect();
            DbUtils.getInstance().syncAppVersion(dbConnection);
            DataBase.getInstance().disconnect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
