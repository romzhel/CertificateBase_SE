package ui_windows.options_window.order_accessibility_editor;

import javafx.scene.layout.AnchorPane;
import lombok.Data;
import utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class OrderAccessibility {
    private int id;
    private String statusCode;
    private String alternativeStatusCode;
    private String sesCode;
    private String descriptionEn;
    private String descriptionRu;
    private String f1;
    private String f2;
    private String status;
    private boolean orderable;

    public OrderAccessibility(String statusCode, String alternativeStatusCode, String descriptionEn, String descriptionRu) {
        this.statusCode = statusCode;
        this.alternativeStatusCode = alternativeStatusCode;
        this.descriptionEn = descriptionEn;
        this.descriptionRu = descriptionRu;
    }

    public OrderAccessibility(ResultSet rs) {
        try {
            id = rs.getInt("id");
            statusCode = rs.getString("status_code");
            sesCode = rs.getString("ses_code");
            descriptionEn = rs.getString("description_en");
            descriptionRu = rs.getString("description_ru");
            f1 = rs.getString("f1");
            f2 = rs.getString("f2");
            status = rs.getString("status");
            orderable = rs.getBoolean("orderable");
            alternativeStatusCode = rs.getString("alt_status_code");
        } catch (SQLException e) {
            System.out.println("exception orderAccessible constructor");
        }
    }

    public OrderAccessibility(AnchorPane ap) {
        AnchorPane root = (AnchorPane) OrdersAccessibilityEditorWindow.getStage().getScene().getRoot();

        id = 0;
        statusCode = Utils.getControlValue(root, "tfStatusCode");
        sesCode = Utils.getControlValue(root, "tfSesCode");
        descriptionEn = Utils.getControlValue(root, "tfDescriptionEn");
        descriptionRu = Utils.getControlValue(root, "tfDescriptionRu");
        f1 = Utils.getControlValue(root, "tf1");
        f2 = Utils.getControlValue(root, "tf2");
        status = Utils.getControlValue(root, "tfStatus");
        orderable = Utils.getControlValue(root, "cbxOrderable") == "true" ? true : false;
    }

    public void showInEditorWindow(AnchorPane root) {
        Utils.setControlValue(root, "tfStatusCode", statusCode);
        Utils.setControlValue(root, "tfSesCode", sesCode);
        Utils.setControlValue(root, "tfDescriptionEn", descriptionEn);
        Utils.setControlValue(root, "tfDescriptionRu", descriptionRu);
        Utils.setControlValue(root, "tf1", f1);
        Utils.setControlValue(root, "tf2", f2);
        Utils.setControlValue(root, "tfStatus", status);
        Utils.setControlValue(root, "cbxOrderable", orderable);
    }

    public String getDescription() {
        if (descriptionRu != null && !descriptionRu.isEmpty()) {
            return descriptionRu;
        } else if (descriptionEn != null && !descriptionEn.isEmpty()) {
            return descriptionEn;
        }
        return "";
    }

    @Override
    public String toString() {
        return String.format("(%s) %s", statusCode, getDescription());
    }
}
