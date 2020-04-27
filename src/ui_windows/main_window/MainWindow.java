package ui_windows.main_window;

import core.AddActions;
import core.CoreModule;
import core.Dialogs;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ui_windows.main_window.filter_window_se.FilterParameters_SE;
import ui_windows.options_window.profile_editor.Profile;
import utils.Utils;

import java.io.IOException;

import static ui_windows.options_window.profile_editor.SimpleRight.HIDE;

public class MainWindow extends Application {
    private static Stage mainStage;
    private static AnchorPane rootAnchorPane;
    private static ProgressBar progressBar;
    private static double progressBarValue;
    private static MenuItem miOptions;
    private static boolean initOk;
    private static FXMLLoader fxmlLoader;
    private static MainWindowsController controller;
    private String version = "1.3.1.4 (beta) от 27.04.2020";


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

    public static synchronized void setProgress(double value) {
        progressBarValue = value;

        Platform.runLater(() -> {
            if (progressBar != null) {
                progressBar.setVisible(progressBarValue != 0);
                progressBar.setProgress(value);
            }
        });
    }

    public static ProgressBar getProgressBar() {
        return progressBar;
    }

    public static void setProgressBar(ProgressBar progressBar) {
        MainWindow.progressBar = progressBar;
    }

    public static synchronized double getProgressBarValue() {
        return progressBarValue;
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

        initOk = CoreModule.init();

        if (initOk) {
            fxmlLoader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));
            try {
                rootAnchorPane = fxmlLoader.load();
            } catch (IOException e) {
                System.out.println("error xml file loading: " + e.getMessage());
            }

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

            final String searchBoxCss = getClass().getResource("/utils/SearchBox.css").toExternalForm();
            rootAnchorPane.getStylesheets().add(searchBoxCss);

            applyProfile(CoreModule.getUsers().getCurrentUser().getProfile());

            mainStage.show();
            mainStage.setMinHeight(mainStage.getHeight());
            mainStage.setMinWidth(mainStage.getWidth());

            new AddActions().make();

//        OptionsWindow certificateOverviewWindow = new OptionsWindow();
//        certificateOverviewWindow.open();
        } else Platform.exit();

        mainStage.setOnCloseRequest(event -> {
            try {
                if (CoreModule.getFolders().getTempFolder().exists())
                    Utils.deleteFolder(CoreModule.getFolders().getTempFolder().toPath());
            } catch (IOException ioe) {
                System.out.printf("deleting error " + ioe.getMessage());
            }

            if (progressBarValue > 0) {
                event.consume();
                Dialogs.showMessage("Закрытие программы", "Активен процесс записи в базу " +
                        "данных. Программа закроется автоматически после его завершения.");

                new Thread(() -> {
                    try {
                        while (progressBarValue != 0.0) {
                            Thread.currentThread().sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }

                    Platform.exit();
                }).start();
            }
        });
    }

    @Override
    public void stop() throws Exception {
        if (initOk) CoreModule.getDataBase().disconnect();

        MainWindow.getMainTable().close();
        super.stop();
    }
}
