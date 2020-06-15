package utils.comparation.merger;

import utils.comparation.se.ObjectsComparatorResultSe;

import java.util.ArrayList;
import java.util.Arrays;

public class MergerResultItem<T> {
    private T item;
    private ObjectsComparatorResultSe<T>[] details;

    public MergerResultItem(T item, ObjectsComparatorResultSe<T> detail, int resultCount, int resultIndex) {
        this.item = item;
        details = new ObjectsComparatorResultSe[resultCount];
        details[resultIndex] = detail;
    }

    public void addDetail(ObjectsComparatorResultSe<T> detail, int index) {
        details[index] = detail;
    }

    public T getItem() {
        return item;
    }

    public ArrayList<ObjectsComparatorResultSe<T>> getDetails() {
        return new ArrayList<>(Arrays.asList(details));
    }
}
