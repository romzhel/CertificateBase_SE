package ui_windows.main_window.file_import_window.te;

import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.main_window.MainWindow;

public class FilesSelectionWindow extends OrdinalWindow<FilesSelectionWindowController> {
    public FilesSelectionWindow() {
        super(MainWindow.getMainStage(), Modality.APPLICATION_MODAL, null, "/fxml/filesSelectionWindow.fxml",
                "Выбор файлов для импорта");
    }

    public FilesImportParameters getDataForImport() {
        stage.showAndWait();
        return controller.getFilesImportParameters();
    }
}
