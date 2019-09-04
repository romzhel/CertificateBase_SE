package ui_windows.options_window.requirements_types_editor;


import core.CoreModule;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import ui_windows.options_window.product_lgbk.LgbkAndParent;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.MultiEditor;
import ui_windows.product.Product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

public class RequirementTypesListViews {
    private ListView<String> lvAllNorms;
    private ListView<String> lvSelectedNorms;
    private HashSet<String> selectedGlobalNorms = new HashSet<>();
    private Product product;
    private ProductLgbk lgbk;
    private MultiEditor multiEditor;
    boolean normsModeAdd;

    public RequirementTypesListViews(ProductLgbk pl, ListView<String> lvAllNorms, ListView<String> lvSelectedNorms) {
        this.lvAllNorms = lvAllNorms;
        this.lvSelectedNorms = lvSelectedNorms;
        product = null;
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

    public RequirementTypesListViews(Product product, ListView<String> lvAllNorms, ListView<String> lvSelectedNorms) {
        this.lvAllNorms = lvAllNorms;
        this.lvSelectedNorms = lvSelectedNorms;
        this.product = product;
        lgbk = new ProductLgbk(product.getLgbk(), product.getHierarchy());
        initListViews();
        display();
    }

    public void display() {
        lvAllNorms.getItems().clear();
        lvSelectedNorms.getItems().clear();

        lvAllNorms.getItems().addAll(CoreModule.getRequirementTypes().getAllRequirementTypesShortNames());

        if (multiEditor == null) {
            ArrayList<Integer> productNormList = product == null ? new ArrayList<>() : product.getNormsList().getIntegerItems();

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

                                        int reqId = CoreModule.getRequirementTypes().getRequirementByShortName(item).getId();
                                        TreeItem<ProductLgbk> selectedTreeItem = CoreModule.getProductLgbkGroups().getTreeItem(lgbk);

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

                                        if (productNormList.contains(reqId)) {
                                            setStyle("-fx-text-fill: red;");
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

            selectedGlobalNorms.addAll(CoreModule.getRequirementTypes().getReqTypeShortNamesByIds(
                    new ArrayList<>(CoreModule.getProductLgbkGroups().getGlobalNormIds(lgbk))));

            if (product == null || (product != null && product.getNormsMode() == NormsList.ADD_TO_GLOBAL)) {
                lvSelectedNorms.getItems().addAll(selectedGlobalNorms);
                normsModeAdd = true;
            } else if (product != null && product.getNormsMode() == NormsList.INSTEAD_GLOBAL) {

            }

            lvSelectedNorms.getItems().addAll(CoreModule.getRequirementTypes().getReqTypeShortNamesByIds(productNormList));
        } else {
            ArrayList<Integer> productNormList = new ArrayList<>();
            for (Product prod : multiEditor.getEditedItems()) {
                productNormList.addAll(prod.getNormsList().getIntegerItems());
                lvSelectedNorms.getItems().addAll(CoreModule.getRequirementTypes().getReqTypeShortNamesByIds(productNormList));

                LgbkAndParent lap = CoreModule.getProductLgbkGroups().getLgbkAndParent(new ProductLgbk(prod.getLgbk(), prod.getHierarchy()));

                selectedGlobalNorms.addAll(CoreModule.getRequirementTypes().getReqTypeShortNamesByIds(
                        new ArrayList<>(CoreModule.getProductLgbkGroups().getGlobalNormIds(lap.getLgbkItem()))));

                if (multiEditor.getEditedItems().get(0).getNormsMode() == NormsList.ADD_TO_GLOBAL) {
                    lvSelectedNorms.getItems().addAll(selectedGlobalNorms);
                    normsModeAdd = true;
                } else {

                }
            }




        }





        lvAllNorms.getItems().removeAll(lvSelectedNorms.getItems());

        sortLV(lvSelectedNorms);
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
        if (selectedIndex > -1) {
            lvAllNorms.getItems().add(lvSelectedNorms.getItems().remove(selectedIndex));
            sortLV(lvAllNorms);
        }
    }

    public void removeAllNorms() {
        lvAllNorms.getItems().addAll(lvSelectedNorms.getItems());
        sortLV(lvAllNorms);
        lvSelectedNorms.getItems().clear();
    }

    private void sortLV(ListView<String> listView) {
        TreeSet<String> sortedList = new TreeSet<>(listView.getItems());
        listView.getItems().clear();
        listView.getItems().addAll(sortedList);
    }

    public NormsList getProductNormsListForSave() {
        ArrayList<String> onlyProduct = new ArrayList<>();
        onlyProduct.addAll(lvSelectedNorms.getItems());

//        if (product.getNormsMode() == NormsList.ADD_TO_GLOBAL) {
        if (normsModeAdd) {
            onlyProduct.removeAll(selectedGlobalNorms);
        }

        return new NormsList(CoreModule.getRequirementTypes().getReqTypeIdsByShortNames(onlyProduct));
    }


}
