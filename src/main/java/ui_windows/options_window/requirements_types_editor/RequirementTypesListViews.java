package ui_windows.options_window.requirements_types_editor;


import javafx.collections.ListChangeListener;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.product_lgbk.ProductLgbkGroups;
import ui_windows.product.MultiEditor;
import ui_windows.product.MultiEditorItem;
import ui_windows.product.Product;
import ui_windows.product.Products;
import ui_windows.product.data.DataItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

public class RequirementTypesListViews {
    private ListView<String> lvAllNorms;
    private ListView<String> lvSelectedNorms;
    private HashSet<Integer> globalNorms = new HashSet<>();
    private ProductLgbk lgbk;
    private MultiEditor multiEditor;

    public RequirementTypesListViews(ProductLgbk pl, ListView<String> lvAllNorms, ListView<String> lvSelectedNorms) {
        this.lvAllNorms = lvAllNorms;
        this.lvSelectedNorms = lvSelectedNorms;
        lgbk = pl;
        initListViews();
        display();
    }

    public RequirementTypesListViews(MultiEditor multiEditor, ListView<String> lvAllNorms, ListView<String> lvSelectedNorms) {
        this.lvAllNorms = lvAllNorms;
        this.lvSelectedNorms = lvSelectedNorms;
        this.multiEditor = multiEditor;
        initListViews();
        display();
    }

/*    public RequirementTypesListViews(Product product, ListView<String> lvAllNorms, ListView<String> lvSelectedNorms) {
        this.lvAllNorms = lvAllNorms;
        this.lvSelectedNorms = lvSelectedNorms;
        this.product = product;
        lgbk = new ProductLgbk(product);
        initListViews();
        display();
    }*/

    public void display() {
        HashSet<Integer> normsForDisplaying = new HashSet<>();

        lvAllNorms.getItems().clear();
        lvSelectedNorms.getItems().clear();

        lvAllNorms.getItems().addAll(RequirementTypes.getInstance().getAllRequirementTypesShortNames());

        if (multiEditor != null) {
            MultiEditorItem multiEditorItem = new MultiEditorItem(DataItem.DATA_NORMS_MODE, MultiEditorItem.CAN_NOT_BE_SAVED);
            multiEditorItem.compare(multiEditor.getEditedItems());

//            HashSet<Integer> globalNorms = new HashSet<>();
            HashSet<Integer> productNorms = new HashSet<>();
            for (Product product : multiEditor.getEditedItems()) {
                globalNorms.addAll(Products.getInstance().getGlobalNorms(product));
                productNorms.addAll(product.getNormsList().getIntegerItems());
            }

            if (multiEditorItem.getCommonValue() == null || (int) multiEditorItem.getCommonValue() == NormsList.ADD_TO_GLOBAL) {
                normsForDisplaying.addAll(globalNorms);
            }
            normsForDisplaying.addAll(productNorms);


        } else if (lgbk != null) {
            TreeItem<ProductLgbk> selectedTreeItem = ProductLgbkGroups.getInstance().getTreeItem(lgbk);
            normsForDisplaying.addAll(selectedTreeItem.getValue().getNormsList().getIntegerItems());

        }
        initItemChangeListener(new ArrayList<>(normsForDisplaying));

        lvSelectedNorms.getItems().addAll(RequirementTypes.getInstance().getReqTypeShortNamesByIds(new ArrayList<>(normsForDisplaying)));
        lvAllNorms.getItems().removeAll(lvSelectedNorms.getItems());
        sortLV(lvSelectedNorms);
    }

    private void initItemChangeListener(ArrayList<Integer> productNormList) {
        lvSelectedNorms.getItems().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                lvSelectedNorms.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
                    @Override
                    public ListCell<String> call(ListView<String> param) {
                        return new ListCell<String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);

                                if (!isEmpty()) {
                                    setText(item);

                                    int reqId = RequirementTypes.getInstance().getRequirementByShortName(item).getId();

                                    if (lgbk != null) {
                                        TreeItem<ProductLgbk> selectedTreeItem = ProductLgbkGroups.getInstance().getTreeItem(lgbk);

                                        while (selectedTreeItem != null) {
                                            ProductLgbk currentPrLgbk = selectedTreeItem.getValue();

                                            if (currentPrLgbk.getNormsList().getIntegerItems().contains(reqId)) {
                                                if (currentPrLgbk.getNodeType() == 0) {
                                                    setStyle("-fx-font-weight: bold; -fx-text-fill: blue;");
                                                } else if (currentPrLgbk.getNodeType() == 1) {
                                                    setStyle("-fx-text-fill: green;");
                                                } else if (currentPrLgbk.getNodeType() == 2) {
                                                    setStyle("-fx-text-fill: brown;");
                                                }
                                            }
                                            selectedTreeItem = selectedTreeItem.getParent();
                                        }
                                    } else if (multiEditor != null) {
                                        if (globalNorms.contains(reqId)) {
                                            setStyle("-fx-font-weight: bold; -fx-text-fill: brown;");
                                        }
                                    }

                                } else {
                                    setText(null);
                                    setStyle("");
                                }
                            }
                        };
                    }
                });
            }
        });
    }

    private void initListViews() {
        lvAllNorms.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    moveNorm();
                }
            }
        });

        lvSelectedNorms.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    removeNorm();
                }
            }
        });
    }


    public void moveNorm() {
        int selectedIndex = lvAllNorms.getSelectionModel().getSelectedIndex();
        if (selectedIndex > -1) {
            lvSelectedNorms.getItems().add(lvAllNorms.getItems().remove(selectedIndex));
            sortLV(lvSelectedNorms);
        }
    }

    public void moveAllNorms() {
        lvSelectedNorms.getItems().addAll(lvAllNorms.getItems());
        sortLV(lvSelectedNorms);
        lvAllNorms.getItems().clear();
    }

    public void removeNorm() {
        int selectedIndex = lvSelectedNorms.getSelectionModel().getSelectedIndex();
        boolean isGlobalNorm = globalNorms.contains(RequirementTypes.getInstance().getRequirementByShortName(lvSelectedNorms.getSelectionModel().getSelectedItem()).getId());
        if (selectedIndex > -1 && !isGlobalNorm) {
            lvAllNorms.getItems().add(lvSelectedNorms.getItems().remove(selectedIndex));
            sortLV(lvAllNorms);
        }
    }

    public void removeAllNorms() {
        for (String normName : lvSelectedNorms.getItems()) {
            boolean isGlobalNorm = globalNorms.contains(RequirementTypes.getInstance().getRequirementByShortName(normName).getId());
            if (!isGlobalNorm) {
                lvAllNorms.getItems().add(normName);
            }
        }
        lvSelectedNorms.getItems().removeAll(lvAllNorms.getItems());
        sortLV(lvAllNorms);
    }

    private void sortLV(ListView<String> listView) {
        TreeSet<String> sortedList = new TreeSet<>(listView.getItems());
        listView.getItems().clear();
        listView.getItems().addAll(sortedList);
    }

    public NormsList getProductNormsListForSave(Product product) {
        ArrayList<String> onlyProduct = new ArrayList<>();
        onlyProduct.addAll(lvSelectedNorms.getItems());

//        if (product.getNormsMode() == NormsList.ADD_TO_GLOBAL) {
        if (product.getNormsMode() == NormsList.ADD_TO_GLOBAL) {
            onlyProduct.removeAll(RequirementTypes.getInstance().getReqTypeShortNamesByIds(new ArrayList<>(globalNorms)));
        }

        return new NormsList(RequirementTypes.getInstance().getReqTypeIdsByShortNames(onlyProduct));
    }


}
