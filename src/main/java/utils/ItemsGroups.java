package utils;

import java.util.Comparator;
import java.util.TreeSet;

public class ItemsGroups<U, S, T> {
    private ItemsGroup<U, ItemsGroup<S, T>> groupNode;
    private Comparator<ItemsGroup<S, T>> comparator;

    public ItemsGroups(U node, Comparator<ItemsGroup<S, T>> comparator) {
        groupNode = new ItemsGroup<U, ItemsGroup<S, T>>(node, comparator);
        this.comparator = comparator;
    }

    public void addGroup(ItemsGroup<S, T> newGroup) {
        boolean isNewGroup = true;
        for (ItemsGroup<S, T> group: groupNode.getItems()) {
            if (group.getGroupNode().equals(newGroup.getGroupNode()) ||
                    comparator != null && comparator.compare(group, newGroup) == 0) {
                group.addItems(newGroup.getItems());
                isNewGroup = false;
            }
        }

        if (isNewGroup) {
            groupNode.addItem(newGroup);
        }
    }

    public ItemsGroup<S, T> getGroup(S groupNode) {
        for (ItemsGroup<S, T> group : this.groupNode.getItems()) {
            if (group.getGroupNode().equals(groupNode)) {
                return group;
            }
        }
        return null;
    }

    public void deleteGroup(S groupNode) {
        /*for (ItemsGroup<S, T> group : this.groupNode.getItems()) {
            if (group.getGroupNode().equals(groupNode)) {*/
                this.groupNode.getItems().remove(groupNode);
        /*    }
        }*/
    }

    public TreeSet<ItemsGroup<S, T>> getItems(){
        return groupNode.getItems();
    }
}
