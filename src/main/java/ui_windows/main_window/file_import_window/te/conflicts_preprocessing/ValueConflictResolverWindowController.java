package ui_windows.main_window.file_import_window.te.conflicts_preprocessing;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui.Dialogs;

import java.util.List;

@Getter
public class ValueConflictResolverWindowController {
    private static final Logger logger = LogManager.getLogger(ValueConflictResolverWindowController.class);
    private List<ConflictItem> result;

    @FXML
    private TreeView<Object> tvConflictItems;
    @FXML
    private Button btnOk;
    @FXML
    private Button btnCancel;

    public void init(List<ConflictItem> conflictItems) {
        result = conflictItems;
        ConflictItemsTable.init(tvConflictItems, conflictItems);
        btnOk.setOnAction(event -> apply());
        btnCancel.setOnAction(event -> cancel());
    }

    public void apply() {
        if (!isSelectionCorrect()) {
            Dialogs.showMessage("Разрешение конфликтов значений", "Пожалуйста, перепроверьте, должно быть " +
                    "выбрано одно значение для каждого свойства");
            return;
        }

        tvConflictItems.getScene().getWindow().hide();
    }

    public void cancel() {
        result = null;
        tvConflictItems.getScene().getWindow().hide();
    }

    private boolean isSelectionCorrect() {
        for (TreeItem<Object> productItem : tvConflictItems.getRoot().getChildren()) {
            for (TreeItem<Object> propertyItem : productItem.getChildren()) {
                int selectedCount = (int) propertyItem.getChildren().stream()
                        .map(treeItem -> ((ConflictProperty) treeItem.getValue()).isSelected())
                        .filter(isSelected -> isSelected)
                        .count();
                if (selectedCount != 1) {
                    return false;
                }
            }
        }
        return true;
    }
}
