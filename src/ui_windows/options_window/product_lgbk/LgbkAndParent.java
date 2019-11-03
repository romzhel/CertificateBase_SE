package ui_windows.options_window.product_lgbk;

import core.CoreModule;
import ui_windows.options_window.families_editor.ProductFamily;

public class LgbkAndParent {
    private ProductLgbk lgbkRoot;
    private ProductLgbk lgbkParent;
    private ProductLgbk lgbkItem;

    public LgbkAndParent(ProductLgbk lgbkRoot) {
        this.lgbkRoot = lgbkRoot;
    }

    public ProductFamily getProductFamily() {
        if (lgbkItem != null && lgbkItem.getFamilyId() > 0) {
            return CoreModule.getProductFamilies().getFamilyById(lgbkItem.getFamilyId());
        } else if (lgbkParent != null) {
            return CoreModule.getProductFamilies().getFamilyById(lgbkParent.getFamilyId());
        }
        return null;
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
