package ui_windows.main_window.file_import_window.te;

import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.main_window.MainWindow;

import java.util.List;
import java.util.Map;

public class ColumnMappingWindow extends OrdinalWindow<ColumnMappingWindowController> {

    public ColumnMappingWindow(String fileName, Map<String, List<ImportColumnParameter>> importParameters) {
        super(MainWindow.getMainStage(), Modality.APPLICATION_MODAL, null, "/fxml/columnMappingWindow.fxml",
                fileName);

        controller.init(importParameters);
    }

    public Map<String, List<ImportColumnParameter>> getParameters() throws Exception {
        stage.showAndWait();
        return controller.getParameters();
    }
}
