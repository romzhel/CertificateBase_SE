package utils.comparation.se;

import core.Dialogs;

import java.lang.reflect.Field;

public class ObjectsComparatorSe<T extends Cloneable> {


    public ObjectsComparatorResultSe<T> compare(T object1, T object2, ComparingParameters ocps) {
        ObjectsComparatorResultSe<T> ocr = new ObjectsComparatorResultSe<>(object1, object2);
        for (Field field : ocps.getFields()) {
            if (ocps.getComparingRules().isCanBeSkipped(new Param<>(object1, object2, field))) {
                continue;
            }

            field.setAccessible(true);
            try {
                if (field.get(object2) != null && !field.get(object1).equals(field.get(object2))) {
                    ocr.addChangedField(field);

                    if (ocr.getItem_before() == null) {
                        ocr.setItem_before((T) object1.clone());
                    }
                }
            } catch (IllegalAccessException e) {
                Dialogs.showMessage("Ошибка", "Произошла ошибка при сравнении объектов: " + e.getMessage());
            }
        }

        return ocr.getChangedFields().size() > 0 ? ocr : null;
    }
}
