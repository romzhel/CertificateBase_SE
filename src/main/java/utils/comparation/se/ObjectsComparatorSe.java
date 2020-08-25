package utils.comparation.se;

import ui.Dialogs;

import java.lang.reflect.Field;

public class ObjectsComparatorSe<T extends Cloneable> {
    Param<T> param;

    public ObjectsComparatorResultSe<T> compare(T object1, T object2, ComparingParameters ocps) {
        ObjectsComparatorResultSe<T> ocr = new ObjectsComparatorResultSe<>(object1, object2);
        for (Field field : ocps.getFields()) {
            param = new Param<>(object1, object2, field);
            if (ocps.getComparingRules().isCanBeSkipped(param)) {
                continue;
            }

            if (ocps.getComparingRules().applyCustomRule(param).getObject2() != object2) {
                ocr.setItem_after(param.getObject2());
            }

            field.setAccessible(true);
            try {
                if (field.get(object2) != null && !field.get(param.getObject1()).equals(field.get(param.getObject2()))) {
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
