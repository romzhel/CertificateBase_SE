package utils.comparation.se;

import ui_windows.main_window.file_import_window.te.ImportColumnParameter;

import java.util.List;

public interface ComparingRules<T> {
    boolean isTheSameItem(Param<T> params);
    boolean isCanBeSkipped(Param<T> params);

    String treatMaterial(String material);

    void addHistoryComment(ObjectsComparatorResultSe<T> result);

    boolean addNewItem(T item, List<ImportColumnParameter> fields);

    Param<T> applyCustomRule(Param<T> params);
}
