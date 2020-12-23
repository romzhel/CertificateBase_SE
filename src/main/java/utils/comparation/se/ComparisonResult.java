package utils.comparation.se;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public abstract class ComparisonResult<T> {
    protected List<ObjectsComparatorResultSe<T>> newItemsResult;
    protected List<ObjectsComparatorResultSe<T>> changedItemsResult;
    protected List<ObjectsComparatorResultSe<T>> goneItemsResult;
    protected List<ObjectsComparatorResultSe<T>> nonChangedItemsResult;

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

    public List<T> getNewItems() {
        return extractItems(newItemsResult, ObjectsComparatorResultSe::getItem_after);
    }

    public List<T> getChangedItems() {
        return extractItems(changedItemsResult, ObjectsComparatorResultSe::getItem);
    }

    public List<T> getGoneItems() {
        return extractItems(goneItemsResult, ObjectsComparatorResultSe::getItem);
    }

    public List<T> getNonChangedItems() {
        return extractItems(nonChangedItemsResult, ObjectsComparatorResultSe::getItem);
    }

    private List<T> extractItems(List<ObjectsComparatorResultSe<T>> resultList, Function<ObjectsComparatorResultSe<T>, T> function) {
        return resultList.stream()
                .map(function)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
