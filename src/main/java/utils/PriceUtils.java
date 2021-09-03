package utils;

import lombok.extern.log4j.Log4j2;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.price_lists_editor.PriceLists;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.product.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ui_windows.options_window.price_lists_editor.se.PriceListContentTable.CONTENT_MODE_FAMILY;
import static ui_windows.options_window.price_lists_editor.se.PriceListContentTable.CONTENT_MODE_LGBK;

@Log4j2
public class PriceUtils {
    public static double roundCost(double value) {
        return BigDecimal.valueOf(value).setScale(5, RoundingMode.HALF_UP).doubleValue();
    }

    public static double addDiscount(double value, int discount) {
        BigDecimal val = BigDecimal.valueOf(value);
        BigDecimal cor = BigDecimal.valueOf((100 - discount) / 100.0);
        BigDecimal res = val.multiply(cor).setScale(5, RoundingMode.HALF_UP);

        return res.doubleValue();
    }

    public static double getFromString(String textValue) throws Exception {
        return new BigDecimal(textValue).setScale(5, RoundingMode.HALF_UP).doubleValue();
    }

    public static double getCostFromPriceList(Product product, Map<String, Object> options) {
        List<PriceListSheet> treatedSheets = new ArrayList<>();
        Object opt = options != null ? options.getOrDefault("priceListSheet", null) : null;

        if (opt instanceof PriceListSheet) {
            treatedSheets.add((PriceListSheet) opt);
        } else {
            for (PriceList priceList : PriceLists.getInstance().getItems()) {
                treatedSheets.addAll(priceList.getSheets());
            }
        }

        for (PriceListSheet pls : treatedSheets) {
            if (!pls.isInPrice(product)) {
                continue;
            }

            if (pls.getContentMode() == CONTENT_MODE_FAMILY)
                pls.getContentTable().switchContentMode(CONTENT_MODE_LGBK);

//                double correction = 1D - ((double) pls.getDiscount() / 100);
            if (pls.getDiscount() <= 30) {
                return addDiscount(product.getLocalPrice(), pls.getDiscount());
            } else {
//                System.out.println("price list sheet " + pls.getSheetName() + ", discount = " + pls.getDiscount() + " %");
                log.warn("price list sheet '{}' has discount = {}%", pls.getSheetName(), pls.getDiscount());
                return roundCost(product.getLocalPrice());
            }
        }

        return 0.0;
    }
}
