package ui_windows.options_window.price_lists_editor.se;

import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.options_window.OptionsWindow;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.user_editor.Users;

public class PriceListEditorWindow extends OrdinalWindow {
    public PriceListEditorWindow(PriceList editedPriceList) {
        super(OptionsWindow.getStage(), Modality.APPLICATION_MODAL, mode,
                "priceListEditorWindowv2.fxml", "Прайс лист");

        PriceListEditorWindowControllerv2 controller = (PriceListEditorWindowControllerv2) loader.getController();
        editedPriceList.showInEditorWindow(controller);
        controller.setTempPriceList(editedPriceList);

        Users.getInstance().getCurrentUser().getProfile().getPriceLists().apply(controller.btnApply);

        stage.show();
    }
}
