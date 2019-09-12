package ui_windows.options_window.product_lgbk;

import core.CoreModule;
import database.ProductLgbksDB;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import ui_windows.options_window.requirements_types_editor.RequirementTypesListViews;
import utils.Utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static ui_windows.Mode.ADD;
import static ui_windows.Mode.EDIT;

public class LgbkEditorWindowController implements Initializable {
    private RequirementTypesListViews listViews;

    @FXML
    ComboBox<String> cbFamily;

    @FXML
    ListView<String> lvAllNorms;

    @FXML
    ListView<String> lvSelectedNorms;

    @FXML
    public CheckBox ckbNotUsed;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbFamily.getItems().add("");
        cbFamily.getItems().addAll(CoreModule.getProductFamilies().getFamiliesNames());

        TreeTableView<ProductLgbk> plt = CoreModule.getProductLgbks().getProductLgbksTable().getTableView();
        ProductLgbk pl = plt.getSelectionModel().getSelectedItem().getValue();
        listViews = new RequirementTypesListViews(pl, lvAllNorms, lvSelectedNorms);
    }

    public void apply() {
        AnchorPane root = LgbkEditorWindow.getRootAnchorPane();

        if (LgbkEditorWindow.getMode() == ADD) {
            if (!Utils.hasEmptyControls(root)) {
                ProductLgbk plg = new ProductLgbk(root);
                if (!CoreModule.getProductLgbks().hasDublicates(plg)) {
                    CoreModule.getProductLgbks().addItem(plg);
                    close();
                }
            }
        } else if (LgbkEditorWindow.getMode() == EDIT) {
            TreeTableView<ProductLgbk> plt = CoreModule.getProductLgbks().getProductLgbksTable().getTableView();
            ProductLgbk pl = plt.getSelectionModel().getSelectedItem().getValue();

            pl.setDescription_en(Utils.getControlValue(root, "tfDescriptionEn"));
            pl.setDescription_ru(Utils.getControlValue(root, "tfDescriptionRu"));
            pl.setLgbk(Utils.getControlValue(root, "tfLgbk"));
            pl.setHierarchy(Utils.getControlValue(root, "tfHierarchy"));

            ArrayList<String> normsALS = Utils.getALControlValueFromLV(root, "lvSelectedNorms");
            String normIds = CoreModule.getRequirementTypes().getReqIdsLineFromShortNamesAL(normsALS);
            pl.setNormsList(new NormsList(normIds));

            String familyValue = Utils.getControlValue(root, "cbFamily").trim();
            pl.setFamilyId(familyValue.length() > 0 ? CoreModule.getProductFamilies().getFamilyIdByName(familyValue) : -1);

            pl.setNotUsed(Utils.getControlValue(root, "ckbNotUsed") == "true" ? true : false);

            new ProductLgbksDB().updateData(pl);
            CoreModule.getProductLgbks().getProductLgbksTable().getTableView().refresh();
            close();
        }
    }

    public void close() {
        CoreModule.getProducts().getTableView().refresh();
        LgbkEditorWindow.getStage().close();
    }

    public void moveNorm() {
        listViews.moveNorm();
    }

    public void moveAllNorms() {
        listViews.moveAllNorms();
    }

    public void removeNorm() {
       listViews.removeNorm();
    }

    public void removeAllNorms() {
       listViews.removeAllNorms();
    }

}
