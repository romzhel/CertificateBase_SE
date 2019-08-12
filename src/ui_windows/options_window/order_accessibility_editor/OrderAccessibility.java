package ui_windows.options_window.order_accessibility_editor;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.AnchorPane;
import utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderAccessibility {
    private int id;
    private StringProperty statusCode;
    private StringProperty sesCode;
    private StringProperty descriptionEn;
    private StringProperty descriptionRu;
    private StringProperty f1;
    private StringProperty f2;
    private StringProperty status;
    private boolean orderable;

    public OrderAccessibility(){}

    public OrderAccessibility(ResultSet rs) {
        try {
            id = rs.getInt("id");
            statusCode = new SimpleStringProperty(rs.getString("status_code"));
            sesCode = new SimpleStringProperty(rs.getString("ses_code"));
            descriptionEn = new SimpleStringProperty(rs.getString("description_en"));
            descriptionRu = new SimpleStringProperty(rs.getString("description_ru"));
            f1 = new SimpleStringProperty(rs.getString("f1"));
            f2 = new SimpleStringProperty(rs.getString("f2"));
            status = new SimpleStringProperty(rs.getString("status"));
            orderable = rs.getBoolean("orderable");
        } catch (SQLException e) {
            System.out.println("exception orderAccessible constructor");
        }
    }

    public OrderAccessibility(AnchorPane ap) {
        AnchorPane root = (AnchorPane) OrdersAccessibilityEditorWindow.getStage().getScene().getRoot();

        id = 0;
        statusCode = new SimpleStringProperty(Utils.getControlValue(root, "tfStatusCode"));
        sesCode = new SimpleStringProperty(Utils.getControlValue(root, "tfSesCode"));
        descriptionEn = new SimpleStringProperty(Utils.getControlValue(root, "tfDescriptionEn"));
        descriptionRu = new SimpleStringProperty(Utils.getControlValue(root, "tfDescriptionRu"));
        f1 = new SimpleStringProperty(Utils.getControlValue(root, "tf1"));
        f2 = new SimpleStringProperty(Utils.getControlValue(root, "tf2"));
        status = new SimpleStringProperty(Utils.getControlValue(root, "tfStatus"));
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
        return statusCode.get();
    }

    public void setStatusCode(String statusCode) {
        this.statusCode.set(statusCode);
    }

    public StringProperty statusCodeProperty() {
        return statusCode;
    }

    public String getSesCode() {
        return sesCode.get();
    }

    public void setSesCode(String sesCode) {
        this.sesCode.set(sesCode);
    }

    public StringProperty sesCodeProperty() {
        return sesCode;
    }

    public String getDescriptionEn() {
        return descriptionEn.get();
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn.set(descriptionEn);
    }

    public StringProperty descriptionEnProperty() {
        return descriptionEn;
    }

    public String getDescriptionRu() {
        return descriptionRu.get();
    }

    public void setDescriptionRu(String descriptionRu) {
        this.descriptionRu.set(descriptionRu);
    }

    public StringProperty descriptionRuProperty() {
        return descriptionRu;
    }

    public String getF1() {
        return f1.get();
    }

    public void setF1(String f1) {
        this.f1.set(f1);
    }

    public StringProperty f1Property() {
        return f1;
    }

    public String getF2() {
        return f2.get();
    }

    public void setF2(String f2) {
        this.f2.set(f2);
    }

    public StringProperty f2Property() {
        return f2;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public boolean isOrderable() {
        return orderable;
    }

    public void setOrderable(boolean orderable) {
        this.orderable = orderable;
    }
}
