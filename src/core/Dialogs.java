package core;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class Dialogs {
    public static final FileChooser.ExtensionFilter EXCEL_FILES = new FileChooser.ExtensionFilter("Файлы Excel", "*.xls*");
    public static final FileChooser.ExtensionFilter ALL_FILES = new FileChooser.ExtensionFilter("Все файлы", "*.*");
    public static final FileChooser.ExtensionFilter DATABASE_FILES = new FileChooser.ExtensionFilter("База данных", "certificateDB.db");

    public static String textInput(String title, String text, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(text);
        dialog.getEditor().setPrefWidth(250);
        dialog.getEditor().setText(defaultValue);

// Traditional way to getItems the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) return result.get();
        else return null;
    }

    public static File selectFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Все файлы", "*.*");//Расширение
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(stage);//Указываем текущую сцену

        return file;
    }

    public static File selectDBFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл базы данных");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("База данных",
                "certificateDB.db");//Расширение
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(stage);//Указываем текущую сцену

        return file;
    }

    public static File selectNOWFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Файлы Excel",
                "*.xls*");
        fileChooser.getExtensionFilters().addAll(filter);
        File file = fileChooser.showOpenDialog(stage);//Указываем текущую сцену

        return file;
    }

    public File selectAnyFile(Stage stage, String windowTitle, FileChooser.ExtensionFilter fileFilter, String fileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(windowTitle);
        if (fileFilter != null) fileChooser.getExtensionFilters().add(fileFilter);
        if (fileName != null) {
            if (fileName.contains("\\")) {
                fileChooser.setInitialDirectory(new File(fileName));
            } else {
                fileChooser.setInitialFileName(fileName);
            }
        }

        return fileName == null || fileName.contains("\\") ? fileChooser.showOpenDialog(stage) : fileChooser.showSaveDialog(stage);
    }

    public File selectFolder(Stage stage, String title) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);
        File file = directoryChooser.showDialog(stage);

        return file;
    }


    public static void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initModality(Modality.APPLICATION_MODAL);

        alert.showAndWait();
    }

    public static boolean confirm(String title, String message) {
        ButtonType cancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, cancel);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.initModality(Modality.APPLICATION_MODAL);

        Optional<ButtonType> option = alert.showAndWait();

        return option.get() == ButtonType.OK;
    }
}
