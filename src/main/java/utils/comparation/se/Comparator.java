package utils.comparation.se;

import java.util.Collection;

public interface Comparator<T extends Cloneable> {
    ComparisonResult<T> compare(T object1, T object2, ComparingParameters<T> parameters);

    ComparisonResult<T> compare(Collection<T> items1, Collection<T> items2, ComparingParameters<T> parameters);

    public void fixChanges();

    ComparisonResult<T> getComparisonResult();
}
