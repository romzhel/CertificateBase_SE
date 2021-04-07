package utils.comparation.se;

import lombok.Data;
import lombok.NoArgsConstructor;
import ui_windows.main_window.file_import_window.te.ImportColumnParameter;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ObjectsComparatorResultSe<T> {
    private T item;
    private T item_before;
    private T item_after;
    private List<ImportColumnParameter> changedFields;

    public ObjectsComparatorResultSe(T object1, T object2) {
        changedFields = new ArrayList<>();
        this.item = object1;
        this.item_after = object2;
    }

    public ObjectsComparatorResultSe(T object1, T object2, List<ImportColumnParameter> changedFields) {
        this.item = object1;
        this.item_after = object2;
        this.changedFields = changedFields;
    }

    public void addChangedField(ImportColumnParameter parameter) {
        changedFields.add(parameter);
    }
}
