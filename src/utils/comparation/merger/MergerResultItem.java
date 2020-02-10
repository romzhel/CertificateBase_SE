package utils.comparation.merger;

import utils.comparation.se.ObjectsComparatorResultSe;

import java.util.ArrayList;

public class MergerResultItem<T> {
    private T item;
    private ArrayList<ObjectsComparatorResultSe<T>> details;

    public MergerResultItem(T item, ObjectsComparatorResultSe<T> detail, int index) {
        this.item = item;
        addToIndex(detail, index);
    }

    public void addDetail(ObjectsComparatorResultSe<T> detail, int index) {
        addToIndex(detail, index);
    }

    private void addToIndex(ObjectsComparatorResultSe<T> detail, int index) {
        if (details == null) {
            details = new ArrayList<>();
        }

        while(details.size() <= index) {
            details.add(null);
        }

        details.set(index, detail);
    }

    public T getItem() {
        return item;
    }

    public ArrayList<ObjectsComparatorResultSe<T>> getDetails() {
        return details;
    }
}
