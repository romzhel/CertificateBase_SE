package ui_windows.options_window.order_accessibility_editor;

import javafx.stage.Modality;
import ui_windows.Mode;
import ui_windows.OrdinalWindow;
import ui_windows.options_window.OptionsWindow;
import ui_windows.options_window.user_editor.Users;

import static ui_windows.Mode.EDIT;

public class OrdersAccessibilityEditorWindow extends OrdinalWindow {

    public OrdersAccessibilityEditorWindow(Mode editorMode) {
        super(OptionsWindow.getStage(), Modality.APPLICATION_MODAL,
                editorMode, "/fxml/ordersAccessibilityEditorWindow.fxml", "Доступность заказа");

        if (mode == EDIT) {//put data into fields
            OrdersAccessibility.getInstance().getTable().getTableView().getSelectionModel().getSelectedItem()
                    .showInEditorWindow(rootAnchorPane);
        }

        applyProfileSimple(Users.getInstance().getCurrentUser().getProfile().getOrderAccessible());

        stage.show();
    }
}
