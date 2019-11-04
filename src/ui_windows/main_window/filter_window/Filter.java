package ui_windows.main_window.filter_window;

import core.CoreModule;
import javafx.application.Platform;
import javafx.scene.control.TableView;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.Product;
import utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import static ui_windows.main_window.filter_window.FilterParameters.*;

public class Filter {
    private String changeCodes[];
    private String changeTexts[];
    private String changeCode = "";
    private FilterWindowController controller;

    public Filter() {
        changeCodes = new String[]{"", "new", "dchain", "country", "article", "hierarchy, lgbk", "endofservice", "dangerous"};
        changeTexts = new String[]{"--- Любое ---", "Новая позиция", "Доступность для заказа", "Страна", "Артикул", "Иерархия",
                "Сервисный период", "Ограничения логистики"};
    }

    public void displayInUI() {
        if (controller != null) {
            FILTER_ALL_ITEMS.displayValue(controller.rbAllItems);
            FILTER_PRICE_ITEMS.displayValue(controller.rbPriceItems);
            FILTER_FAMILY.displayValue(controller.cbFamily);
            FILTER_LGBK.displayValue(controller.cbLgbk);
        }
        if (MainWindow.getSearchBox() != null) {
            FILTER_SEARCH_BOX.displayValue(MainWindow.getSearchBox().getTextBox());
        }
    }

    public void switchFilterParameters(FilterParameters oldFp, FilterParameters newFp) {
        if (oldFp != null) oldFp.save();
        if (newFp != null) newFp.load();
        displayInUI();
        apply();
    }

    public ArrayList<String> getChangeTexts() {
        return new ArrayList<>(Arrays.asList(changeTexts));
    }

    public String getChangeCodeByText(String textCode) {
        ArrayList<String> changeTextsList = new ArrayList<>(Arrays.asList(changeTexts));
        int index = changeTextsList.indexOf(textCode);

        return index > 0 ? changeCodes[index] : "";
    }

    public String getChangeText() {
        ArrayList<String> changeCodesList = new ArrayList<>(Arrays.asList(changeCodes));
        int index = changeCodesList.indexOf(changeCode);

        return index > 0 ? changeTexts[index] : "--- Любое ---";
    }

    public void apply() {
        TableView<Product> tableView = CoreModule.getProducts().getTableView();
        String find = FILTER_SEARCH_BOX.getValue();
        find = find.replaceAll("\\*", ".*");
        find = find.replaceAll("\\.", ".");

        ArrayList<Product> result = new ArrayList<>();
//        TreeSet<ProductLgbk> accessibleLgbks = new TreeSet<>((o1, o2) -> o1.getLgbk().compareTo(o2.getLgbk()));

        boolean articleMatch = false;
        boolean materialMatch = false;
        boolean descriptionMatch = false;
        boolean filterMatch = false;
        boolean familyMatch = false;
        boolean lgbkMatch = false;

        for (Product p : CoreModule.getCurrentItems()) {
            articleMatch = p.getArticle().toUpperCase().matches("^(" + find.toUpperCase() + ").*");
            materialMatch = p.getMaterial().toUpperCase().matches("^(" + find.toUpperCase() + ").*");
            descriptionMatch = p.getDescriptionru().toLowerCase().contains(find.toLowerCase()) /*||
                p.getDescriptionen().toLowerCase().matches(".*(" + find.toLowerCase() + ").*")*/;
            filterMatch = p.matchFilter(CoreModule.getFilter());

            ProductFamily productFamily = (ProductFamily) FILTER_FAMILY.getValue();
            if (productFamily.equals(FILTER_VALUE_ALL_FAMILIES) || productFamily == null) {
                familyMatch = true;
            } else {
                ProductFamily pf = p.getProductFamily();
                familyMatch = pf != null && pf.equals(productFamily);
            }

            ProductLgbk productLgbk = (ProductLgbk) FILTER_LGBK.getValue();
            if (productLgbk.equals(FILTER_VALUE_ALL_LGBKS) || productLgbk == null) {
                lgbkMatch = true;
            } else if (productLgbk != null) {
                lgbkMatch = productLgbk.getLgbk().equals(p.getLgbk());
            }

            if (familyMatch && lgbkMatch && (filterMatch && (articleMatch || materialMatch || descriptionMatch))) {
                result.add(p);
//                accessibleLgbks.add(CoreModule.getProductLgbks().getByLgbkName(p.getLgbk()));
            }
        }

        Platform.runLater(() -> {
            tableView.getItems().clear();
            tableView.getItems().addAll(result);
            tableView.sort();
            Utils.setControlValue(MainWindow.getRootAnchorPane(), "lbRecordCount", Integer.toString(tableView.getItems().size()));
            tableView.refresh();
        });
    }

    public String getChangeCode() {
        return changeCode;
    }

    public void setChangeCode(String changeText) {
        changeCode = getChangeCodeByText(changeText);
    }

    public void setController(FilterWindowController controller) {
        this.controller = controller;
        displayInUI();
    }

    public interface TableRenewedListener {
        void getLgbksForItems(TreeSet<ProductLgbk> lgbks);

    }
}
