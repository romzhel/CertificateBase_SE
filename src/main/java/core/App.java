package core;

import ui_windows.main_window.MainWindow;

import java.io.InputStream;
import java.util.Properties;

public class App {
    private static final String PROP_FILE_NAME = "application.properties";
    private static App instance;
    private static Properties properties;

    private App() throws Exception {
        properties = new Properties();
        InputStream propFile = MainWindow.class.getClassLoader().getResourceAsStream(PROP_FILE_NAME);
        properties.load(propFile);
        propFile.close();
    }

    public static App getProperties() throws Exception {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    public String getVersion() {
        return String.format("%s от %s", properties.getProperty("app_version"), properties.getProperty("app_date"))
                .replace("- ", " ");
    }

    public String getDbFileName() {
        return properties.getProperty("db_file_name");
    }

    public String getRemoteDbFolder() {
        return properties.getProperty("remote_db_folder");
    }
}
