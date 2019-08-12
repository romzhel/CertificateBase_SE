package core;

import javafx.collections.ListChangeListener;
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
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Выгрузка NOW",
                "ORDERSTAT*.xls*", "Product*.xls*");//Расширение
        FileChooser.ExtensionFilter filter2 = new FileChooser.ExtensionFilter("Файлы Excel",
                "*.xls*");
        fileChooser.getExtensionFilters().addAll(filter, filter2);
        File file = fileChooser.showOpenDialog(stage);//Указываем текущую сцену

        return file;
    }

    public static String selectFolder(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите папку");
        File file = directoryChooser.showDialog(stage) ;//Указываем текущую сцену

        if (file == null) return null;

        String parts[] = file.getAbsolutePath().split("\\\\");

        return parts[parts.length - 1];
    }

    public static void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initModality(Modality.APPLICATION_MODAL);

        alert.showAndWait();
    }

    public static boolean confirm(String title, String message){
        ButtonType cancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, cancel);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.initModality(Modality.APPLICATION_MODAL);

        Optional<ButtonType> option = alert.showAndWait();

      return option.get() == ButtonType.OK;
    }
}
