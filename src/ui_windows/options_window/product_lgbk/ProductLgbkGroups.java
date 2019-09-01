package ui_windows.options_window.product_lgbk;

import core.CoreModule;
import core.Dialogs;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import ui_windows.product.Product;
import ui_windows.product.Products;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

public class ProductLgbkGroups {
    private ProductLgbk rootNode;
    private TreeItem<ProductLgbk> treeItemRoot;
    private TreeSet<ProductLgbkGroup> lgbkGroups;

    public ProductLgbkGroups() {
        lgbkGroups = new TreeSet<>((o1, o2) -> o1.getGroupNode().getLgbk().compareTo(o2.getGroupNode().getLgbk()));
    }

    public ProductLgbkGroups get() {
        lgbkGroups.clear();
        if (CoreModule.getProductLgbks().getProductLgbks().size() > 0) {
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
        ArrayList<ProductLgbk> newProductLgbk = new ArrayList<>();

        for (Product product : products.getItems()) {
            String hierarchyName = product.getHierarchy();
            String productHierarchy = hierarchyName.isEmpty() ? "" : hierarchyName.matches("^\\d.*") ?
                    hierarchyName.substring(1, 4) : hierarchyName.substring(0, 3);

            ProductLgbk newLgbk = new ProductLgbk(product.getLgbk(), productHierarchy + "...");
            ProductLgbk newLgbkForGroup = new ProductLgbk(product.getLgbk(), "Все", ProductLgbk.GROUP_NODE);
            ProductLgbkGroup newGroup = new ProductLgbkGroup(newLgbkForGroup);

            if (!lgbkGroups.contains(newGroup)) {
                lgbkGroups.add(newGroup);
                newGroup.addProductLgbkNode(newLgbk);
                newProductLgbk.add(newLgbkForGroup);
                newProductLgbk.add(newLgbk);
            } else {
                Iterator<ProductLgbkGroup> iterator = lgbkGroups.iterator();
                while (iterator.hasNext()) {
                    ProductLgbkGroup temp = iterator.next();
                    if (temp.getGroupNode().getLgbk().equals(newLgbk.getLgbk())) {
                        if (temp.addProductLgbkNode(newLgbk)) newProductLgbk.add(newLgbk);
                        break;
                    }
                }
            }
        }

        return newProductLgbk;
    }

    public void createFromLgbks(ProductLgbks productLgbks) {
        lgbkGroups.clear();
        for (ProductLgbk lgbk : productLgbks.getProductLgbks()) {
            if (lgbk.getNodeType() == ProductLgbk.ROOT_NODE) {
                rootNode = lgbk;
                continue;
            } else if (lgbk.getNodeType() == ProductLgbk.GROUP_NODE) {
                lgbkGroups.add(new ProductLgbkGroup(lgbk));
                continue;
            } else if (lgbk.getNodeType() == ProductLgbk.ITEM_NODE) {
                Iterator<ProductLgbkGroup> iterator = lgbkGroups.iterator();
                while (iterator.hasNext()) {
                    ProductLgbkGroup temp = iterator.next();
                    if (temp.getGroupNode().getLgbk().equals(lgbk.getLgbk())) {
                        temp.addProductLgbkNode(lgbk);
                        continue;
                    }
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
            TreeTableView<ProductLgbk> tableView = CoreModule.getProductLgbks().getProductLgbksTable().getTableView();
            if (tableView != null) tableView.setRoot(getFullTreeSet());
        }
    }

    public TreeItem<ProductLgbk> getFullTreeSet() {
        treeItemRoot = new TreeItem<>(rootNode);

        for (ProductLgbkGroups.ProductLgbkGroup lgbkGroup : lgbkGroups) {
            TreeItem<ProductLgbk> lgbkGroupNode = new TreeItem<>(lgbkGroup.getGroupNode());

            for (ProductLgbk productLgbk : lgbkGroup.getLgbkItems()) {
                lgbkGroupNode.getChildren().add(new TreeItem<>(productLgbk));
            }

            treeItemRoot.getChildren().add(lgbkGroupNode);
        }

        return treeItemRoot;
    }

    public ArrayList<ProductLgbk> getLgbks() {
        ArrayList<ProductLgbk> result = new ArrayList<>();

        result.add(rootNode);

        for (ProductLgbkGroups.ProductLgbkGroup lgbkGroup : lgbkGroups) {
            result.add(lgbkGroup.getGroupNode());
            result.addAll(lgbkGroup.getLgbkItems());
        }

        return result;
    }

    public LgbkAndParent getLgbkAndParent(ProductLgbk lookingForLgbk) {
        ProductLgbkGroup lookingForGroup = new ProductLgbkGroup(lookingForLgbk);
        LgbkAndParent result = new LgbkAndParent(rootNode);

        if (lgbkGroups.contains(lookingForGroup)) {
            Iterator<ProductLgbkGroup> iterator = lgbkGroups.iterator();
            while (iterator.hasNext()) {
                ProductLgbkGroup temp = iterator.next();

                String comp1 = lookingForLgbk.getLgbk();
                String comp2 = temp.getGroupNode().getLgbk();

                if (comp1.equals(comp2)) {
                    lookingForGroup = temp;
                    result.setLgbkParent(temp.getGroupNode());
                    break;
                }
            }

            Iterator<ProductLgbk> iterator2 = lookingForGroup.getLgbkItems().iterator();
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
        LgbkAndParent lgbkAndParent = getLgbkAndParent(productLgbk);
        if (lgbkAndParent == null) return "";

        return lgbkAndParent.getLgbkParent().getDescription() + " / " + lgbkAndParent.getLgbkItem().getDescription();
    }

    public TreeItem<ProductLgbk> getTreeItem(ProductLgbk lookingForLgbk) {
        if (treeItemRoot == null) return null;
        if (treeItemRoot.getValue().equals(lookingForLgbk)) return treeItemRoot;

        for (TreeItem<ProductLgbk> groupTreeItem : treeItemRoot.getChildren()) {
            if (groupTreeItem.getValue().getId() == lookingForLgbk.getId()) return groupTreeItem;

            if (groupTreeItem.getValue().getLgbk().equals(lookingForLgbk.getLgbk())) {
                for (TreeItem<ProductLgbk> subgroupTreeItem : groupTreeItem.getChildren()) {
                    if (lookingForLgbk.getId() == subgroupTreeItem.getValue().getId() ||
                            lookingForLgbk.getHierarchy().contains(subgroupTreeItem.getValue().getHierarchy().replaceAll("\\.", ""))) {
                        return subgroupTreeItem;
                    }
                }
            }
        }

        return null;
    }

    public HashSet<Integer> getGlobalNormIds(ProductLgbk productLgbk) {
        HashSet<Integer> globalNorms = new HashSet<>();

        TreeItem<ProductLgbk> selectedTreeItem = CoreModule.getProductLgbkGroups().getTreeItem(productLgbk);
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

    private class ProductLgbkGroup {
        private ProductLgbk groupNode;
        private TreeSet<ProductLgbk> lgbkItems;

        private ProductLgbkGroup(ProductLgbk groupNode) {
            this.groupNode = groupNode;
            groupNode.setNodeType(ProductLgbk.GROUP_NODE);
            lgbkItems = new TreeSet<>((o1, o2) -> o1.getHierarchy().compareTo(o2.getHierarchy()));
        }

        private boolean addProductLgbkNode(ProductLgbk lgbk) {
            if (!lgbkItems.contains(lgbk)) {
                lgbkItems.add(lgbk);
                return true;
            } else return false;
        }

        public TreeSet<ProductLgbk> getLgbkItems() {
            return lgbkItems;
        }

        public ProductLgbk getGroupNode() {
            return groupNode;
        }
    }
}
