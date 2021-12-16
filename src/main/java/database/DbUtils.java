package database;

import core.App;
import exceptions.AppExpiredException;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class DbUtils {
    private static DbUtils instance;
    private int appVersionValue;
    private int appDbVersionValue;

    private DbUtils() {
    }

    public static DbUtils getInstance() {
        if (instance == null) {
            instance = new DbUtils();
        }
        return instance;
    }

    public void checkAppVersion(Connection dbConnection) throws Exception {
        appDbVersionValue = getAppVersionValueFromDb(dbConnection);
        appVersionValue = getVersionValue(App.getProperties().getVersion());

        log.info("current app version = {} ({}), in db = {}", appVersionValue, App.getProperties().getVersion(),
                appDbVersionValue);

        if (appVersionValue < appDbVersionValue) {
            throw new AppExpiredException("App expired, new version " + appDbVersionValue);
        }
    }

    public boolean isAppVersionNeedUpdateInDb() {
        return appVersionValue > appDbVersionValue;
    }

    public void syncAppVersion(Connection dbConnection) throws Exception {
        log.info("update app version in db: {} -> {}", appDbVersionValue, appVersionValue);

        PreparedStatement stat = dbConnection.prepareStatement("UPDATE info SET val = ? WHERE param = 'version'");
        stat.setInt(1, appVersionValue);
        int res = stat.executeUpdate();
        stat.close();

        if (res != 1) {
            log.warn("can't update app version in DB");
        }
    }

    public int getAppVersionValueFromDb(Connection dbConnection) throws Exception {
        Statement stat = dbConnection.createStatement();
        ResultSet rs = stat.executeQuery("SELECT val FROM info WHERE param = 'version'");
        int dbVersion = rs.getInt(1);
        stat.close();

        return dbVersion;
    }

    public int getVersionValue(String version) {
        if (version == null) return 0;

        Pattern p = Pattern.compile("(\\d+)(\\D+||$)");
        Matcher m = p.matcher(version);

        int scale = 1_000_000;
        int id = 0;
        while (m.find()) {
            id += Integer.parseInt(m.group(1)) * scale;
            scale /= 100;
        }

        return id;
    }
}
