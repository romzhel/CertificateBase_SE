package ui_windows.options_window.product_lgbk;

import lombok.Data;

@Data
public class LgbkAndParent {
    private ProductLgbk lgbkRoot;
    private ProductLgbk lgbkParent;
    private ProductLgbk lgbkItem;

    public LgbkAndParent(ProductLgbk lgbkRoot) {
        this.lgbkRoot = lgbkRoot;
    }
}
