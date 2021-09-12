package ui_windows.options_window.product_lgbk;

import javafx.scene.layout.AnchorPane;
import lombok.Data;
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

@Data
public class ProductLgbk implements PriceListContentItem {
    public static final int ROOT_NODE = 0;
    public static final int GROUP_NODE = 1;
    public static final int ITEM_NODE = 2;
    private int id;
    private String lgbk;
    private String hierarchy;
    private String description_en = "";
    private String description_ru = "";
    private NormsList normsList = new NormsList("");
    private int familyId = -1;
    private boolean isNotUsed;
    private int nodeType = -1;

    public ProductLgbk(String lgbk) {
        this.lgbk = lgbk;
    }

    public ProductLgbk(String lgbk, String hierarchy) {
        this.lgbk = lgbk;
        this.hierarchy = hierarchy;
        nodeType = ITEM_NODE;
    }

    public ProductLgbk(String lgbk, String hierarchy, int nodeType) {
        this.lgbk = lgbk;
        this.hierarchy = hierarchy;
        this.nodeType = nodeType;
    }

    public ProductLgbk(String lgbk, String hierarchy, String description_en, String description_ru, int familyId,
                       boolean isNotUsed) {
        id = 0;
        this.lgbk = lgbk;
        this.hierarchy = hierarchy;
        this.description_en = description_en;
        this.description_ru = description_ru;
        this.familyId = familyId;
        this.isNotUsed = isNotUsed;
        nodeType = ITEM_NODE;
    }

    public ProductLgbk(ResultSet rs) {
        try {
            id = rs.getInt("id");
            lgbk = rs.getString("lgbk");
            hierarchy = rs.getString("hierarchy");
            description_en = rs.getString("description_en");
            description_ru = rs.getString("description_ru");
            familyId = rs.getInt("family_id");
            isNotUsed = rs.getBoolean("not_used");
            nodeType = rs.getInt("node_type");
            normsList = new NormsList(rs.getString("norms_list"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ProductLgbk(AnchorPane root) {
        lgbk = Utils.getControlValue(root, "tfLgbk");
        hierarchy = Utils.getControlValue(root, "tfHierarchy");
        description_en = Utils.getControlValue(root, "tfDescriptionEn");
        description_ru = Utils.getControlValue(root, "tfDescriptionRu");

        String familyValue = Utils.getControlValue(root, "cbFamily").trim();
        familyId = familyValue.length() > 0 ? ProductFamilies.getInstance().getFamilyIdByName(familyValue) : -1;

        isNotUsed = Utils.getControlValue(root, "ckbNotUsed") == "true" ? true : false;
        normsList = new NormsList(Utils.getControlValue(root, "lvSelectedNorms"));
    }

    public ProductLgbk(Product product) {
        lgbk = product.getLgbk() == null ? "nnn" : product.getLgbk();
        hierarchy = product.getHierarchy() == null ? "nnn" : product.getHierarchy();
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
        if (description_ru == null || description_ru.isEmpty()) {
            if (description_en == null || description_en.isEmpty()) {
                return "";
            } else {
                return description_en;
            }
        } else {
            return description_ru;
        }
    }

    public String getDescriptionRuEn() {
        return getDescription();
    }

    public String getDescriptionEnRu() {
        if (description_en == null || description_en.isEmpty()) {
            if (description_ru == null || description_ru.isEmpty()) {
                return "";
            } else {
                return description_ru;
            }
        } else {
            return description_en;
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
        String currLgbk = lgbk;
        String anotherLgbk = anotherInstance.getLgbk();
        String currHierarchy = hierarchy.replaceAll("\\.", "");
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
        return Objects.equals(lgbk, that.lgbk) && Objects.equals(hierarchy, that.hierarchy);
    }

    @Override
    public int hashCode() {
        return lgbk.concat("_").concat(hierarchy).hashCode();
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
