package ui_windows.options_window.product_lgbk;

import core.CoreModule;
import database.Request;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.AnchorPane;
import ui_windows.main_window.Product;
import utils.Utils;

import javax.rmi.CORBA.Util;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductLgbk {
    private int id;
    private StringProperty lgbk;
    private StringProperty hierarchy;
    private StringProperty description = new SimpleStringProperty("");
    private int familyId = -1;
    private boolean isNotUsed;


    public ProductLgbk(String lgbk, String hierarchy){
        this.lgbk = new SimpleStringProperty(lgbk);
        this.hierarchy = new SimpleStringProperty(hierarchy);
    }

    public ProductLgbk(String lgbk, String hierarchy, String description, int familyId, boolean isNotUsed) {
        id = 0;
        this.lgbk = new SimpleStringProperty(lgbk);
        this.hierarchy = new SimpleStringProperty(hierarchy);
        this.description = new SimpleStringProperty(description);
        this.familyId = familyId;
        this.isNotUsed = isNotUsed;
    }

    public ProductLgbk(ResultSet rs){
        try {
            id = rs.getInt("id");
            lgbk = new SimpleStringProperty(rs.getString("lgbk"));
            hierarchy = new SimpleStringProperty(rs.getString("hierarchy"));
            description = new SimpleStringProperty(rs.getString("description"));
            familyId = rs.getInt("family_id");
            isNotUsed = rs.getBoolean("not_used");
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public ProductLgbk(AnchorPane root){
        lgbk = new SimpleStringProperty(Utils.getControlValue(root, "tfLgbk"));
        hierarchy = new SimpleStringProperty(Utils.getControlValue(root, "tfHierarchy"));
        description = new SimpleStringProperty(Utils.getControlValue(root, "tfDescription"));

        String familyValue = Utils.getControlValue(root, "cbFamily").trim();
        familyId = familyValue.length() > 0 ? CoreModule.getProductFamilies().getFamilyIdByName(familyValue) : -1;

        isNotUsed = Utils.getControlValue(root, "ckbNotUsed") == "true" ? true : false;
    }

    public void showInEditorWindow(AnchorPane root){
        Utils.setControlValue(root, "tfLgbk", getLgbk());
        Utils.setControlValue(root, "tfHierarchy", getHierarchy());
        Utils.setControlValue(root, "tfDescription", getDescription());
        if (familyId >= 0) Utils.setControlValue(root, "cbFamily", CoreModule.getProductFamilies().getFamilyNameById(familyId));
        Utils.setControlValue(root, "ckbNotUsed", isNotUsed);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLgbk() {
        return lgbk.get();
    }

    public StringProperty nameProperty() {
        return lgbk;
    }

    public void setName(String name) {
        this.lgbk.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public int getFamilyId() {
        return familyId;
    }

    public void setFamilyId(int familyId) {
        this.familyId = familyId;
    }

    public boolean isNotUsed() {
        return isNotUsed;
    }

    public void setNotUsed(boolean notUsed) {
        isNotUsed = notUsed;
    }

    public StringProperty lgbkProperty() {
        return lgbk;
    }

    public void setLgbk(String lgbk) {
        this.lgbk.set(lgbk);
    }

    public String getHierarchy() {
        return hierarchy.get();
    }

    public StringProperty hierarchyProperty() {
        return hierarchy;
    }

    public void setHierarchy(String hierarchy) {
        this.hierarchy.set(hierarchy);
    }
}
