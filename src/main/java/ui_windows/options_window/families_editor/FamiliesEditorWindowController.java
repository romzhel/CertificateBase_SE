package ui_windows.options_window.families_editor;

import core.Dialogs;
import database.ProductFamiliesDB;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import ui_windows.options_window.product_lgbk.ProductLgbks;
import utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;

import static ui_windows.Mode.ADD;
import static ui_windows.Mode.EDIT;

public class FamiliesEditorWindowController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void actionFamilyEditorApply() {
        AnchorPane root = FamiliesEditorWindow.getRootAnchorPane();

        if (FamiliesEditorWindow.getMode() == ADD) {
            if (!Utils.hasEmptyControls(root)) {
                ProductFamily pf = new ProductFamily(root);
                if (!ProductFamilies.getInstance().hasDublicates(pf.getName())) {
                    ProductFamilies.getInstance().addItem(pf);
                    FamiliesEditorWindow.getStage().close();
                } else Dialogs.showMessage("Повтор значений", "Такое направление уже имеется");

            }

        } else if (FamiliesEditorWindow.getMode() == EDIT) {
            ProductFamily pfChanged = new ProductFamily(root);

            TableView<ProductFamily> pft = ProductFamilies.getInstance().getProductFamiliesTable().getTableView();
            int index = pft.getSelectionModel().getSelectedIndex();
            ProductFamily pf = pft.getItems().get(index);
            pf.setName(pfChanged.getName());
            pf.setResponsible((pfChanged.getResponsible()));
           new ProductFamiliesDB().updateData(pf);

            FamiliesEditorWindow.getStage().close();
        } else {

        }

        ProductLgbks.getInstance().getProductLgbksTable().getTableView().refresh();
    }

    public void actionFamilyEditorClose() {
        FamiliesEditorWindow.getStage().close();
    }

}
