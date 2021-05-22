package utils.comparation.se;

import ui_windows.main_window.file_import_window.te.ImportColumnParameter;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import utils.comparation.te.ChangedProperty;

import java.util.List;

public interface ComparingRules<T> {
    boolean isTheSameItem(Param<T> params);

    boolean isCanBeSkipped(Param<T> params);

    boolean isCanBeSkipped_v2(ChangedProperty changedProperty);

    String treatMaterial(String material);

    void addHistoryComment(ObjectsComparatorResultSe<T> result);

    boolean addNewItem(T item, List<ImportColumnParameter> fields);

    boolean addNewItem_v2(ImportedProduct item);

    Param<T> applyCustomRule(Param<T> params);
}
