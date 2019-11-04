package ui_windows.main_window.filter_window;

import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;

public class FilterParameters {
    public static final String FILTER_VALUE_ALL_ITEMS = "--- Все ---";
    public static final ProductFamily FILTER_VALUE_ALL_FAMILIES = new ProductFamily(FILTER_VALUE_ALL_ITEMS);
    public static final ProductLgbk FILTER_VALUE_ALL_LGBKS = new ProductLgbk(FILTER_VALUE_ALL_ITEMS);
    public static final FilterParameter<Boolean> FILTER_ALL_ITEMS = new FilterParameter<>(false);
    public static final FilterParameter<Boolean> FILTER_PRICE_ITEMS = new FilterParameter<>(true);
    public static final FilterParameter<ProductFamily> FILTER_FAMILY = new FilterParameter<>(FILTER_VALUE_ALL_FAMILIES);
    public static final FilterParameter<ProductLgbk> FILTER_LGBK = new FilterParameter<>(FILTER_VALUE_ALL_LGBKS);
    public static final FilterParameter<String> FILTER_SEARCH_BOX = new FilterParameter<>("");

    private ProductFamily productFamily;
    private ProductLgbk productLgbk;
    private boolean allItems;
    private boolean priceItems;
    private String searchBoxText;

    public FilterParameters() {
        productFamily = FILTER_VALUE_ALL_FAMILIES;
        productLgbk = FILTER_VALUE_ALL_LGBKS;
        allItems = true;
        priceItems = false;
        searchBoxText = "";
    }

    public void save() {
        productFamily = FILTER_FAMILY.getValue();
        productLgbk = FILTER_LGBK.getValue();
        allItems = FILTER_ALL_ITEMS.getValue();
        priceItems = FILTER_PRICE_ITEMS.getValue();
        searchBoxText = FILTER_SEARCH_BOX.getValue();
    }

    public void load() {
        FILTER_FAMILY.setValue(this.productFamily);
        FILTER_LGBK.setValue(this.productLgbk);
        FILTER_ALL_ITEMS.setValue(this.allItems);
        FILTER_PRICE_ITEMS.setValue(this.priceItems);
        FILTER_SEARCH_BOX.setValue(this.searchBoxText);
    }

    public FilterParameters setProductFamily(ProductFamily productFamily) {
        this.productFamily = productFamily;
        return this;
    }

    public FilterParameters setProductLgbk(ProductLgbk productLgbk) {
        this.productLgbk = productLgbk;
        return this;
    }

    public FilterParameters setAllItems(boolean allItems) {
        this.allItems = allItems;
        priceItems = !allItems;
        return this;
    }

    public FilterParameters setPriceItems(boolean priceItems) {
        this.priceItems = priceItems;
        allItems = !priceItems;
        return this;
    }

    public FilterParameters setSearchBoxText(String searchBoxText) {
        this.searchBoxText = searchBoxText;
        return this;
    }
}
