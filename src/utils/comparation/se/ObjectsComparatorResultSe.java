package utils.comparation.se;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ObjectsComparatorResultSe<T> {
    private T item;
    private T item_before;
    private T item_after;
    private ArrayList<Field> changedFields;

    public ObjectsComparatorResultSe(T object1, T object2) {
        changedFields = new ArrayList<>();
        this.item = object1;
        this.item_after = object2;
    }

    public ObjectsComparatorResultSe(T object1, T object2, ArrayList<Field> changedFields) {
        this.item = object1;
        this.item_after = object2;
        this.changedFields = changedFields;
    }

    public void addChangedField(Field field) {
        changedFields.add(field);
    }

    public T getItem() {
        return item;
    }

    public T getItem_before() {
        return item_before;
    }

    public ArrayList<Field> getChangedFields() {
        return changedFields;
    }

    public boolean isChanged() {
        return changedFields.size() > 0;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public void setItem_before(T item_before) {
        this.item_before = item_before;
    }

    public T getItem_after() {
        return item_after;
    }

    public void setItem_after(T item_after) {
        this.item_after = item_after;
    }
}
