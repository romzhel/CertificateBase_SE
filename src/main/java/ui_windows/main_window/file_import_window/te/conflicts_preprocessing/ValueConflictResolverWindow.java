package ui_windows.main_window.file_import_window.te.conflicts_preprocessing;

import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.main_window.MainWindow;

import java.util.List;

public class ValueConflictResolverWindow extends OrdinalWindow<ValueConflictResolverWindowController> {
    public ValueConflictResolverWindow(List<ConflictItem> conflictItems) {
        super(
                MainWindow.getMainStage(),
                Modality.APPLICATION_MODAL,
                null,
                "/fxml/valueConflictResolverWindow.fxml",
                "Выбор файлов для импорта"
        );

        controller.init(conflictItems);
    }

    public List<ConflictItem> getResult() throws Exception {
        stage.showAndWait();
        return controller.getResult();
    }
}
