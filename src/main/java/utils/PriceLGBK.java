package utils;

import ui_windows.product.Product;

public class PriceLGBK {

    public static String getPriceLgbk(Product product) {
        if (product.getLgbk().matches("^(H1S[1-2]+.*)?(H22M)?$") && product.getHierarchy().matches("^(\\d)?(SMK)+.*$")) {
            return "LVBAC";
        }
        return product.getLgbk();
    }
}
