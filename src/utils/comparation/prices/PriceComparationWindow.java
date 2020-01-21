package utils.comparation.prices;

import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.profile_editor.SimpleRight;

public class PriceComparationWindow extends OrdinalWindow {
    public PriceComparationWindow() {
        super(MainWindow.getMainStage(), Modality.APPLICATION_MODAL, null, "priceComparationWindow.fxml",
                "Выбор прайс-листов для сравнения");

        stage.show();
    }

    @Override
    public void applyProfileSimple(SimpleRight sr) {
        super.applyProfileSimple(sr);
    }
}
