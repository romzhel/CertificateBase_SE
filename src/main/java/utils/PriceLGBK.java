package utils;

import ui_windows.product.Product;

public class PriceLGBK {

    public static String getpriceLgbk(Product product) {
        if (product.getLgbk().matches("^H1S[1-2]+.*$") && product.getHierarchy().matches("^(\\d)?(SMK)+.*$")) {
            return "LVBAC";
        }
        return product.getLgbk();
    }
}
