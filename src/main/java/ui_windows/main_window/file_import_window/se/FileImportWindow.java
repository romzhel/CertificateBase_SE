package ui_windows.main_window.file_import_window.se;

import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.main_window.MainWindow;
import ui_windows.main_window.file_import_window.FileImportParameter;

import java.util.List;

public class FileImportWindow extends OrdinalWindow<FileImportWindowController> {

    public FileImportWindow(FileImport fileImport) {
        super(MainWindow.getMainStage(), Modality.APPLICATION_MODAL, null, "/fxml/fileImportWindow.fxml",
                "Назначение столбцов");

        controller = loader.getController();
        controller.init(fileImport);
    }

    public List<FileImportParameter> getParameters() {
        stage.showAndWait();
        return controller.getParameters();
    }
}
