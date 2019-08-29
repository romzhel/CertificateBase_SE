package ui_windows.options_window.product_lgbk;

import core.CoreModule;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.AnchorPane;
import utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductLgbk {
    static final int ROOT_NODE = 0;
    static final int GROUP_NODE = 1;
    static final int ITEM_NODE = 2;
    private int id;
    private StringProperty lgbk;
    private StringProperty hierarchy;
    private StringProperty description_en = new SimpleStringProperty("");
    private StringProperty description_ru = new SimpleStringProperty("");
    private NormsList normsList;
    private int familyId = -1;
    private boolean isNotUsed;
    private int nodeType = -1;


    public ProductLgbk(String lgbk, String hierarchy) {
        this.lgbk = new SimpleStringProperty(lgbk);
        this.hierarchy = new SimpleStringProperty(hierarchy);
        nodeType = ITEM_NODE;
    }

    public ProductLgbk(String lgbk, String hierarchy, int nodeType) {
        this.lgbk = new SimpleStringProperty(lgbk);
        this.hierarchy = new SimpleStringProperty(hierarchy);
        this.nodeType = nodeType;
    }

    public ProductLgbk(String lgbk, String hierarchy, String description_en, String description_ru, int familyId,
                       boolean isNotUsed) {
        id = 0;
        this.lgbk = new SimpleStringProperty(lgbk);
        this.hierarchy = new SimpleStringProperty(hierarchy);
        this.description_en = new SimpleStringProperty(description_en);
        this.description_ru = new SimpleStringProperty(description_ru);
        this.familyId = familyId;
        this.isNotUsed = isNotUsed;
        nodeType = ITEM_NODE;
    }

    public ProductLgbk(ResultSet rs) {
        try {
            id = rs.getInt("id");
            lgbk = new SimpleStringProperty(rs.getString("lgbk"));
            hierarchy = new SimpleStringProperty(rs.getString("hierarchy"));
            description_en = new SimpleStringProperty(rs.getString("description_en"));
            description_ru = new SimpleStringProperty(rs.getString("description_ru"));
            familyId = rs.getInt("family_id");
            isNotUsed = rs.getBoolean("not_used");
            nodeType = rs.getInt("node_type");
            normsList = new NormsList(rs.getString("norms_list"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ProductLgbk(AnchorPane root) {
        lgbk = new SimpleStringProperty(Utils.getControlValue(root, "tfLgbk"));
        hierarchy = new SimpleStringProperty(Utils.getControlValue(root, "tfHierarchy"));
        description_en = new SimpleStringProperty(Utils.getControlValue(root, "tfDescriptionEn"));
        description_ru = new SimpleStringProperty(Utils.getControlValue(root, "tfDescriptionRu"));

        String familyValue = Utils.getControlValue(root, "cbFamily").trim();
        familyId = familyValue.length() > 0 ? CoreModule.getProductFamilies().getFamilyIdByName(familyValue) : -1;

        isNotUsed = Utils.getControlValue(root, "ckbNotUsed") == "true" ? true : false;
        normsList = new NormsList(Utils.getControlValue(root, "lvSelectedNorms"));
    }

    public void showInEditorWindow(AnchorPane root) {
        Utils.setControlValue(root, "tfLgbk", getLgbk());
        Utils.setControlValue(root, "tfHierarchy", getHierarchy());
        Utils.setControlValue(root, "tfDescriptionEn", getDescription_en());
        Utils.setControlValue(root, "tfDescriptionRu", getDescription_ru());
        if (familyId >= 0)
            Utils.setControlValue(root, "cbFamily", CoreModule.getProductFamilies().getFamilyNameById(familyId));
        Utils.setControlValue(root, "ckbNotUsed", isNotUsed);

        String line = normsList.getStringLine();
        ArrayList<String> selectedNorms = CoreModule.getRequirementTypes().getRequirementsList(line);
        ArrayList<String> allNorms = CoreModule.getRequirementTypes().getAllRequirementTypesShortNames();
        if (selectedNorms != null) allNorms.removeAll(selectedNorms);

//        Utils.setControlValue(root, "lvAllNorms", allNorms);
//        Utils.setControlValue(root, "lvSelectedNorms", selectedNorms);
    }

    public String getDescription() {
        if (description_ru.getValue() == null || description_ru.getValue().isEmpty()) {
            if (description_en.getValue() == null || description_en.getValue().isEmpty()) {
                return "";
            } else {
                return description_en.getValue();
            }
        } else {
            return description_ru.getValue();
        }
    }

    public String getDescriptionRuEn() {
        return getDescription();
    }

    public String getDescriptionEnRu() {
        if (description_en.getValue() == null || description_en.getValue().isEmpty()) {
            if (description_ru.getValue() == null || description_ru.getValue().isEmpty()) {
                return "";
            } else {
                return description_ru.getValue();
            }
        } else {
            return description_en.getValue();
        }
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

    public String getDescription_en() {
        return description_en.get();
    }

    public StringProperty description_enProperty() {
        return description_en;
    }

    public void setDescription_en(String description_en) {
        this.description_en.set(description_en);
    }

    public String getDescription_ru() {
        return description_ru.get();
    }

    public StringProperty description_ruProperty() {
        return description_ru;
    }

    public void setDescription_ru(String description_ru) {
        this.description_ru.set(description_ru);
    }

    public NormsList getNormsList() {
        return normsList;
    }

    public void setNormsList(NormsList normsList) {
        this.normsList = normsList;
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

    public int getNodeType() {
        return nodeType;
    }

    public void setNodeType(int nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    public String toString() {
        return id + ", " + lgbk.getValue() + ", " + hierarchy.getValue();
    }
}
