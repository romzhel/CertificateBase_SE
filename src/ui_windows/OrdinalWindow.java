package ui_windows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui_windows.options_window.profile_editor.SimpleRight;
import utils.Utils;

import java.io.IOException;
import static ui_windows.options_window.profile_editor.SimpleRight.FULL;

public class OrdinalWindow {
    protected static Stage stage;
    protected static Mode mode;
    protected static AnchorPane rootAnchorPane;

    public OrdinalWindow(Stage parentStage, Modality modality, Mode mode, String resourceName, String title){
        this.mode = mode;
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(resourceName));
            rootAnchorPane = (AnchorPane) root;
        } catch (IOException e) {
            System.out.println("error xml file loading " + resourceName + ", " + e.getMessage());
        }

        stage = new Stage();
        stage.setScene(new Scene(root));

        stage.initOwner(parentStage);
        stage.initModality(modality);
        stage.setTitle(title);
        stage.setResizable(false);
    }

    public void applyProfileSimple(SimpleRight sr){
        boolean editorRights = (sr != FULL);
        Utils.disableEditing(rootAnchorPane, editorRights);
    }

    public static Stage getStage(){return stage;}

    public static Mode getMode(){return mode;}

    public static AnchorPane getRootAnchorPane(){return rootAnchorPane;}

    public static void close(){stage.close();}
}
