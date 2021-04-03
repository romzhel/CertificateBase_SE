package utils;

import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.product.Product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
public class DoublesPreprocessor {
    private static final Logger logger = LogManager.getLogger(DoublesPreprocessor.class);

    public List<Product> getTreatedItems(List<Product> items) {
        logger.info("preprocessing doubles.... ");

        List<String> orderNumbersS = new ArrayList<>();
        int originalItemsCount = items.size();

        HashSet<String> orderNumbers = new HashSet<>();
        for (Product pr : items) {
            orderNumbers.add(pr.getMaterial());
            orderNumbersS.add(pr.getMaterial());
        }

        if (items.size() == orderNumbers.size()) {
            logger.info("no doubles were found");
            return items;
        }

        int index = 0;
        int firstPos;
        int lastPos;
        while (index < items.size()) {
            do {
                firstPos = orderNumbersS.indexOf(items.get(index).getMaterial());
                lastPos = orderNumbersS.lastIndexOf(items.get(index).getMaterial());

                if (firstPos != lastPos) {
                    orderNumbersS.remove(firstPos);
                    items.remove(firstPos);
                }

            } while (firstPos != lastPos);

            index++;
        }

        logger.info("doubles were found, original size: {}, treated size: {}", originalItemsCount, items.size());

        return items;
    }
}
