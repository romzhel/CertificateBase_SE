package ui_windows.main_window;

import core.AddActions;
import core.CoreModule;
import core.Dialogs;
import database.DataBase;
import files.Folders;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ui_windows.ExecutionIndicator;
import ui_windows.main_window.filter_window_se.FilterParameters_SE;
import ui_windows.options_window.profile_editor.Profile;
import ui_windows.options_window.user_editor.Users;
import utils.Utils;

import java.io.File;

import static ui_windows.options_window.profile_editor.SimpleRight.HIDE;

public class MainWindow extends Application {
    private static Stage mainStage;
    private static AnchorPane rootAnchorPane;
    private static MenuItem miOptions;
    private static FXMLLoader fxmlLoader;
    private static MainWindowsController controller;
    private String version = "1.3.2.0 (beta) от 11.05.2020";

    public static void main(String[] args) {
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

    public static FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }

    public static MainWindowsController getController() {
        return controller;
    }

    public static MainTable getMainTable() {
        return controller.getMainTable();
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

            new AddActions().make();

//        OptionsWindow certificateOverviewWindow = new OptionsWindow();
//        certificateOverviewWindow.open();
        } catch (Exception e) {
            e.printStackTrace();
            Dialogs.showMessage("Ошибка инициализация программы", "Программа не может продолжить работу." +
                    "\nПричина: " + e.getMessage());
            Platform.exit();
        }

        mainStage.setOnCloseRequest(event -> {
            if (ExecutionIndicator.getInstance().hasActiveProcess()) {
                event.consume();
                Dialogs.showMessage("Закрытие программы", "Не все процессы завершены. " +
                        "Повторите попытку чуть позже.");
            } else {
                try {
                    File tempFolder = Folders.getInstance().getTempFolder();
                    String[] filesList;
                    if (tempFolder.exists() && (filesList = tempFolder.list((dir, name) -> name.matches(".*\\.xlsx?$"))) != null
                            && filesList.length > 0) {
                        Utils.openFile(Folders.getInstance().getTempFolder());

                        if (Dialogs.confirmTS("Удаление временной папки",
                                "Временная папка не пуста.\n\nУдалить временную папку и все файлы внутри неё?\n")) {
                            Utils.deleteFolder(Folders.getInstance().getTempFolder().toPath());
                        }
                    }
                } catch (Exception e) {
                    System.out.printf(e.getMessage());
                }
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
