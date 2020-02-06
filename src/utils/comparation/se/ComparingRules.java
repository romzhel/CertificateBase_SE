package utils.comparation.se;

public interface ComparingRules {
    boolean isTheSameItem(Param<?> params);
    boolean isCanBeSkipped(Param<?> params);
    void addHistoryComment(ObjectsComparatorResultSe<?> result);
}
