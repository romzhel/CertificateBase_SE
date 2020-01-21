package ui_windows.product.productEditorWindow.configNormsWindow;

import core.SharedData;
import javafx.collections.ObservableList;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui_windows.OrdinalWindow;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.options_window.requirements_types_editor.RequirementTypesListViews;
import ui_windows.product.MultiEditor;
import ui_windows.product.MultiEditorItem;
import ui_windows.product.Product;
import ui_windows.product.productEditorWindow.CertificateVerificationTable;

import java.util.ArrayList;

import static ui_windows.product.data.DataItem.DATA_NORMS_MODE;

public class ConfigNormsWindow extends OrdinalWindow {
    public ConfigNormsWindow(Stage parentStage, MultiEditor multiEditor, CertificateVerificationTable certificateVerificationTable) {
        super(parentStage, Modality.APPLICATION_MODAL, mode, "configNormsWindow.fxml",
                "Настройка проверки норм");

        ConfigNormsWindowController cnwc = loader.getController();
        cnwc.setMultiEditor(multiEditor);
        cnwc.setCertificateVerificationTable(certificateVerificationTable);

        ToggleGroup group = new ToggleGroup();
        cnwc.rbAddToGlobal.setToggleGroup(group);
        cnwc.rbInsteadGlobal.setToggleGroup(group);

        RequirementTypesListViews requirementTypesListViews;

        if (multiEditor.getMode() == MultiEditor.MODE_SINGLE) {
            Product editedProduct = multiEditor.getEditedItems().get(0);
            cnwc.setNormsModeSaved(editedProduct.getNormsMode());

            if (editedProduct.getNormsMode() == NormsList.ADD_TO_GLOBAL) {
                cnwc.rbAddToGlobal.setSelected(true);
            } else if (editedProduct.getNormsMode() == NormsList.INSTEAD_GLOBAL) {
                cnwc.rbInsteadGlobal.setSelected(true);
            }
            requirementTypesListViews = new RequirementTypesListViews(editedProduct, cnwc.lvAllNorms, cnwc.lvSelectedNorms);
            cnwc.setRequirementTypesListViews(requirementTypesListViews);

            group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                editedProduct.setNormsMode(cnwc.rbAddToGlobal.isSelected() ? NormsList.ADD_TO_GLOBAL : NormsList.INSTEAD_GLOBAL);
                requirementTypesListViews.display();
            });
        } else {
            ArrayList<Integer> normsModesForSave = new ArrayList<>();
            ArrayList<String> normsValuesForSave = new ArrayList<>();
            for (Product product : multiEditor.getEditedItems()) {
                normsModesForSave.add(product.getNormsMode());
                normsValuesForSave.add(product.getNormsList().getStringLine());
            }
            cnwc.setNormsModesSaved(normsModesForSave);
            cnwc.setNormsValuesSaved(normsValuesForSave);

            MultiEditorItem multiEditorItem = new MultiEditorItem(DATA_NORMS_MODE, MultiEditorItem.CAN_NOT_BE_SAVED);
            multiEditorItem.compare(multiEditor.getEditedItems());
            Object commonValue = multiEditorItem.getCommonValue();

            if (commonValue != null && (int) commonValue == NormsList.ADD_TO_GLOBAL) {
                cnwc.rbAddToGlobal.setSelected(true);
            } else if (commonValue != null && (int) commonValue == NormsList.INSTEAD_GLOBAL) {
                cnwc.rbInsteadGlobal.setSelected(true);
            } else {
                cnwc.rbAddToGlobal.setSelected(false);
                cnwc.rbInsteadGlobal.setSelected(false);
            }

            requirementTypesListViews = new RequirementTypesListViews(multiEditor, cnwc.lvAllNorms, cnwc.lvSelectedNorms);
            cnwc.setRequirementTypesListViews(requirementTypesListViews);
            requirementTypesListViews.display();

            group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                for (Product prod : multiEditor.getEditedItems()) {
                    prod.setNormsMode(cnwc.rbAddToGlobal.isSelected() ? NormsList.ADD_TO_GLOBAL : NormsList.INSTEAD_GLOBAL);
                }
                requirementTypesListViews.display();
            });
        }

        stage.show();
    }
}
