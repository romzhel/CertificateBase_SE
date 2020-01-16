package ui_windows.options_window.order_accessibility_editor;

import javafx.scene.layout.AnchorPane;
import utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderAccessibility {
    private int id;
    private String statusCode;
    private String sesCode;
    private String descriptionEn;
    private String descriptionRu;
    private String f1;
    private String f2;
    private String status;
    private boolean orderable;

    public OrderAccessibility() {
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
        Utils.setControlValue(root, "tfStatusCode", getStatusCode());
        Utils.setControlValue(root, "tfSesCode", getSesCode());
        Utils.setControlValue(root, "tfDescriptionEn", getDescriptionEn());
        Utils.setControlValue(root, "tfDescriptionRu", getDescriptionRu());
        Utils.setControlValue(root, "tf1", getF1());
        Utils.setControlValue(root, "tf2", getF2());
        Utils.setControlValue(root, "tfStatus", getStatus());
        Utils.setControlValue(root, "cbxOrderable", isOrderable());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getSesCode() {
        return sesCode;
    }

    public void setSesCode(String sesCode) {
        this.sesCode = sesCode;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionRu() {
        return descriptionRu;
    }

    public void setDescriptionRu(String descriptionRu) {
        this.descriptionRu = descriptionRu;
    }

    public String getF1() {
        return f1;
    }

    public void setF1(String f1) {
        this.f1 = f1;
    }

    public String getF2() {
        return f2;
    }

    public void setF2(String f2) {
        this.f2 = f2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isOrderable() {
        return orderable;
    }

    public void setOrderable(boolean orderable) {
        this.orderable = orderable;
    }

    public String getDescription() {
        if (descriptionRu != null && !descriptionRu.isEmpty()) {
            return descriptionRu;
        } else if (descriptionEn != null && !descriptionEn.isEmpty()) {
            return descriptionEn;
        }
        return "";
    }
}
