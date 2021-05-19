package ui_windows.main_window.file_import_window.te.conflicts_preprocessing;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui.Dialogs;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.product.data.DataItem;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        ConflictItemsTable.init(tvConflictItems, conflictItems);
        btnOk.setOnAction(event -> apply());
        btnCancel.setOnAction(event -> cancel());
    }

    public void apply() {
        if (!isSelectionCorrect()) {
            Dialogs.showMessage("Разрешение конфликтов значений", "Пожалуйста, перепроверьте, должно быть " +
                    "выбрано одно значение для свойства");
            return;
        }
        result = generateResult();

        tvConflictItems.getScene().getWindow().hide();
    }

    public void cancel() {
        tvConflictItems.getScene().getWindow().hide();
    }

    private boolean isSelectionCorrect() {
        for (TreeItem<Object> productItem : tvConflictItems.getRoot().getChildren()) {
            for (TreeItem<Object> propertyItem : productItem.getChildren()) {
                int selectedCount = (int) propertyItem.getChildren().stream()
                        .map(treeItem -> ((ConflictItemValue) treeItem.getValue()).isSelected())
                        .filter(isSelected -> isSelected)
                        .count();
                if (selectedCount != 1) {
                    return false;
                }
            }
        }
        return true;
    }

    private List<ConflictItem> generateResult() {
        return tvConflictItems.getRoot().getChildren().stream()
                .map(productTreeItem -> {
                    ConflictItem conflictItem = new ConflictItem();
                    conflictItem.setCalculatedItem((ImportedProduct) productTreeItem.getValue());
                    conflictItem.setConflictValues(productTreeItem.getChildren().stream()
                            .collect(Collectors.toMap(
                                    propertyTreeItem -> (DataItem) propertyTreeItem.getValue(),
                                    propertyTreeItem -> Collections.singletonList(
                                            propertyTreeItem.getChildren().stream()
                                                    .map(valueTreeItem -> (ConflictItemValue) valueTreeItem.getValue())
                                                    .filter(ConflictItemValue::isSelected)
                                                    .findFirst().get()
                                    )
                            ))
                    );
                    return conflictItem;
                })
                .collect(Collectors.toList());
    }
}
