package preloader;

import core.App;
import javafx.application.Preloader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppPreloader extends Preloader {
    private Stage primaryStage;
    private static final Logger logger = LogManager.getLogger(AppPreloader.class);

    @FXML
    private Label lblVersion;
    @FXML
    private Label lblStatus;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        logger.trace("Preloader start");

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/preloaderScreen.fxml"));
            fxmlLoader.setController(this);
            Pane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            lblVersion.setText("Версия " + App.getProperties().getVersionWithDate());
            primaryStage.show();
            logger.trace("preloader is shown");
        } catch (Exception e) {
            logger.error("Preloader error : {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        if (info.getType() == StateChangeNotification.Type.BEFORE_START) {
            primaryStage.hide();
        }
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        lblStatus.setText(info.toString());
    }
}
