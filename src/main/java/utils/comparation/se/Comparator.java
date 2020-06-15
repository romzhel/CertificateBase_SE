package utils.comparation.se;

import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class Comparator<T extends Cloneable> {
    private ComparingParameters comparingParameters;
    private ComparisonResult<T> comparisonResult;
    private ChangesFixer<T> changesFixer;
    private Callback<Param<T>, Param<T>> customComparingRule;

    public Comparator() {
        comparisonResult = new ComparisonResult<>();
    }

    public ComparisonResult<T> compare(T object1, T object2, ComparingParameters parameters) {
        ArrayList<T> items1 = new ArrayList<>();
        ArrayList<T> items2 = new ArrayList<>();
        items1.add(object1);
        items2.add(object2);

        return compare(items1, items2, parameters);
    }

    public ComparisonResult<T> compare(List<T> items1, List<T> items2, ComparingParameters parameters) {
        ObjectsComparatorSe<T> objectsComparator = new ObjectsComparatorSe<>();
        comparingParameters = parameters;

        ArrayList<T> goneItems = new ArrayList<>();
        if (parameters.isCheckGoneItems()) goneItems.addAll(items1);

        for (T item1 : items1) {
            for (T item2 : items2) {

                Param<T> params = new Param<>(item1, item2, null);
                if (parameters.getComparingRules().isTheSameItem(params)) {
                    comparisonResult.addChangedItemResult(objectsComparator.compare(item1, item2, parameters));

                    goneItems.remove(item1);
                    items2.remove(item2);

                    break;
                }
            }
        }

        for (T item : items2) {
            if (parameters.getComparingRules().addNewItem(item, parameters.getFields())) {
                comparisonResult.addNewItemResult(new ObjectsComparatorResultSe<>(null, item, parameters.getFields()));
            }
        }

        for (T item : goneItems) {
            comparisonResult.addGoneItemResult(new ObjectsComparatorResultSe<>(item, null, parameters.getFields()));
        }
        return comparisonResult;
    }

    public void fixChanges() {
        changesFixer = new ChangesFixer<>();
        for (ObjectsComparatorResultSe<T> result : comparisonResult.getChangedItemsResult()) {
            if (changesFixer.fixChanges(result)) {
                comparingParameters.getComparingRules().addHistoryComment(result);
            }
        }
        for (ObjectsComparatorResultSe<T> result : comparisonResult.getNewItemsResult()) {
            comparingParameters.getComparingRules().addHistoryComment(result);

        }
        for (ObjectsComparatorResultSe<T> result : comparisonResult.getGoneItemsResult()) {
            comparingParameters.getComparingRules().addHistoryComment(result);
        }
    }

    public String getLog() {
//        return logger.getComment(comparisonResult);
        return "not released yet";
    }

    public ComparisonResult<T> getComparisonResult() {
        return comparisonResult;
    }
}
