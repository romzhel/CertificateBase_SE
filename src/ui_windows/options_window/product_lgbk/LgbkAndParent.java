package ui_windows.options_window.product_lgbk;

public class LgbkAndParent {
    private ProductLgbk lgbkItem;
    private ProductLgbk lgbkParent;

    public LgbkAndParent(ProductLgbk lgbkItem, ProductLgbk lgbkParent) {
        this.lgbkItem = lgbkItem;
        this.lgbkParent = lgbkParent;
    }

    public LgbkAndParent() {
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
}
