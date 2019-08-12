package ui_windows.options_window.families_editor;

import core.CoreModule;
import core.Dialogs;
import database.ProductFamiliesDB;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;

import static ui_windows.Mode.*;

public class FamiliesEditorWindowController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void actionFamilyEditorApply() {
        AnchorPane root = FamiliesEditorWindow.getRootAnchorPane();

        if (FamiliesEditorWindow.getMode() == ADD) {
            if (!Utils.hasEmptyControls(root)) {
                ProductFamily pf = new ProductFamily(root);
                if (!CoreModule.getProductFamilies().hasDublicates(pf.getName())){
                    CoreModule.getProductFamilies().addItem(pf);
                    FamiliesEditorWindow.getStage().close();
                } else Dialogs.showMessage("Повтор значений", "Такое направление уже имеется");

            }

        } else if (FamiliesEditorWindow.getMode() == EDIT) {
            ProductFamily pfChanged = new ProductFamily(root);

            TableView<ProductFamily> pft = CoreModule.getProductFamilies().getProductFamiliesTable().getTableView();
            int index = pft.getSelectionModel().getSelectedIndex();
            ProductFamily pf = pft.getItems().get(index);
            pf.setName(pfChanged.getName());
            pf.setResponsible((pfChanged.getResponsible()));
           new ProductFamiliesDB().updateData(pf);

            FamiliesEditorWindow.getStage().close();
        } else {

        }

        CoreModule.getProductLgbks().getProductLgbksTable().getTableView().refresh();
    }

    public void actionFamilyEditorClose() {
        FamiliesEditorWindow.getStage().close();
    }

}
