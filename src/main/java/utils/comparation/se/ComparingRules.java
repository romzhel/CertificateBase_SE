package utils.comparation.se;

import java.lang.reflect.Field;
import java.util.List;

public interface ComparingRules<T> {
    boolean isTheSameItem(Param<T> params);
    boolean isCanBeSkipped(Param<T> params);

    String treatMaterial(String material);

    void addHistoryComment(ObjectsComparatorResultSe<T> result);

    boolean addNewItem(T item, List<Field> fields);

    Param<T> applyCustomRule(Param<T> params);
}
