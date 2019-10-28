package ui_windows.main_window.filter_window;

import core.CoreModule;
import javafx.application.Platform;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.LgbkAndParent;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.Product;
import utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import static ui_windows.main_window.filter_window.FilterParameters.*;

public class Filter {
    public static final String FILTER_VALUE_ALL_ITEMS = "--- Все ---";
    public static final ProductFamily FILTER_VALUE_ALL_FAMILIES = new ProductFamily(FILTER_VALUE_ALL_ITEMS);
    public static final ProductLgbk FILTER_VALUE_ALL_LGBKS = new ProductLgbk(FILTER_VALUE_ALL_ITEMS);
    public static final FilterParameter FILTER_ALL_ITEMS = new FilterParameter(true);
    public static final FilterParameter FILTER_PRICE_ITEMS = new FilterParameter(false);
    public static final FilterParameter FILTER_FAMILY = new FilterParameter(FILTER_VALUE_ALL_FAMILIES);
    public static final FilterParameter FILTER_LGBK = new FilterParameter(FILTER_VALUE_ALL_LGBKS);

    public static final String ALL_RECORDS = "--- Все ---";
    private ProductFamily productFamily = FILTER_VALUE_ALL_FAMILIES;
    private ProductLgbk lgbk = FILTER_VALUE_ALL_LGBKS;
    private String changeCodes[];
    private String changeTexts[];
    private String changeCode = "";
    private FilterWindowController controller;

    public Filter() {
        lgbk = FILTER_VALUE_ALL_LGBKS;

        changeCodes = new String[]{"", "new", "dchain", "country", "article", "hierarchy, lgbk", "endofservice", "dangerous"};
        changeTexts = new String[]{"--- Любое ---", "Новая позиция", "Доступность для заказа", "Страна", "Артикул", "Иерархия",
                "Сервисный период", "Ограничения логистики"};
    }

    public void displayInUI(FilterWindowController controller) {
        FILTER_ALL_ITEMS.displayValue(controller.cbxAllRecords);
        FILTER_PRICE_ITEMS.displayValue(controller.cbxPrice);
        FILTER_FAMILY.displayValue(controller.cbFamily);
        FILTER_LGBK.displayValue(controller.cbLgbk);
    }

    public void setProductFamily(ProductFamily productFamily) {
        this.productFamily = productFamily;
    }

    public ProductFamily getProductFamily() {
        return productFamily;
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
        String find = MainWindow.getSearchBox().getText();
        find = find.replaceAll("\\*", ".*");
        find = find.replaceAll("\\.", ".");

        ArrayList<Product> result = new ArrayList<>();
        TreeSet<ProductLgbk> accessibleLgbks = new TreeSet<>((o1, o2) -> o1.getLgbk().compareTo(o2.getLgbk()));

        boolean articleMatch = false;
        boolean materialMatch = false;
        boolean descriptionMatch = false;
        boolean filterMatch = false;
        LgbkAndParent lgbkAndParent;
        ProductFamily pf = null;
        boolean familyMatch = false;
        boolean lgbkMatch = false;

        for (Product p : CoreModule.getCurrentItems()) {
            articleMatch = p.getArticle().toUpperCase().matches("^(" + find.toUpperCase() + ").*");
            materialMatch = p.getMaterial().toUpperCase().matches("^(" + find.toUpperCase() + ").*");
            descriptionMatch = p.getDescriptionru().toLowerCase().contains(find.toLowerCase()) /*||
                p.getDescriptionen().toLowerCase().matches(".*(" + find.toLowerCase() + ").*")*/;
            filterMatch = p.matchFilter(CoreModule.getFilter());

            if (productFamily.equals(FILTER_VALUE_ALL_FAMILIES) || productFamily == null) {
                familyMatch = true;
            } else if (productFamily != null) {
                if (p.getFamily() > 0) {
                    pf = CoreModule.getProductFamilies().getFamilyById(p.getFamily());
                } else {
                    lgbkAndParent = CoreModule.getProductLgbkGroups().getLgbkAndParent(new ProductLgbk(p));
                    if (lgbkAndParent.getLgbkItem() != null && lgbkAndParent.getLgbkItem().getFamilyId() > 0) {
                        pf = CoreModule.getProductFamilies().getFamilyById(lgbkAndParent.getLgbkItem().getFamilyId());
                    } else if (lgbkAndParent.getLgbkParent() != null) {
                        pf = CoreModule.getProductFamilies().getFamilyById(lgbkAndParent.getLgbkParent().getFamilyId());
                    }
                }

                familyMatch = pf != null && pf.equals(CoreModule.getFilter().getProductFamily());
            }

            if (lgbk.equals(FILTER_VALUE_ALL_LGBKS) || lgbk == null) {
                lgbkMatch = true;
            } else if (lgbk != null) {
                lgbkMatch = lgbk.getLgbk().equals(p.getLgbk());
            }

            if (familyMatch && lgbkMatch && (filterMatch && (articleMatch || materialMatch || descriptionMatch))) {
                result.add(p);
                accessibleLgbks.add(CoreModule.getProductLgbks().getByLgbkName(p.getLgbk()));
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

    public ProductLgbk getLgbk() {
        return lgbk;
    }

    public void setLgbk(ProductLgbk lgbk) {
        this.lgbk = lgbk;
    }

    public interface TableRenewedListener {
        void getLgbksForItems(TreeSet<ProductLgbk> lgbks);

    }

    public void setController(FilterWindowController controller) {
        this.controller = controller;
    }
}
