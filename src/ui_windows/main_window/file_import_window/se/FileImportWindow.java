package ui_windows.main_window.file_import_window.se;

import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.main_window.MainWindow;

public class FileImportWindow extends OrdinalWindow {

    public FileImportWindow(FileImport fileImport) {
        super(MainWindow.getMainStage(), Modality.APPLICATION_MODAL, null, "fileImportWindow.fxml",
                "Назначение столбцов");

        FileImportWindowController controller = loader.getController();
        controller.init(fileImport);
        stage.show();
    }
}
