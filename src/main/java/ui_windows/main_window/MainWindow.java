package ui_windows.main_window;

//import core.AddActions;

import com.sun.javafx.application.LauncherImpl;
import core.App;
import core.InitModule;
import core.logger.LoggerInit;
import core.logger.LogsBackuper;
import database.DataBase;
import database.DbUtils;
import exceptions.AppExpiredException;
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
import org.sqlite.SQLiteException;
import preloader.AppPreloader;
import ui.Dialogs;
import ui_windows.ExecutionIndicator;
import ui_windows.main_window.filter_window_se.FilterParameters_SE;
import ui_windows.options_window.profile_editor.Profile;
import ui_windows.options_window.user_editor.Users;
import utils.BytesToReadableFormatConverter;
import utils.Utils;
import utils.files.ResourceSynchronizer;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ui_windows.options_window.profile_editor.SimpleRight.HIDE;

public class MainWindow extends Application {
    private static final LoggerInit loggerInit = new LoggerInit().init();
    private static final Logger logger = LogManager.getLogger(MainWindow.class);
    private static Stage mainStage;
    private static AnchorPane rootAnchorPane;
    private static MenuItem miOptions;
    private static FXMLLoader fxmlLoader;
    private static MainWindowController controller;

    public static void main(String[] args) {
        LauncherImpl.launchApplication(MainWindow.class, AppPreloader.class, args);
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

    public static MainWindowController getController() {
        return controller;
    }

    public static MainTable getMainTable() {
        return controller.getMainTable();
    }

    @Override
    public void init() throws Exception {
        try {
            logger.info("App starting, user = {}, app version = {}, db = {}", System.getProperty("user.name"),
                    App.getProperties().getVersionWithDate(), App.getProperties().getDbFileName());

            long freeMem = Runtime.getRuntime().freeMemory();
            long totalMem = Runtime.getRuntime().totalMemory();
            long maxMem = Runtime.getRuntime().maxMemory();
            int availableProcessors = Runtime.getRuntime().availableProcessors();

            BytesToReadableFormatConverter converter = new BytesToReadableFormatConverter();
            logger.info("System memory: free {}, total {}, max {}",
                    converter.convert(freeMem), converter.convert(totalMem), converter.convert(maxMem));

            logger.info("System available processors count: {}", availableProcessors);

            new InitModule().init(this);
        } catch (AppExpiredException aee) {
            logger.fatal("App expired: {}", aee.getMessage(), aee);
            Dialogs.showMessageTS("Ошибка инициализации приложения", "Приложение устарело. Необходимо " +
                    "синхронизировать папку OneDrive на локальном компьютере и запускать программу оттуда (или через " +
                    "ярлык)\n\nСейчас откроется окно браузера, из которого можно будет осуществить синхронизацию.");
            openOneDriveInBrowser();
        } catch (SQLiteException sqle) {
            logger.fatal("SQL error: {}", sqle.getMessage(), sqle);
            String addReason = "";
            if (DbUtils.getInstance().isAppVersionNeedUpdateInDb()) {
                addReason = ", причиной которой может стать использование устаревшей " +
                        "версии программы\n\nНеобходимо Синхронизировать (не Скачать) папку с OneDrive на локальном " +
                        "компьютере и запускать программу из неё (или через ярлык)";
                openOneDriveInBrowser();
            }

            Dialogs.showMessageTS("Ошибка инициализации приложения", "Во время инициализации программы " +
                    "произошла ошибка\n\n" + sqle.getMessage() + addReason);
        } catch (Exception e) {
            logger.fatal("App init error: {}", e.getMessage(), e);
            Dialogs.showMessageTS("Ошибка инициализации приложения", "Во время инициализации программы " +
                    "произошла ошибка:\n\n" + e.getMessage());
            Platform.exit();
        }
    }

    private void openOneDriveInBrowser() throws IOException, URISyntaxException {
        Desktop desk = Desktop.getDesktop();
        desk.browse(new URI("https://siemens-my.sharepoint.com/:f:/p/roman_zheludkov/EowUKO6Ud21Jui5vjUKLHrIBTLcLWldNhPz9lDo6V7TvrA?e=UAtJ8Y"));
        Platform.exit();
    }

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;

        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mainWindow.fxml"));
            rootAnchorPane = fxmlLoader.load();

            controller = fxmlLoader.getController();
            Scene scene = new Scene(rootAnchorPane);

            scene.setOnKeyPressed(event -> {
                if (event.getCode().getName().equals("F12")) {
                    try {
                        Dialogs.showMessage("Инфо", App.getProperties().getVersionWithDate());
                    } catch (Exception e) {
                    }
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

            try {
                ResourceSynchronizer.synchronize(Folders.getInstance().getCertFolder(), Folders.getInstance().getCashedCertFolder());
            } catch (Exception e) {
                logger.warn("Ошибка запуска синхронайзера {}", e.getMessage());
            }

//            AddActions.make();

//        OptionsWindow certificateOverviewWindow = new OptionsWindow();
//        certificateOverviewWindow.open();
        } catch (Exception e) {
            logger.fatal("App start error: {}", e.getMessage(), e);
            Dialogs.showMessageTS("Ошибка запуска программы", "Программа не может продолжить работу." +
                    "\nПричина:\n\n" + e.getMessage());
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
                Path tempFolder = Folders.getInstance().getTempFolder();
                String[] filesList;
                if ((filesList = tempFolder.toFile().list((dir, name) -> name.matches(".*\\.xlsx?$"))) != null
                        && filesList.length > 0) {
                    Utils.openFile(Folders.getInstance().getTempFolder().toFile());

                    if (Dialogs.confirmTS("Удаление временной папки",
                            "Временная папка не пуста.\n\nУдалить временную папку и все файлы внутри неё?\n")) {
                        Utils.deleteFolder(Folders.getInstance().getTempFolder());
                    }
                }
                logger.info("app closed");

                File[] dBfilesList = Folders.APP_FOLDER.toFile().listFiles(pathname -> pathname.getName().endsWith(".db"));
                TreeSet<File> files = new TreeSet<>((o1, o2) -> o2.getName().compareTo(o1.getName()));
                files.addAll(Arrays.asList(dBfilesList));

                File toKeepFile = files.first();
                logger.debug("newest db file " + toKeepFile.getPath() + " will be kept, other - deletes");
                files.forEach(file -> {
                    if (file != toKeepFile) file.delete();
                });

                Configuration conf = ((LoggerContext) LogManager.getContext(false)).getConfiguration();
                conf.getAppenders().get("FILE").stop();
                conf.getAppenders().get("FILE_ERRORS").stop();
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
        try {
            DataBase.getInstance().disconnect();
        } catch (Exception e) {

        }

        MainWindow.getMainTable().close();

        try {
            super.stop();
        } catch (Exception e) {

        }
    }
}
