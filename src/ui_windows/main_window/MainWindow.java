package ui_windows.main_window;

import core.CoreModule;
import core.Dialogs;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ui_windows.main_window.filter_window.FilterParameters;
import ui_windows.options_window.profile_editor.Profile;
import utils.SearchBox;
import utils.Utils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ui_windows.options_window.profile_editor.SimpleRight.HIDE;

public class MainWindow extends Application {
    private String version = "1.1.0.8 от 20.12.2019";
    private static Stage mainStage;
    private static AnchorPane rootAnchorPane;
    private static ProgressBar progressBar;
    private static double progressBarValue;

    private static MenuItem miOptions;
    private static boolean initOk;
    private static FXMLLoader fxmlLoader;
    private static MainWindowsController controller;

    private static SearchBox searchBox;

    private ExecutorService executorService;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;

//        WaitingWindow ww = new WaitingWindow(null);
        initOk = CoreModule.init();
//        WaitingWindow.close();

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

            searchBox = new SearchBox();
            searchBox.setLayoutX(158);
            searchBox.setLayoutY(601);
            rootAnchorPane.getChildren().add(searchBox);
            rootAnchorPane.setBottomAnchor(searchBox, 39.0);
            rootAnchorPane.setLeftAnchor(searchBox, 158.0);

//            mainTable = new MainTable(controller.tvTable);

            executorService = Executors.newFixedThreadPool(5);
            searchBox.getTextBox().textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    FilterParameters.FILTER_SEARCH_BOX.setValue(newValue);
                    Runnable filterAction = () -> CoreModule.getFilter().apply();
                    executorService.execute(filterAction);
                }
            });

            final String searchBoxCss = getClass().getResource("/utils/SearchBox.css").toExternalForm();
            rootAnchorPane.getStylesheets().add(searchBoxCss);

            applyProfile(CoreModule.getUsers().getCurrentUser().getProfile());

            CoreModule.getFilter().apply();
            searchBox.getTextBox().requestFocus();

            mainStage.show();
            mainStage.setMinHeight(mainStage.getHeight());
            mainStage.setMinWidth(mainStage.getWidth());

//            new AddActions().make();

//        OptionsWindow certificateOverviewWindow = new OptionsWindow();
//        certificateOverviewWindow.open();
        } else Platform.exit();

        mainStage.setOnCloseRequest(event -> {
            executorService.shutdown();
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
                        while (progressBarValue > 0) {
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
                progressBar.setVisible(progressBarValue == 0 ? false : true);
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

    public static SearchBox getSearchBox() {
        return searchBox;
    }

    public static FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }

    public static MainWindowsController getController() {
        return controller;
    }

    public static MainTable getMainTable() {
        return controller.mainTable;
    }
}
