package ui_windows.options_window.order_accessibility_editor;

import core.CoreModule;
import javafx.stage.Modality;
import ui_windows.OrdinalWindow;
import ui_windows.options_window.OptionsWindow;
import ui_windows.Mode;

import static ui_windows.Mode.*;

public class OrdersAccessibilityEditorWindow extends OrdinalWindow {

    public OrdersAccessibilityEditorWindow(Mode editorMode) {
        super(OptionsWindow.getStage(), Modality.APPLICATION_MODAL,
                editorMode, "ordersAccessibilityEditorWindow.fxml", "Доступность заказа");

        if (mode == EDIT) {//put data into fields
            CoreModule.getOrdersAccessibility().getTable().getTableView().getSelectionModel().getSelectedItem()
                    .showInEditorWindow(rootAnchorPane);
        }

        applyProfileSimple(CoreModule.getUsers().getCurrentUser().getProfile().getOrderAccessible());

        stage.show();
    }
}
