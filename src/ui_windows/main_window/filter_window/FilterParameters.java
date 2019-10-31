package ui_windows.main_window.filter_window;

import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.Product;
import utils.SearchBox;

public class FilterParameters {
    private ProductFamily productFamily;
    private ProductLgbk productLgbk;
    private boolean allItems;
    private boolean priceItems;
    private String searchBoxText;

    public FilterParameters(FilterParameter productFamily, FilterParameter productLgbk, FilterParameter allItems,
                            FilterParameter priceItems, String searchBoxText) {
        this.productFamily = (ProductFamily) productFamily.getValue();
        this.productLgbk = (ProductLgbk) productLgbk.getValue();
        this.allItems = (Boolean) allItems.getValue();
        this.priceItems = (Boolean) priceItems.getValue();
        this.searchBoxText = searchBoxText;
    }

    public ProductFamily getProductFamily() {
        return productFamily;
    }

    public ProductLgbk getProductLgbk() {
        return productLgbk;
    }

    public boolean isAllItems() {
        return allItems;
    }

    public boolean isPriceItems() {
        return priceItems;
    }

    public String getSearchBoxText() {
        return searchBoxText;
    }
}
