package utils.comparation.se;

import java.util.ArrayList;

public class ProductsCompareResultsSe<T> {
    private ArrayList<ObjectsComparatorResultSe<T>> newItems;
    private ArrayList<ObjectsComparatorResultSe<T>> changedItems;
    private ArrayList<ObjectsComparatorResultSe<T>> goneItems;

    public ProductsCompareResultsSe() {
        newItems = new ArrayList<>();
        changedItems = new ArrayList<>();
        goneItems = new ArrayList<>();
    }

    public ArrayList<ObjectsComparatorResultSe<T>> getNewItems() {
        return newItems;
    }

    public void addNewItems(ObjectsComparatorResultSe<T> newItems) {
        this.newItems.add(newItems);
    }

    public ArrayList<ObjectsComparatorResultSe<T>> getChangedItems() {
        return changedItems;
    }

    public void addChangedItems(ObjectsComparatorResultSe<T> changedItems) {
        this.changedItems.add(changedItems);
    }

    public ArrayList<ObjectsComparatorResultSe<T>> getGoneItems() {
        return goneItems;
    }

    public void addGoneItems(ObjectsComparatorResultSe<T> goneItems) {
        this.goneItems.add(goneItems);
    }
}
