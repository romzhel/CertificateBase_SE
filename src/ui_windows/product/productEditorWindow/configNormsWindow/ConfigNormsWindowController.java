package ui_windows.product.productEditorWindow.configNormsWindow;

import core.CoreModule;
import database.ProductsDB;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import ui_windows.options_window.requirements_types_editor.RequirementTypesListViews;
import ui_windows.product.MultiEditor;
import ui_windows.product.Product;
import ui_windows.product.productEditorWindow.CertificateVerificationTable;
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
    private CertificateVerificationTable certificateVerificationTable;
    private RequirementTypesListViews requirementTypesListViews;
    //    private Product editedProduct;
    private MultiEditor multiEditor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        boolean saveToDbResult = true;
        if (alp.size() > 0) {
//            new Thread(() -> new ProductsDB().updateData(alp)).start();
            saveToDbResult = new ProductsDB().updateData(alp);
        }

        if (saveToDbResult) {
            /*boolean isEqTypeFilter = ((ProductEditorWindowController) ProductEditorWindow.getLoader().getController()).rmiTypeFilter.isSelected();
            ProductEditorWindowActions.fillCertificateVerificationTable(isEqTypeFilter);*/
            CoreModule.getProducts().getTableView().refresh();
            close();
        }

        certificateVerificationTable.display(certificateVerificationTable.getCheckParameters());
    }

    public void close() {
//        ConfigNormsWindow.getStage().close();
        ((Stage)lvSelectedNorms.getScene().getWindow()).close();
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

    public void setCertificateVerificationTable(CertificateVerificationTable certificateVerificationTable) {
        this.certificateVerificationTable = certificateVerificationTable;
    }
}
