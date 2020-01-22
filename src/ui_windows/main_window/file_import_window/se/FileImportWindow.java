package ui_windows.main_window.file_import_window.se;

import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.main_window.MainWindow;
import ui_windows.main_window.file_import_window.FileImport;

public class FileImportWindow extends OrdinalWindow {

    public FileImportWindow(FileImport fileImport) {
        super(MainWindow.getMainStage(), Modality.APPLICATION_MODAL, null, "fileImportWindow.fxml",
                "Назначение столбцов");

        /*FileImportWindowController controller = loader.getController();
        fileImport.setController(controller);
        controller.setFileImport(fileImport);
*/
        stage.show();
    }
}
