package utils;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

public class ItemsGroup<T, S> {
    private T groupNode;
    private TreeSet<S> items;

    public ItemsGroup() {
    }

    public ItemsGroup(T groupNode) {
        this.groupNode = groupNode;
        items = new TreeSet<>();
    }

    public ItemsGroup(T groupNode, Comparator<S> comparator) {
        this.groupNode = groupNode;
        items = new TreeSet<>(comparator);
    }

    public boolean addItem(S item) {
        if (!items.contains(item)) {
            items.add(item);
            return true;
        } else return false;
    }

    public void addItems(Collection<S> items) {
        for (S item : items) {
            this.items.add(item);
        }
    }

    public TreeSet<S> getItems() {
        return items;
    }

    public T getGroupNode() {
        return groupNode;
    }

    public void setComparator(Comparator<S> comparator) {
        items = new TreeSet<>(comparator);
    }
}
