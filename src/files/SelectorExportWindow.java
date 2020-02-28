package files;

import javafx.stage.Modality;
import javafx.stage.Stage;
import ui_windows.OrdinalWindow;
import ui_windows.product.data.DataItem;

import java.util.ArrayList;

public class SelectorExportWindow extends OrdinalWindow<SelectorExportWindowController> {
    private ArrayList<DataItem> columns;

    public SelectorExportWindow(Stage parentStage) {
        super(parentStage, Modality.APPLICATION_MODAL, null, "selectorExportWindow.fxml", "Выбор столбцов");

        stage.showAndWait();

        columns = controller.getColumnsSelector().getSelectedItems();
    }

    public ArrayList<DataItem> getColumns() {
        return columns;
    }
}
