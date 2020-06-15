package ui_windows.options_window.product_lgbk;

import database.ProductLgbksDB;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;
import ui_windows.options_window.families_editor.ProductFamilies;
import ui_windows.options_window.requirements_types_editor.RequirementTypes;
import ui_windows.options_window.requirements_types_editor.RequirementTypesListViews;
import ui_windows.product.Products;
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
        cbFamily.getItems().addAll(ProductFamilies.getInstance().getFamiliesNames());

        TreeTableView<ProductLgbk> plt = ProductLgbks.getInstance().getProductLgbksTable().getTableView();
        ProductLgbk pl = plt.getSelectionModel().getSelectedItem().getValue();
        listViews = new RequirementTypesListViews(pl, lvAllNorms, lvSelectedNorms);
    }

    public void apply() {
        AnchorPane root = LgbkEditorWindow.getRootAnchorPane();

        if (LgbkEditorWindow.getMode() == ADD) {
            if (!Utils.hasEmptyControls(root)) {
                ProductLgbk plg = new ProductLgbk(root);
                if (!ProductLgbks.getInstance().hasDublicates(plg)) {
                    ProductLgbks.getInstance().addItem(plg);
                    close();
                }
            }
        } else if (LgbkEditorWindow.getMode() == EDIT) {
            TreeTableView<ProductLgbk> plt = ProductLgbks.getInstance().getProductLgbksTable().getTableView();
            ProductLgbk pl = plt.getSelectionModel().getSelectedItem().getValue();

            pl.setDescription_en(Utils.getControlValue(root, "tfDescriptionEn"));
            pl.setDescription_ru(Utils.getControlValue(root, "tfDescriptionRu"));
            pl.setLgbk(Utils.getControlValue(root, "tfLgbk"));
            pl.setHierarchy(Utils.getControlValue(root, "tfHierarchy"));

            ArrayList<String> normsALS = Utils.getALControlValueFromLV(root, "lvSelectedNorms");
            String normIds = RequirementTypes.getInstance().getReqIdsLineFromShortNamesAL(normsALS);
            pl.setNormsList(new NormsList(normIds));

            String familyValue = Utils.getControlValue(root, "cbFamily").trim();
            pl.setFamilyId(familyValue.length() > 0 ? ProductFamilies.getInstance().getFamilyIdByName(familyValue) : -1);

            pl.setNotUsed(Utils.getControlValue(root, "ckbNotUsed").equals("true"));

            new ProductLgbksDB().updateData(pl);
            ProductLgbks.getInstance().getProductLgbksTable().getTableView().refresh();
            close();
        }
    }

    public void close() {
        Products.getInstance().getTableView().refresh();
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
