package ui_windows.options_window.product_lgbk;

import ui_windows.main_window.Product;

public class LgbkAndParent {
    private ProductLgbk lgbkRoot;
    private ProductLgbk lgbkParent;
    private ProductLgbk lgbkItem;

    public LgbkAndParent(ProductLgbk lgbkRoot) {
        this.lgbkRoot = lgbkRoot;
    }

    public ProductLgbk getLgbkItem() {
        return lgbkItem;
    }

    public ProductLgbk getLgbkParent() {
        return lgbkParent;
    }

    public void setLgbkItem(ProductLgbk lgbkItem) {
        this.lgbkItem = lgbkItem;
    }

    public void setLgbkParent(ProductLgbk lgbkParent) {
        this.lgbkParent = lgbkParent;
    }

    public ProductLgbk getLgbkRoot() {
        return lgbkRoot;
    }

    public void setLgbkRoot(ProductLgbk lgbkRoot) {
        this.lgbkRoot = lgbkRoot;
    }
}
