package ui_windows.product.productEditorWindow.configNormsWindow;

import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui_windows.OrdinalWindow;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.options_window.requirements_types_editor.RequirementTypesListViews;
import ui_windows.product.MultiEditor;
import ui_windows.product.Product;
import ui_windows.product.productEditorWindow.ProductEditorWindowActions;

public class ConfigNormsWindow extends OrdinalWindow {
    public ConfigNormsWindow(Stage parentStage, MultiEditor multiEditor) {
        super(parentStage, Modality.APPLICATION_MODAL, mode, "configNormsWindow.fxml",
                "Настройка проверки норм");

        ConfigNormsWindowController cnwc = loader.getController();
        cnwc.setMultiEditor(multiEditor);

        ToggleGroup group = new ToggleGroup();
        cnwc.rbAddToGlobal.setToggleGroup(group);
        cnwc.rbInsteadGlobal.setToggleGroup(group);

        RequirementTypesListViews requirementTypesListViews;

        if (multiEditor == null) {
            Product editedProduct = ProductEditorWindowActions.getEditedItem();

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

        }

        stage.show();
    }
}
