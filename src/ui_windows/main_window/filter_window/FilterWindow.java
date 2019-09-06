package ui_windows.main_window.filter_window;


import core.CoreModule;
import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.main_window.MainWindow;

public class FilterWindow extends OrdinalWindow {

    public FilterWindow() {
        super(MainWindow.getMainStage(), Modality.NONE,
                null, "filterWindow.fxml", "Настройки фильтра отображения");

        CoreModule.getFilter().displayInUI(rootAnchorPane);

        stage.show();
    }
}
