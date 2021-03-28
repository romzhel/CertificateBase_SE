package ui_windows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.options_window.profile_editor.SimpleRight;
import utils.Utils;

import java.io.IOException;

import static ui_windows.options_window.profile_editor.SimpleRight.FULL;

public abstract class OrdinalWindow<T> {
    protected static Stage stage;
    protected static Mode mode;
    protected static AnchorPane rootAnchorPane;
    protected static FXMLLoader loader;
    protected T controller;
    public static final Logger logger = LogManager.getLogger(OrdinalWindow.class);

    public OrdinalWindow(Stage parentStage, Modality modality, Mode mode, String resourceName, String title) {
        logger.trace("загрузка формы '{}'", resourceName);
        this.mode = mode;

        loader = new FXMLLoader(getClass().getResource(resourceName));
        try {
            rootAnchorPane = loader.load();
        } catch (IOException e) {
            logger.error("error fxml file loading", e);
        }

        stage = new Stage();
        stage.setScene(new Scene(rootAnchorPane));

        stage.initOwner(parentStage);
        stage.initModality(modality);
        stage.setTitle(title);
        stage.setResizable(false);

        controller = loader.getController();

        stage.setOnCloseRequest(event -> {
            logger.trace("закрытие формы '{}'", resourceName);
        });
    }

    public static Stage getStage() {
        return stage;
    }

    public static Mode getMode() {
        return mode;
    }

    public static AnchorPane getRootAnchorPane() {
        return rootAnchorPane;
    }

    public static void close() {
        stage.close();
    }

    public static FXMLLoader getLoader() {
        return loader;
    }

    public void applyProfileSimple(SimpleRight sr) {
        boolean editorRights = (sr != FULL);
        Utils.disableEditing(rootAnchorPane, editorRights);
    }
}
