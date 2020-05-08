package ui_windows.options_window.order_accessibility_editor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import utils.Utils;
import utils.comparation.products.ObjectsComparator;

import java.net.URL;
import java.util.ResourceBundle;

import static ui_windows.Mode.ADD;
import static ui_windows.Mode.EDIT;

public class OrdersAccessibilityEditorWindowController implements Initializable {

    @FXML
    TextField tf1;

    @FXML
    TextField tf1Comment;

    @FXML
    TextField tf2;

    @FXML
    TextField tf2Comment;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tf1.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("0")) {
                tf1Comment.setText("EDI transmission of the CSK-file (Record 12) - no transmission");
            } else if (newValue.equals("1")) {
                tf1Comment.setText("EDI transmission of the CSK-file (Record 12) - transmission");
            } else tf1Comment.setText("");
        });

        tf2.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("0")) {
                tf2Comment.setText("Order transmission - no order possible");
            } else if (newValue.equals("1")) {
                tf2Comment.setText("Order transmission - order via EDI (Record 21)");
            } else if (newValue.equals("2")) {
                tf2Comment.setText("Order transmission - order via fax");
            } else tf2Comment.setText("");
        });
    }

    public void actionApply() {
        AnchorPane root = OrdersAccessibilityEditorWindow.getRootAnchorPane();

        if (Utils.hasEmptyControls(root, "tfSesCode", "tfDescriptionRu", "tf1Comment", "tf2Comment", "tfStatus"))
            return;


        if (OrdersAccessibilityEditorWindow.getMode() == ADD) {
            OrdersAccessibility.getInstance().addItem(
                    new OrderAccessibility((AnchorPane) OrdersAccessibilityEditorWindow.getStage().getScene().getRoot()));

        } else if (OrdersAccessibilityEditorWindow.getMode() == EDIT) {
            TableView<OrderAccessibility> oat = OrdersAccessibility.getInstance().getTable().getTableView();
            int index = oat.getSelectionModel().getSelectedIndex();
            OrderAccessibility oa = oat.getItems().get(index);
            OrderAccessibility changedOa = new OrderAccessibility((AnchorPane) OrdersAccessibilityEditorWindow.getStage().getScene().getRoot());

            ObjectsComparator oc = new ObjectsComparator(oa, changedOa, false, "id");
            if (oc.getResult().isNeedUpdateInDB()) OrdersAccessibility.getInstance().updateItem(oa);
        }
        actionClose();
    }

    public void actionClose() {
        OrdersAccessibility.getInstance().getTable().getTableView().refresh();
        OrdersAccessibilityEditorWindow.getStage().close();
    }
}
