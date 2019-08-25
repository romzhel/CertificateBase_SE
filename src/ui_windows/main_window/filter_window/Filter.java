package ui_windows.main_window.filter_window;

import javafx.scene.layout.AnchorPane;
import ui_windows.options_window.families_editor.ProductFamily;
import utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

public class Filter {
    private ArrayList<FilterSimple> filters;
    private ProductFamily productFamily;
    private String changeCodes[];
    private String changeTexts[];
    private String changeCode = "";

    public Filter() {
        filters = new ArrayList<>();
        addItem(new FilterSimple("Все записи", "cbxAllRecords", "", true));
        addItem(new FilterSimple("Только В Прайсе", "cbxPrice", "price", false));
        addItem(new FilterSimple("Показывать В архиве", "cbxArchive", "archive", false));
        addItem(new FilterSimple("Показывать Не используется", "cbxNotUsed", "notUsed", false));
        addItem(new FilterSimple("Только изменения", "cbxNeedAction", "needAction", false));
        productFamily = null;

        changeCodes = new String[]{"", "new", "dchain", "country", "article", "hierarchy, lgbk", "endofservice", "dangerous"};
        changeTexts = new String[]{"--- Любое ---", "Новая позиция", "Доступность для заказа", "Страна", "Артикул", "Иерархия",
                "Сервисный период", "Ограничения логистики"};
    }

    public void addItem(FilterSimple fs) {
        filters.add(fs);
    }

    public ArrayList<FilterSimple> getItems() {
        return filters;
    }

    public void displayInUI(AnchorPane root) {
        Utils.setControlValue(root, "cbxAllRecords", getFilterSimpleByUIname("cbxAllRecords").isValue());
        Utils.setControlValue(root, "cbxPrice", getFilterSimpleByUIname("cbxPrice").isValue());
        Utils.setControlValue(root, "cbxArchive", getFilterSimpleByUIname("cbxArchive").isValue());
        Utils.setControlValue(root, "cbxNotUsed", getFilterSimpleByUIname("cbxNotUsed").isValue());
        Utils.setControlValue(root, "cbxOnlyChanges", getFilterSimpleByUIname("cbxNeedAction").isValue());
    }

    public FilterSimple getFilterSimpleByUIname(String uiname) {
        for (FilterSimple fs : filters) {
            if (fs.getUiname().equals(uiname)) return  fs;
        }
        return null;
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

    public String getChangeCodeByText(String textCode){
        ArrayList<String> changeTextsList = new ArrayList<>(Arrays.asList(changeTexts));
        int index = changeTextsList.indexOf(textCode);

        return index > 0 ? changeCodes[index] : "";
    }

    public String getChangeText(){
        ArrayList<String> changeCodesList = new ArrayList<>(Arrays.asList(changeCodes));
        int index = changeCodesList.indexOf(changeCode);

        return index > 0 ? changeTexts[index] : "--- Любое ---";
    }

    public String getChangeCode() {
        return changeCode;
    }

    public void setChangeCode(String changeText) {
        changeCode = getChangeCodeByText(changeText);
    }
}
