package utils.comparation.se;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

public class ChangesFixer<T> {
    private static final Logger logger = LogManager.getLogger(ChangesFixer.class);

    public boolean fixChanges(ObjectsComparatorResultSe<T> ocr) {
        int fixedCount = 0;
        for (Field field : ocr.getChangedFields()) {
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(ocr.getItem_after());
                field.set(ocr.getItem(), value);
                fixedCount++;
            } catch (IllegalAccessException e) {
                logger.error("Error setting value '{}' for field '{}'", value, field.getName());
            }
        }
        return fixedCount == ocr.getChangedFields().size();
    }


}
