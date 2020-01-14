package ui_windows.product.productEditorWindow.configNormsWindow;

import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui_windows.OrdinalWindow;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.options_window.requirements_types_editor.RequirementTypesListViews;
import ui_windows.product.MultiEditor;
import ui_windows.product.Product;
import ui_windows.product.productEditorWindow.CertificateVerificationTable;
import ui_windows.product.productEditorWindow.ProductEditorWindowActions;

import static ui_windows.product.data.DataItemEnum.*;

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

        if (multiEditor == null) {
            Product editedProduct = ProductEditorWindowActions.getEditedItem();
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
            multiEditor.compare(DATA_NORMS_MODE , cnwc.rbInsteadGlobal, MultiEditor.CAN_NOT_BE_SAVED);
            multiEditor.compare(DATA_NORMS_LIST, cnwc.lvSelectedNorms, MultiEditor.CAN_NOT_BE_SAVED);
            cnwc.setNormsModeSaved(multiEditor.getEditedItems().get(0).getNormsMode());

            if (multiEditor.getFieldAndControl("normsMode").isAllTheSame() &&
                    multiEditor.getEditedItems().get(0).getNormsMode() == NormsList.ADD_TO_GLOBAL) {
                cnwc.rbAddToGlobal.setSelected(true);
            } else {
                cnwc.rbInsteadGlobal.setSelected(true);
            }
            /*MultiEditor.FieldsAndControls fac = multiEditor.getFieldAndControl(cnwc.rbInsteadGlobal);
            cnwc.rbInsteadGlobal.setSelected(true);
            cnwc.rbInsteadGlobal.setDisable(true);*/

            requirementTypesListViews = new RequirementTypesListViews(multiEditor, cnwc.lvAllNorms, cnwc.lvSelectedNorms);
            cnwc.setRequirementTypesListViews(requirementTypesListViews);

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
