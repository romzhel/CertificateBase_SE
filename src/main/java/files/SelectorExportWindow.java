package files;

import javafx.stage.Modality;
import javafx.stage.Stage;
import ui_windows.OrdinalWindow;
import ui_windows.product.data.DataItem;

import java.util.List;

public class SelectorExportWindow extends OrdinalWindow<SelectorExportWindowController> {
    private List<DataItem> columns;

    public SelectorExportWindow(Stage parentStage) {
        super(parentStage, Modality.APPLICATION_MODAL, null, "/fxml/selectorExportWindow.fxml",
                "Выбор столбцов");

        stage.showAndWait();

        columns = controller.getColumnsSelector().getSelectedItems();
    }

    public List<DataItem> getColumns() {
        return columns;
    }
}
