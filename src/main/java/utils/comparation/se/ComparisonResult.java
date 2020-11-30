package utils.comparation.se;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ComparisonResult<T> {
    private List<ObjectsComparatorResultSe<T>> newItemsResult;
    private List<ObjectsComparatorResultSe<T>> changedItemsResult;
    private List<ObjectsComparatorResultSe<T>> goneItemsResult;
    private List<ObjectsComparatorResultSe<T>> nonChangedItemsResult;

    public ComparisonResult() {
        newItemsResult = new ArrayList<>();
        changedItemsResult = new ArrayList<>();
        goneItemsResult = new ArrayList<>();
        nonChangedItemsResult = new ArrayList<>();
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

    public void addNonChangedItemResult(ObjectsComparatorResultSe<T> result) {
        if (result != null) {
            nonChangedItemsResult.add(result);
        }
    }

    public List<ObjectsComparatorResultSe<T>> getNewItemsResult() {
        return newItemsResult;
    }

    public List<ObjectsComparatorResultSe<T>> getChangedItemsResult() {
        return changedItemsResult;
    }

    public List<ObjectsComparatorResultSe<T>> getGoneItemsResult() {
        return goneItemsResult;
    }

    public List<ObjectsComparatorResultSe<T>> getNonChangedItemsResult() {
        return nonChangedItemsResult;
    }

    public List<T> getNewItems() {
        List<T> items = new ArrayList<>();
        for (ObjectsComparatorResultSe<T> res : newItemsResult) {
            if (res.getItem_after() != null) {
                items.add(res.getItem_after());
            }
        }
        return items;
    }

    public List<T> getChangedItems() {
        List<T> items = new ArrayList<>();
        for (ObjectsComparatorResultSe<T> res : changedItemsResult) {
            if (res.getItem() != null) {
                items.add(res.getItem());
            }
        }
        return items;
    }

    public List<T> getGoneItems() {
        List<T> items = new ArrayList<>();
        for (ObjectsComparatorResultSe<T> res : goneItemsResult) {
            if (res.getItem() != null) {
                items.add(res.getItem());
            }
        }
        return items;
    }

    public List<T> getNonChangedItems() {
        return nonChangedItemsResult.stream()
                .map(ocr -> ocr.getItem())
                .filter(item -> item != null)
                .collect(Collectors.toList());
    }
}
