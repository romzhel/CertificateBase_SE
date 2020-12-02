package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceUtils {
    public static double roundCost(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static double addDiscount(double value, int discount) {
        BigDecimal val = BigDecimal.valueOf(value);
        BigDecimal cor = BigDecimal.valueOf((100 - discount) / 100.0);
        BigDecimal res = val.multiply(cor).setScale(2, RoundingMode.HALF_UP);

        return res.doubleValue();
    }
}
