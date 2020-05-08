package ui_windows.request;

import core.CoreModule;
import core.Dialogs;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import ui_windows.ExecutionIndicator;
import ui_windows.product.Product;
import ui_windows.product.Products;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RequestWindowController implements Initializable {
    String notFoundItems = "";
    ArrayList<Product> results = new ArrayList<>();

    @FXML
    Button btnApply;

    @FXML
    TextArea taData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnApply.requestFocus();
    }

    public void actionRequest() {
        notFoundItems = "";
        results.clear();
        ExecutionIndicator.getInstance().start();

        new Thread(() -> {
            String[] lines = taData.getText().split("\n");
            String[] words;
            Product product;
            int position = 0;

            boolean lineFound;
            for (String line : lines) {
                lineFound = false;
                words = line.split("[\t\\s\\:]");

                for (String word : words) {
                    if (word != null && !word.isEmpty() && !word.matches(".*\\s.*")) {
                        product = Products.getInstance().getItemByMaterialOrArticle(word);

                        if (product == null) {
//                            notFoundItems += "- " + line + "\n";
//                            taData.setStyle("-fx-highlight-fill: lightgray; -fx-highlight-text-fill: firebrick; -fx-font-size: 12px;");
//                            taData.selectRange(position, position + word.length() + 1);
//                            position += word.length() + 1;
                        } else {
                            results.add(product);
                            position += line.length() + 1;
                            lineFound = true;
                            break;
                        }
                    }
                }

                if (!lineFound) {
                    notFoundItems += "- " + line + "\n";
//                    taData.setStyle("-fx-highlight-fill: lightgray; -fx-highlight-text-fill: firebrick; -fx-font-size: 12px;");
//                    taData.selectRange(position, position + word.length() + 1);
                }
            }

            ExecutionIndicator.getInstance().stop();

            Platform.runLater(() -> {
                if (!notFoundItems.isEmpty()) {
                    if (!Dialogs.confirm("Создание пользовательского списка", "Следующие позиции не были найдены:\n" +
                            notFoundItems + "\nЖелаете продолжить?")) {
                        return;
                    }
                }

                /*CoreModule.getCustomItems().addAll(results);
                MainWindow.getController().getDataSelectorMenu().selectMenuItem(DataSelectorMenu.MENU_DATA_CUSTOM_SELECTION);*/

                CoreModule.setAndDisplayCustomItems(results);

                ((Stage) btnApply.getScene().getWindow()).close();
            });
        }).start();
    }

    public void actionRequestCertificatesCancel() {
        RequestWindow.close();
    }
}
