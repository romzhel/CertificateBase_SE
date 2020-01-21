package ui_windows.options_window.product_lgbk;

import core.CoreModule;
import core.Dialogs;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import ui_windows.product.Product;
import ui_windows.product.Products;
import utils.ItemsGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

public class ProductLgbkGroups {
    private ProductLgbk rootNode;
    private TreeItem<ProductLgbk> treeItemRoot;
    private TreeSet<ItemsGroup<ProductLgbk, ProductLgbk>> lgbkGroups;

    public ProductLgbkGroups() {
        lgbkGroups = new TreeSet<>((o1, o2) ->
                o1.getGroupNode().getLgbk().compareToIgnoreCase(o2.getGroupNode().getLgbk()));
    }

    public ProductLgbkGroups get() {
        lgbkGroups.clear();
        if (CoreModule.getProductLgbks().getItems().size() > 0) {
            createFromLgbks(CoreModule.getProductLgbks());
        } else {
            createFromProducts(CoreModule.getProducts());
            CoreModule.getProductLgbks().addItems(getLgbks());
        }

        getFullTreeSet();
        return this;
    }

    public ArrayList<ProductLgbk> createFromProducts(Products products) {
        rootNode = new ProductLgbk("Все позиции", "...", ProductLgbk.ROOT_NODE);
        ArrayList<ProductLgbk> newProductLgbks = new ArrayList<>();

        for (Product product : products.getItems()) {
            String hierarchyName = product.getHierarchy();
            String productHierarchy = hierarchyName.isEmpty() ? "" : hierarchyName.matches("^\\d.*") ?
                    hierarchyName.substring(1, 4) : hierarchyName.substring(0, 3);

            ProductLgbk newLgbk = new ProductLgbk(product.getLgbk(), productHierarchy + "...");
            ProductLgbk newLgbkForGroup = new ProductLgbk(product.getLgbk(), "Все", ProductLgbk.GROUP_NODE);
            ItemsGroup<ProductLgbk, ProductLgbk> newHierarchyGroup = new ItemsGroup<>(newLgbkForGroup,
                    (o1, o2) -> o1.getHierarchy().compareToIgnoreCase(o2.getHierarchy()));

            if (!lgbkGroups.contains(newHierarchyGroup)) {
                lgbkGroups.add(newHierarchyGroup);
                newHierarchyGroup.addItem(newLgbk);
                newProductLgbks.add(newLgbkForGroup);
                newProductLgbks.add(newLgbk);
            } else {
                Iterator<ItemsGroup<ProductLgbk, ProductLgbk>> iterator = lgbkGroups.iterator();
                while (iterator.hasNext()) {
                    ItemsGroup<ProductLgbk, ProductLgbk> temp = iterator.next();
                    if (temp.getGroupNode().getLgbk().equals(newLgbk.getLgbk())) {
                        if (temp.addItem(newLgbk)) newProductLgbks.add(newLgbk);
                        break;
                    }
                }
            }
        }

        return newProductLgbks;
    }

    public void createFromLgbks(ProductLgbks productLgbks) {
        lgbkGroups.clear();
        for (ProductLgbk lgbk : productLgbks.getItems()) {
            if (lgbk.getNodeType() == ProductLgbk.ROOT_NODE) {
                rootNode = lgbk;
                continue;
            } else if (lgbk.getNodeType() == ProductLgbk.GROUP_NODE) {
                lgbkGroups.add(new ItemsGroup<ProductLgbk, ProductLgbk>(lgbk,
                        (o1, o2) -> o1.getHierarchy().compareToIgnoreCase(o2.getHierarchy())));
                continue;
            } else if (lgbk.getNodeType() == ProductLgbk.ITEM_NODE) {
                Iterator<ItemsGroup<ProductLgbk, ProductLgbk>> iterator = lgbkGroups.iterator();
                boolean wasFound = false;
                while (iterator.hasNext()) {
                    ItemsGroup<ProductLgbk, ProductLgbk> temp = iterator.next();
                    if (temp.getGroupNode().getLgbk().equals(lgbk.getLgbk())) {
                        temp.addItem(lgbk);
                        wasFound = true;
                        break;
                    }
                }
                if (!wasFound) {
                    System.out.println(lgbk.getLgbk() + "/" + lgbk.getHierarchy() + " was not added due group absent !!!");
                }
            }
        }
    }

    public void checkConsistency() {
        ArrayList<ProductLgbk> newLgbks = createFromProducts(CoreModule.getProducts());

        String newNames = "";
        for (ProductLgbk plgbk : newLgbks) {
            plgbk.setNormsList(new NormsList(new ArrayList<Integer>()));
            newNames = newNames.concat("\n" + plgbk.getLgbk() + ", " + plgbk.getHierarchy());
        }

        final String newNamesF = newNames;
        Platform.runLater(() -> Dialogs.showMessage("Проверка новых направлений",
                "Обнаружено новых направлений: " + newLgbks.size() + newNamesF));


        CoreModule.getProductLgbks().addItems(newLgbks);

        if (newLgbks.size() > 0) {
            if (CoreModule.getProductLgbks().getProductLgbksTable() != null) {
                TreeTableView<ProductLgbk> tableView = CoreModule.getProductLgbks().getProductLgbksTable().getTableView();
                if (tableView != null) tableView.setRoot(getFullTreeSet());
            }
        }
    }

    public TreeItem<ProductLgbk> getFullTreeSet() {
        treeItemRoot = new TreeItem<>(rootNode);

        for (ItemsGroup<ProductLgbk, ProductLgbk> lgbkGroup : lgbkGroups) {
            TreeItem<ProductLgbk> lgbkGroupNode = new TreeItem<>(lgbkGroup.getGroupNode());

            for (ProductLgbk productLgbk : lgbkGroup.getItems()) {
                lgbkGroupNode.getChildren().add(new TreeItem<>(productLgbk));
            }

            treeItemRoot.getChildren().add(lgbkGroupNode);
        }

        return treeItemRoot;
    }

    public ArrayList<ProductLgbk> getLgbks() {
        ArrayList<ProductLgbk> result = new ArrayList<>();

        result.add(rootNode);

        for (ItemsGroup<ProductLgbk, ProductLgbk> lgbkGroup : lgbkGroups) {
            result.add(lgbkGroup.getGroupNode());
            result.addAll(lgbkGroup.getItems());
        }

        return result;
    }

    public LgbkAndParent getLgbkAndParent(ProductLgbk lookingForLgbk) {
        ItemsGroup<ProductLgbk, ProductLgbk> lookingForGroup = new ItemsGroup<>(lookingForLgbk,
                (o1, o2) -> o1.getLgbk().compareToIgnoreCase(o2.getLgbk()));
        LgbkAndParent result = new LgbkAndParent(rootNode);

        if (lgbkGroups.contains(lookingForGroup)) {
            Iterator<ItemsGroup<ProductLgbk, ProductLgbk>> iterator = lgbkGroups.iterator();
            while (iterator.hasNext()) {
                ItemsGroup<ProductLgbk, ProductLgbk> temp = iterator.next();

                String comp1 = lookingForLgbk.getLgbk();
                String comp2 = temp.getGroupNode().getLgbk();

                if (comp1.equals(comp2)) {
                    lookingForGroup = temp;
                    result.setLgbkParent(temp.getGroupNode());
                    break;
                }
            }

            Iterator<ProductLgbk> iterator2 = lookingForGroup.getItems().iterator();
            while (iterator2.hasNext()) {
                ProductLgbk tempLgbk = iterator2.next();

                String comp3 = tempLgbk.getHierarchy().replaceAll("\\.", "");
                String comp4 = lookingForLgbk.getHierarchy();

                if (comp4.contains(comp3) && !comp3.isEmpty() || comp3.isEmpty() && comp4.isEmpty()) {
                    result.setLgbkItem(tempLgbk);
                    return result;
                }
            }
        }

        return null;
    }

    public String getFullDescription(ProductLgbk productLgbk) {
        if (productLgbk == null || productLgbk.getLgbk() == null || productLgbk.getHierarchy() == null) return "";

        LgbkAndParent lgbkAndParent = getLgbkAndParent(productLgbk);
        if (lgbkAndParent == null) return "";

        return lgbkAndParent.getLgbkParent().getDescription() + " / " + lgbkAndParent.getLgbkItem().getDescription();
    }

    public TreeItem<ProductLgbk> getTreeItem(ProductLgbk lookingForLgbk) {
        if (treeItemRoot == null || lookingForLgbk == null) return null;
        if (treeItemRoot.getValue().equals(lookingForLgbk)) return treeItemRoot;

        for (TreeItem<ProductLgbk> groupTreeItem : treeItemRoot.getChildren()) {
            if (groupTreeItem.getValue().getId() == lookingForLgbk.getId()) return groupTreeItem;

            if (groupTreeItem.getValue().getLgbk().equals(lookingForLgbk.getLgbk())) {
                for (TreeItem<ProductLgbk> subgroupTreeItem : groupTreeItem.getChildren()) {
                    String containingPart = subgroupTreeItem.getValue().getHierarchy().replaceAll("\\.", "");

                    if (lookingForLgbk.getId() == subgroupTreeItem.getValue().getId() ||
                            !containingPart.isEmpty() && lookingForLgbk.getHierarchy().contains(containingPart)/* ||
                            containingPart.isEmpty() && lookingForLgbk.getHierarchy().isEmpty()*/) {
                        return subgroupTreeItem;
                    }
                }
            }
        }

        return null;
    }

    public HashSet<Integer> getGlobalNormIds(ProductLgbk productLgbk) {
        HashSet<Integer> globalNorms = new HashSet<>();

        TreeItem<ProductLgbk> selectedTreeItem = getTreeItem(productLgbk);
        while (selectedTreeItem != null) {
            globalNorms.addAll(selectedTreeItem.getValue().getNormsList().getIntegerItems());
            selectedTreeItem = selectedTreeItem.getParent();
        }

        return globalNorms;
    }

    public TreeSet<String> getGroupLgbkDescriptions() {
        TreeSet<String> result = new TreeSet<>();
        String description;
        for (TreeItem<ProductLgbk> plgbk : treeItemRoot.getChildren()) {
            description = plgbk.getValue().getDescription();
            if (plgbk.getValue() != null && !plgbk.getValue().getLgbk().isEmpty()) {
                result.add("[" + plgbk.getValue().getLgbk() + "] " + description);
            }
        }
        return result;
    }

    public ProductLgbk getRootNode() {
        return rootNode;
    }
}
