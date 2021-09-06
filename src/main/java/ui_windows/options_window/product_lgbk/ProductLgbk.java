package ui_windows.options_window.product_lgbk;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.AnchorPane;
import ui_windows.options_window.families_editor.ProductFamilies;
import ui_windows.options_window.price_lists_editor.se.PriceListContentItem;
import ui_windows.options_window.price_lists_editor.se.PriceListContentTableItem;
import ui_windows.options_window.requirements_types_editor.RequirementTypes;
import ui_windows.product.Product;
import utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class ProductLgbk implements PriceListContentItem {
    public static final int ROOT_NODE = 0;
    public static final int GROUP_NODE = 1;
    public static final int ITEM_NODE = 2;
    private int id;
    private StringProperty lgbk;
    private StringProperty hierarchy;
    private StringProperty description_en = new SimpleStringProperty("");
    private StringProperty description_ru = new SimpleStringProperty("");
    private NormsList normsList = new NormsList("");
    private int familyId = -1;
    private boolean isNotUsed;
    private int nodeType = -1;

    public ProductLgbk(String lgbk) {
        this.lgbk = new SimpleStringProperty(lgbk);
    }

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
        familyId = familyValue.length() > 0 ? ProductFamilies.getInstance().getFamilyIdByName(familyValue) : -1;

        isNotUsed = Utils.getControlValue(root, "ckbNotUsed") == "true" ? true : false;
        normsList = new NormsList(Utils.getControlValue(root, "lvSelectedNorms"));
    }

    public ProductLgbk(Product product) {
        lgbk = new SimpleStringProperty(product.getLgbk() == null ? "nnn" : product.getLgbk());
        hierarchy = new SimpleStringProperty(product.getHierarchy() == null ? "nnn" : product.getHierarchy());
        normsList = new NormsList("");
        nodeType = ITEM_NODE;
    }

    public void showInEditorWindow(AnchorPane root) {
        Utils.setControlValue(root, "tfLgbk", getLgbk());
        Utils.setControlValue(root, "tfHierarchy", getHierarchy());
        Utils.setControlValue(root, "tfDescriptionEn", getDescription_en());
        Utils.setControlValue(root, "tfDescriptionRu", getDescription_ru());
        if (familyId >= 0)
            Utils.setControlValue(root, "cbFamily", ProductFamilies.getInstance().getFamilyNameById(familyId));
        Utils.setControlValue(root, "ckbNotUsed", isNotUsed);

        ((LgbkEditorWindowController) LgbkEditorWindow.getLoader().getController()).ckbNotUsed.setDisable(nodeType == ROOT_NODE);

        String line = normsList.getStringLine();
        ArrayList<String> selectedNorms = RequirementTypes.getInstance().getRequirementsList(line);
        ArrayList<String> allNorms = RequirementTypes.getInstance().getAllRequirementTypesShortNames();
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

    public String getCombineDescriptionLgbk() {
        return String.format("[%s] %s", getLgbk(), getDescriptionRuEn());
    }

    public String getCombineDescriptionHierarchy() {
        return String.format("[%s] %s", getHierarchy(), getDescriptionRuEn());
    }

    public String getCombineLgbkDescription() {
        String lgbkName = "";
        LgbkAndParent lap = ProductLgbkGroups.getInstance().getLgbkAndParent(this);
        if (lap != null && lap.getLgbkParent() != null) {
            lgbkName = lap.getLgbkParent().getLgbk();
        }

        return String.format("[%s] %s", lgbkName, getDescriptionRuEn());
    }

    public String getCombineHierarchyDescription() {
        return String.format("[%s] %s", getHierarchy(), getDescriptionRuEn());
    }

    @Override
    public String toString() {
        return String.format("%s / %s", getLgbk(), getHierarchy());
    }

    public boolean compare(ProductLgbk anotherInstance) {
        String currLgbk = lgbk.get();
        String anotherLgbk = anotherInstance.getLgbk();
        String currHierarchy = hierarchy.get().replaceAll("\\.", "");
        String anotherHierarchy = anotherInstance.getHierarchy().replaceAll("\\.", "");

        if (currLgbk.equals(anotherLgbk) &&
                (currHierarchy.contains(anotherHierarchy) || anotherHierarchy.contains(currHierarchy))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductLgbk)) return false;
        ProductLgbk that = (ProductLgbk) o;
        return Objects.equals(lgbk.getValue(), that.lgbk.getValue()) && Objects.equals(hierarchy.getValue(), that.hierarchy.getValue());
    }

    @Override
    public int hashCode() {
        return lgbk.getValue().concat("_").concat(hierarchy.getValue()).hashCode();
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

    /*@Override
    public String toString() {
        return id + ", " + lgbk.getValue() + ", " + hierarchy.getValue();
    }*/

    @Override
    public PriceListContentTableItem getTableItem() {
        return new PriceListContentTableItem(this);
    }
}
