package ui_windows.product.productEditorWindow.configNormsWindow;

import core.CoreModule;
import database.ProductsDB;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import ui_windows.product.Product;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.options_window.requirements_types_editor.RequirementTypesListViews;
import ui_windows.product.productEditorWindow.ProductEditorWindowActions;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ConfigNormsWindowController implements Initializable {
    private RequirementTypesListViews requirementTypesListViews;
    private Product editedProduct;

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

        editedProduct = ProductEditorWindowActions.getEditedItem();

        if (editedProduct.getNormsMode() == NormsList.ADD_TO_GLOBAL) {
            rbAddToGlobal.setSelected(true);
        } else if (editedProduct.getNormsMode() == NormsList.INSTEAD_GLOBAL) {
            rbInsteadGlobal.setSelected(true);
        }

        requirementTypesListViews = new RequirementTypesListViews(editedProduct, lvAllNorms, lvSelectedNorms);

        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            editedProduct.setNormsMode(rbAddToGlobal.isSelected() ? NormsList.ADD_TO_GLOBAL : NormsList.INSTEAD_GLOBAL);
            requirementTypesListViews.display();
        });
    }

    public void apply() {
        editedProduct.setNormsList(requirementTypesListViews.getProductNormsListForSave());

        ArrayList<Product> alp = new ArrayList<>();
        alp.add(editedProduct);
        new ProductsDB().updateData(alp);
        ProductEditorWindowActions.fillCertificateVerificationTable();
        CoreModule.getProducts().getTableView().refresh();
        close();
    }

    public void close() {
        ConfigNormsWindow.getStage().close();
    }

    public void moveNorm() {
        requirementTypesListViews.moveNorm();
    }

    public void moveAllNorms() {
        requirementTypesListViews.moveAllNorms();
    }

    public void removeNorm() {
        requirementTypesListViews.removeNorm();
    }

    public void removeAllNorms() {
        requirementTypesListViews.removeAllNorms();
    }


}
