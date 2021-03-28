package ui_windows.main_window.file_import_window.te;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import ui.Dialogs;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ui_windows.main_window.file_import_window.te.FilesImportParametersEnum.RESET_NON_FOUND_ITEMS_COST;
import static ui_windows.main_window.file_import_window.te.FilesImportParametersEnum.RESET_STATISTIC;

public class FilesSelectionWindowController implements Initializable {
    private FilesImportParameters filesImportParameters = new FilesImportParameters();

    @FXML
    private ListView<File> lvFiles;
    @FXML
    private Button btnUp;
    @FXML
    private Button btnDown;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnRemove;
    @FXML
    private CheckBox chbxResetStatistic;
    @FXML
    private CheckBox chbxResetCostNonfoundItems;
    @FXML
    private Button btnOk;
    @FXML
    private Button btnCancel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initComponents();
    }

    private void initComponents() {
        btnUp.setOnAction(event -> {
            int sourceIndex = lvFiles.getSelectionModel().getSelectedIndex();
            if (sourceIndex <= 0) {
                return;
            }

            changeItemsOrder(sourceIndex, sourceIndex - 1);
        });

        btnDown.setOnAction(event -> {
            int sourceIndex = lvFiles.getSelectionModel().getSelectedIndex();
            if (sourceIndex == lvFiles.getItems().size() - 1) {
                return;
            }

            changeItemsOrder(sourceIndex, sourceIndex + 1);
        });

        btnAdd.setOnAction(event -> {
            List<File> addedFiles = new Dialogs().selectFiles((Stage) btnAdd.getScene().getWindow(),
                    "Добавление файлов для импорта", Dialogs.EXCEL_FILES);
            lvFiles.getItems().addAll(addedFiles);
        });

        btnRemove.setOnAction(event -> {
            File selectedItem = lvFiles.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                lvFiles.getItems().remove(selectedItem);
            }
        });

        btnOk.setOnAction(event -> {
            filesImportParameters.setFiles(lvFiles.getItems());
            filesImportParameters.getParams().put(RESET_STATISTIC, chbxResetStatistic.isSelected());
            filesImportParameters.getParams().put(RESET_NON_FOUND_ITEMS_COST, chbxResetCostNonfoundItems.isSelected());
            ((Stage) btnOk.getScene().getWindow()).close();
        });

        btnCancel.setOnAction(event -> {
            filesImportParameters = null;
            ((Stage) btnCancel.getScene().getWindow()).close();
        });

        chbxResetStatistic.setOnMouseClicked(event -> {
            if (chbxResetStatistic.isSelected() && !Dialogs.confirmTS("Удаление предыдущей статистики",
                    "Действительно желаете удалить предыдущую статистику импорта?")) {
                chbxResetStatistic.setSelected(false);
            }
        });

        chbxResetCostNonfoundItems.setOnMouseClicked(event -> {
            if (chbxResetCostNonfoundItems.isSelected() && !Dialogs.confirmTS("Удаление предыдущей статистики",
                    "Действительно желаете удалить стоимость ненайденных позиций?")) {
                chbxResetCostNonfoundItems.setSelected(false);
            }
        });
    }

    private void changeItemsOrder(int sourceIndex, int targetIndex) {
        Map<Integer, File> sortedHashMap = lvFiles.getItems().stream().collect(Collectors.toMap(file -> lvFiles.getItems().indexOf(file), file -> file));
        File temp = sortedHashMap.get(targetIndex);
        sortedHashMap.put(targetIndex, lvFiles.getSelectionModel().getSelectedItem());
        sortedHashMap.put(sourceIndex, temp);

        lvFiles.getItems().clear();
        lvFiles.getItems().addAll(sortedHashMap.values());
        lvFiles.getSelectionModel().select(targetIndex);
    }

    public FilesImportParameters getFilesImportParameters() {
        return filesImportParameters;
    }
}
