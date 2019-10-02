package ui_windows.options_window.price_lists_editor.se;

import core.CoreModule;
import javafx.stage.Modality;
import ui_windows.Mode;
import ui_windows.OrdinalWindow;
import ui_windows.options_window.OptionsWindow;
import ui_windows.options_window.price_lists_editor.PriceList;

import static ui_windows.Mode.ADD;
import static ui_windows.Mode.EDIT;

public class PriceListEditorWindow extends OrdinalWindow {
    public PriceListEditorWindow(Mode mode) {
        super(OptionsWindow.getStage(), Modality.APPLICATION_MODAL, mode,
                "priceListEditorWindowv2.fxml", "Прайс лист");

        PriceListEditorWindowControllerv2 controller = (PriceListEditorWindowControllerv2) loader.getController();

        PriceList tempPriceList = null;
        if (mode == ADD) {
            tempPriceList = new PriceList();
        } else if (mode == EDIT) {//put data into fields
            PriceList selectedItem = CoreModule.getPriceLists().getPriceListsTable().getSelectedItem();

            tempPriceList = new PriceList(selectedItem);
            tempPriceList.getSheets().addAll(selectedItem.getSheets());
        }

        tempPriceList.showInEditorWindow(controller);
        controller.setTempPriceList(tempPriceList);

        stage.show();
    }
}
