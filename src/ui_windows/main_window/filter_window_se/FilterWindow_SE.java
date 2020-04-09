package ui_windows.main_window.filter_window_se;


import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.main_window.MainWindow;

public class FilterWindow_SE extends OrdinalWindow<FilterWindowController_SE> {

    public FilterWindow_SE() {
        super(MainWindow.getMainStage(), Modality.NONE,
                null, "filterWindow_SE.fxml", "Настройки фильтра отображения");

        stage.show();
    }
}
