package utils.comparation.products;

import org.apache.logging.log4j.util.Strings;

import java.util.Arrays;

public class ProductNameResolver {

    public static String resolve(String name) {
        return name.replaceAll("(^0+)*(\\-)*(\\:)*(VBPZ)*(BPZ)*(\\s)*([/()])*", "").trim();
    }

    /**
     * Корректная сортировка для второго разряда заказных номеров, например, S54507-C5-A1 и S54507-C22-A1
     */
    public static String prepareMaterialForComparing(String text) {
        String[] parts = text.split("-");

        if (parts.length < 3) {
            return text;
        }

        int num = Math.max(4 - parts[1].length(), 0);
        String addedText = "00".substring(0, num);
        parts[1] = parts[1].substring(0, 1).concat(addedText).concat(parts[1].substring(1));

        return Strings.join(Arrays.asList(parts), '-');
    }
}
