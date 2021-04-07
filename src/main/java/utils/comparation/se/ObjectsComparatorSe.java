package utils.comparation.se;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.main_window.file_import_window.te.ImportColumnParameter;

import java.lang.reflect.Field;

public class ObjectsComparatorSe<T extends Cloneable> {
    private static final Logger logger = LogManager.getLogger(ObjectsComparatorSe.class);
    Param<T> param;

    public ObjectsComparatorResultSe<T> compare(T object1, T object2, ComparingParameters<T> ocps) throws RuntimeException {
        ObjectsComparatorResultSe<T> ocr = new ObjectsComparatorResultSe<>(object1, object2);
        for (ImportColumnParameter columnParameter : ocps.getColumnParameters()) {
            Field field = columnParameter.getDataItem().getField();
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
                    ocr.addChangedField(columnParameter);

                    if (ocr.getItem_before() == null) {
                        ocr.setItem_before((T) object1.clone());
                    }
                }
            } catch (IllegalAccessException e) {
                logger.error("Ошибка сравнения '{}' и '{}', result: {}", object1, object2, ocr, e);
                throw new RuntimeException(e);
            }
        }

//        return ocr.getChangedFields().size() > 0 ? ocr : null;
        return ocr;
    }
}
