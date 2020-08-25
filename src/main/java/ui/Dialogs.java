package ui;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Dialogs {
    public static final FileChooser.ExtensionFilter EXCEL_FILES = new FileChooser.ExtensionFilter("Файлы Excel", "*.xls*");
    public static final FileChooser.ExtensionFilter ALL_FILES = new FileChooser.ExtensionFilter("Все файлы", "*.*");
    public static final FileChooser.ExtensionFilter DATABASE_FILES = new FileChooser.ExtensionFilter("База данных", "certificateDB.db");

    public static final int OK = 0;
    public static final int CANCEL = 1;

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

    public List<File> selectAnyFile(Stage stage, String windowTitle, FileChooser.ExtensionFilter fileFilter, String fileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(windowTitle);
        if (fileFilter != null) fileChooser.getExtensionFilters().add(fileFilter);
        if (fileName != null) {
            File temp = new File(fileName);
            if (fileName.contains("\\") && temp.exists()) {
                fileChooser.setInitialDirectory(temp);
            } else {
                fileChooser.setInitialFileName(fileName);
            }
        }

        return fileName == null || fileName.contains("\\") ? fileChooser.showOpenMultipleDialog(stage) :
                Arrays.asList(fileChooser.showSaveDialog(stage));
    }

    public List<File> selectAnyFileTS(Stage stage, String windowTitle, FileChooser.ExtensionFilter fileFilter, String fileName) {
        if (!Thread.currentThread().getName().equals("JavaFX Application Thread")) {
            AtomicReference<List<File>> result = new AtomicReference<>(null);
            CountDownLatch inputWaiting = new CountDownLatch(1);

            Platform.runLater(() -> {
                result.set(selectAnyFile(stage, windowTitle, fileFilter, fileName));
                inputWaiting.countDown();
            });

            try {
                inputWaiting.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return result.get();
        } else {
            return selectAnyFile(stage, windowTitle, fileFilter, fileName);
        }
    }

    public File selectFolder(Stage stage, String title) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);
        File file = directoryChooser.showDialog(stage);

        return file;
    }


    public static void showMessage(String title, String message, double... size) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initModality(Modality.APPLICATION_MODAL);

        if (size.length > 0) alert.getDialogPane().setMinWidth(size[0]);
        if (size.length > 1) alert.getDialogPane().setMinHeight(size[1]);

        alert.showAndWait();
    }

    public static void showMessageTS(String title, String message, double... size) {
        if (!Thread.currentThread().getName().equals("JavaFX Application Thread")) {
            CountDownLatch inputWaiting = new CountDownLatch(1);

            Platform.runLater(() -> {
                showMessage(title, message, size);
                inputWaiting.countDown();
            });

            try {
                inputWaiting.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            showMessage(title, message, size);
        }
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

    public static boolean confirmTS(String title, String message) {
        if (!Thread.currentThread().getName().equals("JavaFX Application Thread")) {
            AtomicBoolean result = new AtomicBoolean(false);
            CountDownLatch inputWaiting = new CountDownLatch(1);

            Platform.runLater(() -> {
                result.set(confirm(title, message));
                inputWaiting.countDown();
            });

            try {
                inputWaiting.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return result.get();
        } else {
            return confirm(title, message);
        }
    }

    public int chooser(String title, String message, String... buttonCaptions) {
        List<ButtonType> buttons = new ArrayList<>();
        if (buttonCaptions.length > 0) {
            for (String buttonCaption : buttonCaptions) {
                buttons.add(new ButtonType(buttonCaption, ButtonBar.ButtonData.OK_DONE));
            }
        } else {
            buttons.add(new ButtonType("ОК", ButtonBar.ButtonData.OK_DONE));
        }
        buttons.add(new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE));

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, buttons.toArray(new ButtonType[]{}));
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.initModality(Modality.APPLICATION_MODAL);

        Optional<ButtonType> option = alert.showAndWait();
        return option.map(buttons::indexOf).orElse(-1);
    }

    public int chooserTS(final String title, final String message, final String... buttonCaptions) {
        AtomicInteger selection = new AtomicInteger(-1);
        if (!Thread.currentThread().getName().equals("JavaFX Application Thread")) {
            CountDownLatch inputWaiting = new CountDownLatch(1);

            Platform.runLater(()-> {
                selection.set(chooser(title, message, buttonCaptions));
                inputWaiting.countDown();
            });

            try {
                inputWaiting.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return selection.get();
        }

        return chooser(title, message, buttonCaptions);
    }
}
