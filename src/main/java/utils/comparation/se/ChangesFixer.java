package utils.comparation.se;

import java.lang.reflect.Field;

public class ChangesFixer<T> {

    public boolean fixChanges(ObjectsComparatorResultSe<T> ocr) {
        int fixedCount = 0;
        for (Field field : ocr.getChangedFields()) {
            field.setAccessible(true);
            try {
                field.set(ocr.getItem(), field.get(ocr.getItem_after()));
                fixedCount++;
            } catch (IllegalAccessException e) {
                System.out.println("Error setting value for field " + field.getName());
            }
        }
        return fixedCount == ocr.getChangedFields().size();
    }


}
