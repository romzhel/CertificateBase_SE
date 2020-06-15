package ui_windows.main_window.filter_window_se;


import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.main_window.MainWindow;

public class FilterWindow_SE extends OrdinalWindow<FilterWindowController_SE> {
    private static boolean isOpened;

    private FilterWindow_SE() {
        super(MainWindow.getMainStage(), Modality.NONE,
                null, "/fxml/filterWindow_SE.fxml", "Настройки фильтра отображения");

        stage.setOnCloseRequest(event -> isOpened = false);

        stage.show();
    }

    public static void openFilterWindow() {
        if (!isOpened) {
            isOpened = true;
            new FilterWindow_SE();
        }
    }
}
