package ui_windows.main_window.file_import_window.te;

import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.main_window.MainWindow;
import ui_windows.main_window.file_import_window.te.importer.ImportDataSheet;

public class ColumnMappingWindow extends OrdinalWindow<ColumnMappingWindowController> {

    public ColumnMappingWindow(ImportDataSheet importDataSheet) {
        super(MainWindow.getMainStage(), Modality.APPLICATION_MODAL, null, "/fxml/columnMappingWindow.fxml",
                "Сопоставление столбцов");

        controller.init(importDataSheet);
    }

    public ImportDataSheet getResult() throws Exception {
        stage.showAndWait();
        return controller.getResult();
    }
}
