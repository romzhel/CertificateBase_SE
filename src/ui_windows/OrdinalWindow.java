package ui_windows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import org.apache.poi.ss.formula.functions.T;
import ui_windows.options_window.profile_editor.SimpleRight;
import utils.Utils;

import java.io.IOException;

import static ui_windows.options_window.profile_editor.SimpleRight.FULL;

public class OrdinalWindow {
    protected static Stage stage;
    protected static Mode mode;
    protected static AnchorPane rootAnchorPane;
    protected static FXMLLoader loader;
    protected static T controller;

    public OrdinalWindow(Stage parentStage, Modality modality, Mode mode, String resourceName, String title, T... controller) {
        this.mode = mode;
        loader = new FXMLLoader(getClass().getResource(resourceName));
        try {
            rootAnchorPane = loader.load();
        } catch (IOException e) {
            System.out.println("error xml file loading " + resourceName + ", " + e.getMessage());
        }

        stage = new Stage();
        stage.setScene(new Scene(rootAnchorPane));

        stage.initOwner(parentStage);
        stage.initModality(modality);
        stage.setTitle(title);
        stage.setResizable(false);
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

    public void applyProfileSimple(SimpleRight sr) {
        boolean editorRights = (sr != FULL);
        Utils.disableEditing(rootAnchorPane, editorRights);
    }

    public static FXMLLoader getLoader() {
        return loader;
    }
}
