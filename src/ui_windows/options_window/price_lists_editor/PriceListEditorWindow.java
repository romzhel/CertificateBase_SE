package ui_windows.options_window.price_lists_editor;

import core.CoreModule;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import ui_windows.Mode;
import ui_windows.OrdinalWindow;
import ui_windows.options_window.OptionsWindow;

import static ui_windows.Mode.*;

public class PriceListEditorWindow extends OrdinalWindow {

    public PriceListEditorWindow(Mode editorMode) {
        super(OptionsWindow.getStage(), Modality.APPLICATION_MODAL, editorMode,
                "priceListEditorWindow.fxml", "Редактор прайс-листа");

        if (mode == ADD) {

        } else if (mode == EDIT) {//put data into fields

            TableView<PriceList> plt = CoreModule.getPriceLists().getPriceListsTable().getTableView();
            PriceList selectedItem = plt.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                selectedItem.showInEditorWindow(rootAnchorPane);
            }

        }

        stage.show();
    }
}
