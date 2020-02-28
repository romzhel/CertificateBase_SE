package utils.comparation.prices;

import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.profile_editor.SimpleRight;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SelectPricesForComparisonWindow extends OrdinalWindow<SelectPricesForComparisonWindowController> {
    private ArrayList<File> priceListFiles;

    public SelectPricesForComparisonWindow() {
        super(MainWindow.getMainStage(), Modality.APPLICATION_MODAL, null, "SelectPricesForComparisonWindow.fxml",
                "Выбор прайс-листов для сравнения");

        stage.showAndWait();

        priceListFiles = new ArrayList<>(Arrays.asList(controller.getFile1(), controller.getFile2()));
    }

    public ArrayList<File> getPriceListFiles() {
        return priceListFiles;
    }

    @Override
    public void applyProfileSimple(SimpleRight sr) {
        super.applyProfileSimple(sr);
    }
}
