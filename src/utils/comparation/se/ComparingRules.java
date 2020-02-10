package utils.comparation.se;

import java.lang.reflect.Field;
import java.util.ArrayList;

public interface ComparingRules {
    boolean isTheSameItem(Param<?> params);
    boolean isCanBeSkipped(Param<?> params);
    void addHistoryComment(ObjectsComparatorResultSe<?> result);
    boolean addNewItem(Object object, ArrayList<Field> fields);
}
