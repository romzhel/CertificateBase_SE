package utils.comparation.merger;

import utils.comparation.se.ComparisonResult;
import utils.comparation.se.ObjectsComparatorResultSe;

import java.util.ArrayList;
import java.util.Comparator;

public class ComparisonResultMerger<T> {
    private ArrayList<ComparisonResult<T>> resultsForMerging;
    private MergerResult<T> result;

    public ComparisonResultMerger(Comparator<T> itemsComparator) {
        resultsForMerging = new ArrayList<>();
        result = new MergerResult<>(itemsComparator);
    }

    public void addForMerging(ComparisonResult<T> resultForMerging) {
        resultsForMerging.add(resultForMerging);
    }

    public MergerResult<T> merge() {
        int index = 0;
        for (ComparisonResult<T> comparisonResult : resultsForMerging) {

            for (ObjectsComparatorResultSe<T> resultSet : comparisonResult.getNewItemsResult()) {
                result.addItem(resultSet.getItem_after(), resultSet, index);
            }

            for (ObjectsComparatorResultSe<T> resultSet : comparisonResult.getChangedItemsResult()) {
                result.addItem(resultSet.getItem(), resultSet, index);
            }

            for (ObjectsComparatorResultSe<T> resultSet : comparisonResult.getGoneItemsResult()) {
                result.addItem(resultSet.getItem(), resultSet, index);
            }
            index++;
        }

        return null;
    }

    public MergerResult<T> getResult() {
        return result;
    }
}
