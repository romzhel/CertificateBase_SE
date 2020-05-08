package ui_windows.product.productEditorWindow.configNormsWindow;

import database.ProductsDB;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.options_window.requirements_types_editor.RequirementTypesListViews;
import ui_windows.product.MultiEditor;
import ui_windows.product.MultiEditorItem;
import ui_windows.product.Product;
import ui_windows.product.Products;
import ui_windows.product.productEditorWindow.CertificateVerificationTable;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static ui_windows.product.data.DataItem.DATA_NORMS_MODE;

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
    private MultiEditor multiEditor;
    private ArrayList<Integer> normsModesSaved;
    private ArrayList<String> normsValuesSaved;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void init(MultiEditor multiEditor, CertificateVerificationTable certificateVerificationTable) {
        this.multiEditor = multiEditor;
        this.certificateVerificationTable = certificateVerificationTable;

        ToggleGroup group = new ToggleGroup();
        rbAddToGlobal.setToggleGroup(group);
        rbInsteadGlobal.setToggleGroup(group);
        saveExistingParameters();

        requirementTypesListViews = new RequirementTypesListViews(multiEditor, lvAllNorms, lvSelectedNorms);
        requirementTypesListViews.display();

        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            for (Product prod : multiEditor.getEditedItems()) {
                prod.setNormsMode(rbAddToGlobal.isSelected() ? NormsList.ADD_TO_GLOBAL : NormsList.INSTEAD_GLOBAL);
            }
            requirementTypesListViews.display();
        });

        MultiEditorItem multiEditorItem = new MultiEditorItem(DATA_NORMS_MODE, MultiEditorItem.CAN_NOT_BE_SAVED);
        multiEditorItem.compare(multiEditor.getEditedItems());
        Object commonValue = multiEditorItem.getCommonValue();

        if (commonValue != null && (int) commonValue == NormsList.ADD_TO_GLOBAL) {
            rbAddToGlobal.setSelected(true);
        } else if (commonValue != null && (int) commonValue == NormsList.INSTEAD_GLOBAL) {
            rbInsteadGlobal.setSelected(true);
        } else {
            rbAddToGlobal.setSelected(false);
            rbInsteadGlobal.setSelected(false);
        }
    }

    public void apply() {
        ArrayList<Product> alp = new ArrayList<>();

        if (rbAddToGlobal.isSelected() || rbInsteadGlobal.isSelected()) {
            for (Product product : multiEditor.getEditedItems()) {
                product.setNormsMode(rbAddToGlobal.isSelected() ? NormsList.ADD_TO_GLOBAL : NormsList.INSTEAD_GLOBAL);
                product.setNormsList(requirementTypesListViews.getProductNormsListForSave(product));
                alp.addAll(multiEditor.getEditedItems());
            }
        }

        boolean saveToDbResult = true;
        if (alp.size() > 0) {
//            new Thread(() -> new ProductsDB().updateData(alp)).start();
            saveToDbResult = new ProductsDB().updateData(alp);
        }

        if (saveToDbResult) {
            Products.getInstance().getTableView().refresh();
            closeWindow();
        }

        certificateVerificationTable.display(certificateVerificationTable.getCheckParameters());
    }

    public void close() {
        for (int i = 0; i < multiEditor.getEditedItems().size(); i++) {
            multiEditor.getEditedItems().get(i).setNormsMode(normsModesSaved.get(i));
            multiEditor.getEditedItems().get(i).setNormsList(new NormsList(normsValuesSaved.get(i)));
        }
        closeWindow();
    }

    private void saveExistingParameters() {
        normsModesSaved = new ArrayList<>();
        normsValuesSaved = new ArrayList<>();
        for (Product product : multiEditor.getEditedItems()) {
            normsModesSaved.add(product.getNormsMode());
            normsValuesSaved.add(product.getNormsList().getStringLine());
        }
    }

    public void closeWindow() {
        ((Stage) lvSelectedNorms.getScene().getWindow()).close();
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
