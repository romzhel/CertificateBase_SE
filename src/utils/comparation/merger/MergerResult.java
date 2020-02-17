package utils.comparation.merger;

import utils.comparation.se.ObjectsComparatorResultSe;

import java.util.ArrayList;
import java.util.Comparator;

public class MergerResult<T> {
    private ArrayList<MergerResultItem<T>> resultItems;
    private Comparator<T> itemsComparator;
    private int detailsCount;

    public MergerResult(Comparator<T> itemsComparator, int detailsCount) {
        this.itemsComparator = itemsComparator;
        resultItems = new ArrayList<>();
        this.detailsCount = detailsCount;
    }

    public void addItem(T item, ObjectsComparatorResultSe<T> detail, int detailIndex) {
        boolean isAbsent = true;
        for (MergerResultItem<T> resultItem : resultItems) {
            if (itemsComparator.compare(item, resultItem.getItem()) == 0) {
                resultItem.addDetail(detail, detailIndex);
                isAbsent = false;
            }
        }

        if (isAbsent) {
            resultItems.add(new MergerResultItem<>(item, detail, detailsCount, detailIndex));
        }
    }

    public ArrayList<MergerResultItem<T>> getResultItems() {
        return resultItems;
    }
}
