package ui_windows.product.productEditorWindow.configNormsWindow;

import core.CoreModule;
import database.ProductsDB;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import ui_windows.options_window.requirements_types_editor.RequirementTypesListViews;
import ui_windows.product.MultiEditor;
import ui_windows.product.Product;
import ui_windows.product.productEditorWindow.ProductEditorWindowActions;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ConfigNormsWindowController implements Initializable {
    @FXML
    ListView<String> lvAllNorms;
    @FXML
    ListView<String> lvSelectedNorms;
    @FXML
    RadioButton rbAddToGlobal;
    @FXML
    RadioButton rbInsteadGlobal;
    private RequirementTypesListViews requirementTypesListViews;
    //    private Product editedProduct;
    private MultiEditor multiEditor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*ToggleGroup group = new ToggleGroup();
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
        });*/
    }

    public void apply() {
        ArrayList<Product> alp = new ArrayList<>();
        if (multiEditor == null) {
            Product editedProduct = ProductEditorWindowActions.getEditedItem();
            editedProduct.setNormsList(requirementTypesListViews.getProductNormsListForSave());
            alp.add(editedProduct);
        } else {
            for (Product product : multiEditor.getEditedItems()) {
                product.setNormsList(requirementTypesListViews.getProductNormsListForSave());
            }

            alp.addAll(multiEditor.getEditedItems());
        }

        if (alp.size() > 0) {
            new Thread(() -> new ProductsDB().updateData(alp)).start();
        }

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

    public MultiEditor getMultiEditor() {
        return multiEditor;
    }

    public void setMultiEditor(MultiEditor multiEditor) {
        this.multiEditor = multiEditor;
    }

    public RequirementTypesListViews getRequirementTypesListViews() {
        return requirementTypesListViews;
    }

    public void setRequirementTypesListViews(RequirementTypesListViews requirementTypesListViews) {
        this.requirementTypesListViews = requirementTypesListViews;
    }

    public ListView<String> getLvAllNorms() {
        return lvAllNorms;
    }

    public void setLvAllNorms(ListView<String> lvAllNorms) {
        this.lvAllNorms = lvAllNorms;
    }

    public ListView<String> getLvSelectedNorms() {
        return lvSelectedNorms;
    }

    public void setLvSelectedNorms(ListView<String> lvSelectedNorms) {
        this.lvSelectedNorms = lvSelectedNorms;
    }

    public RadioButton getRbAddToGlobal() {
        return rbAddToGlobal;
    }

    public void setRbAddToGlobal(RadioButton rbAddToGlobal) {
        this.rbAddToGlobal = rbAddToGlobal;
    }

    public RadioButton getRbInsteadGlobal() {
        return rbInsteadGlobal;
    }

    public void setRbInsteadGlobal(RadioButton rbInsteadGlobal) {
        this.rbInsteadGlobal = rbInsteadGlobal;
    }
}
