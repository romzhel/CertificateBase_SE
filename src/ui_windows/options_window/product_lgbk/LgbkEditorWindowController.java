package ui_windows.options_window.product_lgbk;

import core.CoreModule;
import database.ProductLgbksDB;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;

import static ui_windows.Mode.ADD;
import static ui_windows.Mode.EDIT;

public class LgbkEditorWindowController implements Initializable {

    @FXML
    ComboBox<String> cbFamily;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbFamily.getItems().add("");
        cbFamily.getItems().addAll(CoreModule.getProductFamilies().getFamiliesNames());
    }

    public void apply(){
        AnchorPane root = LgbkEditorWindow.getRootAnchorPane();

        if (LgbkEditorWindow.getMode() == ADD) {
            if (!Utils.hasEmptyControls(root)) {
                ProductLgbk plg = new ProductLgbk(root);
                if (!CoreModule.getProductLgbks().hasDublicates(plg)) {
                    CoreModule.getProductLgbks().addItem(plg);
                    close();
                }
            }
        } else if (LgbkEditorWindow.getMode() == EDIT){
            TableView<ProductLgbk> plt = CoreModule.getProductLgbks().getProductLgbksTable().getTableView();
            int index = plt.getSelectionModel().getSelectedIndex();
            ProductLgbk pl = plt.getItems().get(index);

            pl.setDescription(Utils.getControlValue(root, "tfDescription"));
            pl.setLgbk(Utils.getControlValue(root, "tfLgbk"));
            pl.setHierarchy(Utils.getControlValue(root, "tfHierarchy"));

            String familyValue = Utils.getControlValue(root, "cbFamily").trim();
            pl.setFamilyId(familyValue.length() > 0 ? CoreModule.getProductFamilies().getFamilyIdByName(familyValue) : -1);

            pl.setNotUsed(Utils.getControlValue(root, "ckbNotUsed") == "true" ? true : false);

            new ProductLgbksDB().updateData(pl);
            CoreModule.getProductLgbks().getProductLgbksTable().getTableView().refresh();
            close();
        }
    }

    public void close(){
        CoreModule.getProducts().getTableView().refresh();
        LgbkEditorWindow.getStage().close();
    }
}
