package utils.requests_handlers;

import core.InitModule;
import javafx.application.Platform;
import ui.Dialogs;
import ui_windows.ExecutionIndicator;
import ui_windows.product.Product;
import ui_windows.product.Products;

import java.util.ArrayList;
import java.util.List;

public class ProductsRequestHandler {
    private static ProductsRequestHandler instance;
    private String notFoundItems;
    private List<Product> results;

    private ProductsRequestHandler() {
        results = new ArrayList<>();
    }

    public static ProductsRequestHandler getInstance() {
        if (instance == null) {
            instance = new ProductsRequestHandler();
        }
        return instance;
    }

    public void findAndShowProductsFromText(String text) {
        notFoundItems = "";
        results.clear();
        ExecutionIndicator.getInstance().start();

        new Thread(() -> {
            String[] lines = text.split("\n");
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

                InitModule.setAndDisplayCustomItems(results);
            });
        }).start();
    }
}
