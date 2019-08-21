package ui_windows.productEditorWindow.configNormsWindow;

import core.CoreModule;
import database.ProductsDB;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import ui_windows.main_window.Product;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.productEditorWindow.ProductEditorWindowActions;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.TreeSet;

public class ConfigNormsWindowController implements Initializable {

    @FXML
    ListView<String> lvAllNorms;

    @FXML
    ListView<String> lvSelectedNorms;

    @FXML
    RadioButton rbAddToGlobal;

    @FXML
    RadioButton rbInsteadGlobal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup group = new ToggleGroup();
        rbAddToGlobal.setToggleGroup(group);
        rbInsteadGlobal.setToggleGroup(group);

        Product editedProduct = ProductEditorWindowActions.getEditedItem();

        if (editedProduct.getNormsMode() == NormsList.ADD_TO_GLOBAL) {
            rbAddToGlobal.setSelected(true);
            lvAllNorms.getItems().addAll(CoreModule.getRequirementTypes().getAllRequirementTypesShortNames());

            for (int normIndex : ProductEditorWindowActions.needNorms) {
                String normShortName = CoreModule.getRequirementTypes().getRequirementByID(normIndex).getShortName();
                lvSelectedNorms.getItems().add(normShortName);
                lvAllNorms.getItems().remove(normShortName);
            }

            for (int normIndex:editedProduct.getNormsList().getIntegerItems()) {
                String normShortName = CoreModule.getRequirementTypes().getRequirementByID(normIndex).getShortName();
                lvSelectedNorms.getItems().add(normShortName);
                lvAllNorms.getItems().remove(normShortName);
            }

            lvSelectedNorms.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
                @Override
                public ListCell<String> call(ListView<String> param) {
                    return new ListCell<String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);

                            if (!isEmpty()) {
                                setText(item);

                                if (getIndex() < ProductEditorWindowActions.needNorms.size()) {   // norms from global
                                    setTextFill(Color.BLUE);
                                } else {        //norms from product

                                }
                            }
                        }
                    };
                }
            });
        } else if (editedProduct.getNormsMode() == NormsList.INSTEAD_GLOBAL){

        }

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

    public void apply() {
        Product editedProduct = ProductEditorWindowActions.getEditedItem();
        HashSet<String> allSelectedNorms = new HashSet<>();
        allSelectedNorms.addAll(lvSelectedNorms.getItems());
        ArrayList<Integer> productNorms = new ArrayList<>();

        for (String shortName:allSelectedNorms             ) {
            productNorms.add(CoreModule.getRequirementTypes().getRequirementByShortName(shortName).getId());
        }

        productNorms.removeAll(ProductEditorWindowActions.needNorms);
        editedProduct.setNormsList(new NormsList(productNorms));

        ArrayList<Product> alp = new ArrayList<>();
        alp.add(editedProduct);
        new ProductsDB().updateData(alp);
        ProductEditorWindowActions.fillCertificateVerificationTable();
        close();
    }

    public void close() {
        ConfigNormsWindow.getStage().close();
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
            lvSelectedNorms.refresh();
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
}
