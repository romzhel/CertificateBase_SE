package ui_windows.request;

import core.CoreModule;
import core.Dialogs;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import ui_windows.main_window.DataSelectorMenu;
import ui_windows.main_window.MainTable;
import ui_windows.main_window.MainWindow;
import ui_windows.product.Product;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;

public class RequestWindowController implements Initializable {
    @FXML
    Button btnApply;

    @FXML
    TextArea taData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnApply.requestFocus();
    }

    public void actionRequest() {
        MainWindow.setProgress(-1);

        new Thread(new Runnable() {
            public void run() {
                ArrayList<Product> results = new ArrayList<>();
                String notFoundItems = "";

                String[] lines = taData.getText().split("\n");
                String[] words;
                Product product;

                for (String line : lines) {
                    words = line.split("\t");

                    for (String word : words) {
                        if (word != null && !word.isEmpty() && !word.matches(".*\\s.*")) {
                            product = CoreModule.getProducts().getItemByMaterialOrArticle(word);

                            if (product == null) {
                                notFoundItems += "- " + line + "\n";
                            } else {
                                results.add(product);
                                break;
                            }
                        }
                    }
                }

                CoreModule.getCustomItems().addAll(results);
//        CoreModule.setCurrentItems(CoreModule.getCustomItems());
        /*if (DataSelectorMenu.MENU_DATA_CUSTOM_SELECTION.isSelected()) {
            MainTable.getTvTable().getItems().clear();
            MainTable.getTvTable().getItems().addAll(CoreModule.getCurrentItems());
        }*/
                MainWindow.getController().getDataSelectorMenu().selectMenuItem(DataSelectorMenu.MENU_DATA_CUSTOM_SELECTION);

                if (!notFoundItems.isEmpty()) {
                    final String notFoundItemsF = notFoundItems;
                    Platform.runLater(() ->
                    Dialogs.showMessage("Создание пользовательского списка", "Следующие позиции не были найдены:\n" + notFoundItemsF));
                }

                MainWindow.setProgress(0);
            }
        }).start();

        ((Stage) btnApply.getScene().getWindow()).close();
    }

    public void actionRequestCertificatesCancel() {
        RequestWindow.close();
    }
}
