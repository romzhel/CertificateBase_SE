package utils.comparation.se;

import java.util.ArrayList;

public class ComparisonResult<T> {
    private ArrayList<ObjectsComparatorResultSe<T>> newItemsResult;
    private ArrayList<ObjectsComparatorResultSe<T>> changedItemsResult;
    private ArrayList<ObjectsComparatorResultSe<T>> goneItemsResult;

    public ComparisonResult() {
        newItemsResult = new ArrayList<>();
        changedItemsResult = new ArrayList<>();
        goneItemsResult = new ArrayList<>();
    }

    public void addNewItemResult(ObjectsComparatorResultSe<T> result) {
        if (result != null) {
            newItemsResult.add(result);
        }
    }

    public void addChangedItemResult(ObjectsComparatorResultSe<T> result) {
        if (result != null) {
            changedItemsResult.add(result);
        }
    }

    public void addGoneItemResult(ObjectsComparatorResultSe<T> result) {
        if (result != null) {
            goneItemsResult.add(result);
        }
    }

    public ArrayList<ObjectsComparatorResultSe<T>> getNewItemsResult() {
        return newItemsResult;
    }

    public ArrayList<ObjectsComparatorResultSe<T>> getChangedItemsResult() {
        return changedItemsResult;
    }

    public ArrayList<ObjectsComparatorResultSe<T>> getGoneItemsResult() {
        return goneItemsResult;
    }

    public ArrayList<T> getNewItems() {
        ArrayList<T> items = new ArrayList<>();
        for (ObjectsComparatorResultSe<T> res : newItemsResult) {
            if (res.getItem_before() != null) {
                items.add(res.getItem_before());
            }
        }
        return items;
    }

    public ArrayList<T> getChangedItems() {
        ArrayList<T> items = new ArrayList<>();
        for (ObjectsComparatorResultSe<T> res : changedItemsResult) {
            if (res.getItem() != null) {
                items.add(res.getItem());
            }
        }
        return items;
    }

    public ArrayList<T> getGoneItems() {
        ArrayList<T> items = new ArrayList<>();
        for (ObjectsComparatorResultSe<T> res : goneItemsResult) {
            if (res.getItem() != null) {
                items.add(res.getItem());
            }
        }
        return items;
    }
}
