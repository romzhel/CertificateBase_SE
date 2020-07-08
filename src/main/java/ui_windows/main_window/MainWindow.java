package ui_windows.main_window;

//import core.AddActions;

import core.CoreModule;
import core.Dialogs;
import core.logger.LoggerInit;
import core.logger.LogsBackuper;
import database.DataBase;
import files.Folders;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import ui_windows.ExecutionIndicator;
import ui_windows.main_window.filter_window_se.FilterParameters_SE;
import ui_windows.options_window.profile_editor.Profile;
import ui_windows.options_window.user_editor.Users;
import utils.Utils;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ui_windows.options_window.profile_editor.SimpleRight.HIDE;

public class MainWindow extends Application {
    private static final LoggerInit loggerInit = new LoggerInit();
    private static final Logger logger = LogManager.getLogger(MainWindow.class);
    private static String version;
    private static Properties appProperties;
    private static Stage mainStage;
    private static AnchorPane rootAnchorPane;
    private static MenuItem miOptions;
    private static FXMLLoader fxmlLoader;
    private static MainWindowsController controller;

    public static void main(String[] args) {
        appProperties = new Properties();
        try (InputStream propFile = MainWindow.class.getClassLoader().getResourceAsStream("application.properties")) {
            appProperties.load(propFile);
        } catch (Exception e) {
            System.out.println("properties file not found");
            Platform.exit();
        }

        version = appProperties.getProperty("app_version") + " от " + appProperties.getProperty("app_date");
        logger.info("App starting, user = {}, app version = {}, db = {}", System.getProperty("user.name"), version,
                Folders.DB_FILE_NAME);
        launch(args);
    }

    public static void setMiOptions(MenuItem miOptions) {
        MainWindow.miOptions = miOptions;
    }

    public static void applyProfile(Profile profile) {
//        miFile.setDisable(profile.getFileMenu() == HIDE);
        controller.mniOpenNow.setDisable(profile.getFileMenuOpen() == HIDE);
        controller.mPriceList.setDisable(profile.getFileMenuExportPrice() == HIDE);

        miOptions.setDisable(profile.getOptionsMenu() == HIDE);
    }

    public static AnchorPane getRootAnchorPane() {
        return rootAnchorPane;
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static MainWindowsController getController() {
        return controller;
    }

    public static MainTable getMainTable() {
        return controller.getMainTable();
    }

    public static Properties getAppProperties() {
        return appProperties;
    }

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;

        try {
            new CoreModule().init();

            fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mainWindow.fxml"));
            rootAnchorPane = fxmlLoader.load();

            controller = (MainWindowsController) fxmlLoader.getController();
            Scene scene = new Scene(rootAnchorPane);

            scene.setOnKeyPressed(event -> {
                if (event.getCode().getName().equals("F12")) {
                    Dialogs.showMessage("Инфо", version);
                }
            });

            mainStage.setScene(scene);
            mainStage.setTitle("База по продукции и сертификатам");
            mainStage.setResizable(true);
            mainStage.setIconified(false);

            rootAnchorPane.getChildren().add(FilterParameters_SE.getSearchBox());
            AnchorPane.setBottomAnchor(FilterParameters_SE.getSearchBox(), 39.0);
            AnchorPane.setLeftAnchor(FilterParameters_SE.getSearchBox(), 158.0);
            FilterParameters_SE.getSearchBox().getTextBox().requestFocus();

            final String searchBoxCss = getClass().getResource("/css/SearchBox.css").toExternalForm();
            rootAnchorPane.getStylesheets().add(searchBoxCss);

            applyProfile(Users.getInstance().getCurrentUser().getProfile());

            mainStage.show();
            mainStage.setMinHeight(mainStage.getHeight());
            mainStage.setMinWidth(mainStage.getWidth());

//            new AddActions().make();

//        OptionsWindow certificateOverviewWindow = new OptionsWindow();
//        certificateOverviewWindow.open();
        } catch (Exception e) {
            logger.fatal("Program init error: {}", e.getMessage(), e);
            Dialogs.showMessage("Ошибка инициализация программы", "Программа не может продолжить работу." +
                    "\nПричина: " + e.getMessage());
            Platform.exit();
        }

        mainStage.setOnCloseRequest(event -> {
            if (ExecutionIndicator.getInstance().hasActiveProcess()) {
                if (!Dialogs.confirmTS("Закрытие программы", "Не все процессы завершены.\n\n " +
                        "Всё равно желаете закрыть программу?")) {
                    event.consume();
                    return;
                }
            }

            try {
                File tempFolder = Folders.getInstance().getTempFolder();
                String[] filesList;
                if (tempFolder != null && tempFolder.exists()
                        && (filesList = tempFolder.list((dir, name) -> name.matches(".*\\.xlsx?$"))) != null
                        && filesList.length > 0) {
                    Utils.openFile(Folders.getInstance().getTempFolder());

                    if (Dialogs.confirmTS("Удаление временной папки",
                            "Временная папка не пуста.\n\nУдалить временную папку и все файлы внутри неё?\n")) {
                        Utils.deleteFolder(Folders.getInstance().getTempFolder().toPath());
                    }
                }
                logger.info("app closed");

                File[] dBfilesList = new File(Folders.APP_FOLDER).listFiles(pathname -> pathname.getName().endsWith(".db"));
                TreeSet<File> files = new TreeSet<>((o1, o2) -> o2.getName().compareTo(o1.getName()));
                files.addAll(Arrays.asList(dBfilesList));

                File toKeepFile = files.first();
                logger.debug("newest db file " + toKeepFile.getPath() + " will be kept, other - deletes");
                files.forEach(file -> {
                    if (file != toKeepFile) file.delete();
                });

                Configuration conf = ((LoggerContext) LogManager.getContext(false)).getConfiguration();
                conf.getAppenders().get("FILE").stop();
                System.out.println(Folders.getInstance().getAppLogsFolder());
//                new Thread(() -> {
                ScheduledExecutorService logsTreatment = Executors.newSingleThreadScheduledExecutor();
                logsTreatment.schedule(() -> LogsBackuper.getInstance().backup(), 3, TimeUnit.SECONDS);
                logsTreatment.shutdown();
//                }).start();
                System.out.println(Folders.getInstance().getAppLogsFolder());
            } catch (Exception e) {
                logger.error("app closing error: {}", e.getMessage(), e);
            }
        });
    }

    @Override
    public void stop() {
        DataBase.getInstance().disconnect();

        try {
            MainWindow.getMainTable().close();
        } catch (Exception e) {

        }
        try {
            super.stop();
        } catch (Exception e) {

        }
    }
}
