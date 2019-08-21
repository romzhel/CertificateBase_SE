package ui_windows.options_window.product_lgbk;

import core.CoreModule;
import database.ProductLgbksDB;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import utils.Utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TreeSet;

import static ui_windows.Mode.ADD;
import static ui_windows.Mode.EDIT;

public class LgbkEditorWindowController implements Initializable {

    @FXML
    ComboBox<String> cbFamily;

    @FXML
    ListView<String> lvAllNorms;

    @FXML
    ListView<String> lvSelectedNorms;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbFamily.getItems().add("");
        cbFamily.getItems().addAll(CoreModule.getProductFamilies().getFamiliesNames());

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
