package utils;

import ui_windows.options_window.product_lgbk.ProductLgbk;

import java.util.Comparator;
import java.util.TreeSet;

public class ItemsGroup<T, S> {
    private T groupNode;
    private TreeSet<S> items;
    private int level;

    public ItemsGroup(T groupNode, Comparator<S> comparator) {
        this.groupNode = groupNode;
//        this.level = level;
        items = new TreeSet<>(comparator);
    }

    public boolean addItem(S item) {
        if (!items.contains(item)) {
            items.add(item);
            return true;
        } else return false;
    }

    public TreeSet<S> getItems() {
        return items;
    }

    public T getGroupNode() {
        return groupNode;
    }
}
