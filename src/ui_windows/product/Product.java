package ui_windows.product;

import core.CoreModule;
import database.se.DBSynced;
import database.se.DbFieldIgnore;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import ui_windows.main_window.file_import_window.ColumnsMapper;
import ui_windows.main_window.file_import_window.RowData;
import ui_windows.main_window.filter_window.Filter;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;
import ui_windows.options_window.product_lgbk.LgbkAndParent;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import utils.Countries;
import utils.Utils;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static ui_windows.main_window.filter_window.FilterParameters.FILTER_ALL_ITEMS;
import static ui_windows.main_window.filter_window.FilterParameters.FILTER_PRICE_ITEMS;
import static ui_windows.product.data.ProductProperties.*;

public class Product implements DBSynced {
    @DbFieldIgnore
    public static final String NO_DATA = "нет данных";
    private int id;
    private String material;
    private String productForPrint;
    private String article;
    private String hierarchy;
    private String lgbk;
    private String endOfService;
    private String dangerous;
    private String country;
    private String dchain;
    private String descriptionRu = "";
    private String descriptionEn = "";
    private boolean price = true;
    private String lastImportCodes = "";

    private int family;
    private int type_id;
    private int productLine_id;
    private String history = "";
    private String lastChangeDate = "";
    private String fileName = "";
    private String comments = "";
    private String replacement = "";
    private NormsList normsList;
    private int normsMode = NormsList.ADD_TO_GLOBAL;
    private int minOrder;
    private int packetSize;
    private int leadTime;
    private double weight;
    private double localPrice;

    public Product() {
    }

    public Product(AnchorPane root) {
        id = 0;
        material = Utils.getControlValue(root, "tfMaterial");
        productForPrint = (Utils.getControlValue(root, "tfProductPrint"));
        article = Utils.getControlValue(root, "tfArticle");
        family = CoreModule.getProductFamilies().getFamilyIdByName(Utils.getControlValue(root, "cbFamily"));
        descriptionRu = Utils.getControlValue(root, "taDescription");
        descriptionEn = Utils.getControlValue(root, "taDescriptionEn");
        price = Utils.getControlValue(root, "cbxPrice") == "true";
        lastImportCodes = "";

        type_id = CoreModule.getProductTypes().getIDbyType(Utils.getControlValue(root, "cbType"));
        history = Utils.getControlValue(root, "lHistory");
        lastChangeDate = "";
        comments = Utils.getControlValue(root, "taComments");
        replacement = Utils.getControlValue(root, "tfReplacement");
    }

    public Product(RowData rowData, ColumnsMapper mapper) {
        String cellValue;
        id = 0;
        material = rowData.get(mapper.getFieldIndexByName(DESC_ORDER_NUMBER)).replaceAll("\\,", ".");
        productForPrint = rowData.get(mapper.getFieldIndexByName(DESC_ORDER_NUMBER_PRINT)).replaceAll("\\,", ".");
        article = rowData.get(mapper.getFieldIndexByName(DESC_ARTICLE)).replaceAll("\\,", ".");
        hierarchy = rowData.get(mapper.getFieldIndexByName(DESC_HIERARCHY));
        lgbk = rowData.get(mapper.getFieldIndexByName(DESC_LGBK));

        cellValue = rowData.get(mapper.getFieldIndexByName(DESC_SERVICE_END)).replaceAll("\\,", ".");
        endOfService = cellValue.matches("00.00.0000") ? "" : cellValue;
        dangerous = rowData.get(mapper.getFieldIndexByName(DESC_LOGISTIC_LIMITATION));
        country = rowData.get(mapper.getFieldIndexByName(DESC_COUNTRY));
        dchain = rowData.get(mapper.getFieldIndexByName(DESC_DCHAIN));

        price = false;
        descriptionRu = rowData.get(mapper.getFieldIndexByName(DESC_DESCRIPTION_RU));
        descriptionEn = rowData.get(mapper.getFieldIndexByName(DESC_DESCRIPTION_EN));

        normsList = new NormsList(new ArrayList<Integer>());

        minOrder = (int) getDoubleFromString(rowData.get(mapper.getFieldIndexByName(DESC_MIN_ORDER)));
        packetSize = (int) getDoubleFromString(rowData.get(mapper.getFieldIndexByName(DESC_PACKSIZE)));
        leadTime = (int) getDoubleFromString(rowData.get(mapper.getFieldIndexByName(DESC_LEADTIME)));
        weight = getDoubleFromString(rowData.get(mapper.getFieldIndexByName(DESC_WEIGHT)));
        localPrice = getDoubleFromString(rowData.get(mapper.getFieldIndexByName(DESC_LOCAL_PRICE)));
    }

    public Product(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        material = rs.getString("material");
        productForPrint = rs.getString("product_print");
        article = rs.getString("article");
        hierarchy = rs.getString("hierarchy");
        lgbk = rs.getString("lgbk");
        family = rs.getInt("family");
        endOfService = rs.getString("end_of_service");
        dangerous = rs.getString("dangerous");
        country = rs.getString("country");
        dchain = rs.getString("dchain");

        descriptionRu = nullToEmpty(rs.getString("description_ru"));
        descriptionEn = nullToEmpty(rs.getString("description_en"));
        price = rs.getBoolean("price");

        history = rs.getString("history");
        lastChangeDate = rs.getString("last_change_date");
        fileName = rs.getString("file_name");
        comments = rs.getString("comments");
        replacement = rs.getString("replacement");

        type_id = rs.getInt("type_id");
        lastImportCodes = rs.getString("last_import_codes") == null ? "" : rs.getString("last_import_codes");

        normsList = new NormsList(rs.getString("norms_list"));
        normsMode = rs.getInt("norms_mode");

        minOrder = rs.getInt("min_order");
        packetSize = rs.getInt("packet_size");
        leadTime = rs.getInt("lead_time");
        weight = rs.getDouble("weight");
        localPrice = rs.getDouble("local_price");
    }

    private double getDoubleFromString(String text) {
        if (text == null || text.isEmpty()) return 0;
        try {
            return Double.parseDouble(text);
        } catch (Exception e) {
            System.out.println(article + ", bad double: " + text);
            return 0.0;
        }
    }

    public String getOrderableStatus() {
        OrderAccessibility oa = CoreModule.getOrdersAccessibility().getOrderAccessibilityByStatusCode(getDchain());
        String result = "";

        if (oa != null) {
            if (oa.getStatus().trim().length() > 0) result = oa.getStatus();
            else {
                if (oa.getDescriptionRu().trim().length() > 0) result = oa.getDescriptionRu();
                else {
                    if (oa.getDescriptionEn().trim().length() > 0) result = oa.getDescriptionEn();
                }
            }

            if (result.length() > 0) result = "(" + getDchain() + ") " + result;
            else result = "(" + getDchain() + ")";

        } else {
            if (getDchain().trim().length() > 0) result = "(" + getDchain() + ")";
        }

        return result;
    }

    public void displayInEditorWindow(AnchorPane root) {
        Utils.setControlValue(root, "tfMaterial", getMaterial());
        Utils.setControlValue(root, "tfProductPrint", getProductForPrint());
        Utils.setControlValue(root, "taDescription", getDescriptionRu());
        Utils.setControlValue(root, "taDescriptionEn", getDescriptionEn());
        Utils.setControlValue(root, "tfArticle", getArticle());
        Utils.setControlValue(root, "tfHierarchy", getHierarchy());
        Utils.setControlValue(root, "tfLgbk", getLgbk());
        Utils.setControlValue(root, "tfEndOfService", getEndOfService());
        Utils.setControlValue(root, "tfDangerous", getDangerous());
        Utils.setControlValue(root, "tfCountry", Countries.getCombinedName(getCountry()));
        Utils.setControlValue(root, "tfAccessibility", getOrderableStatus());
        Utils.setControlValue(root, "cbxOrderable", isOrderableCalculated());
        Utils.setControlValue(root, "tfDescription", getDescriptionRu());
        Utils.setControlValue(root, "cbxPrice", isPrice());
        Utils.setControlValue(root, "lHistory", getHistory());
        Utils.setControlValue(root, "tfManHier", CoreModule.getProductLgbkGroups().getFullDescription(
                new ProductLgbk(getLgbk(), getHierarchy())));

        String fileName = getFileName().isEmpty() ? getMaterial().replaceAll("[\\\\/:*?\"<>|]+", "") +
                "_" + getArticle().replaceAll("[\\\\/:*?\"<>|]+", "") + ".pdf" : getFileName();

        Utils.setControlValue(root, "tfFileName", fileName);
        if (new File(CoreModule.getFolders().getManualsFolder().getPath() + "\\" + fileName).exists()) {
            Utils.setColor(root, "tfFileName", Color.GREEN);
        } else {
            Utils.setColor(root, "tfFileName", Color.RED);
        }

        Utils.setControlValue(root, "taComments", getComments());
        Utils.setControlValue(root, "tfReplacement", getReplacement());

//        Utils.setControlValue(root, "cbType", CoreModule.getProductTypes().getPreparedTypes());
        Utils.setControlValue(root, "cbType", CoreModule.getProductTypes().getTypeById(getType_id()));

        ArrayList<String> items = CoreModule.getProductFamilies().getFamiliesNames();//add all families and display value
        items.add(0, "");
        Utils.setControlValue(root, "cbFamily", items);
        Utils.setControlValue(root, "tfMinOrder", minOrder == 0 ? NO_DATA : String.valueOf(minOrder));
        Utils.setControlValue(root, "tfPacketSize", packetSize == 0 ? NO_DATA : String.valueOf(packetSize));
        Utils.setControlValue(root, "tfLeadTime", leadTime == 0 ? NO_DATA : String.valueOf(leadTime + 14));
        Utils.setControlValue(root, "tfWeight", weight == 0 ? NO_DATA : String.valueOf(weight));
        Utils.setControlValue(root, "tfLocalPrice", localPrice == 0 ? NO_DATA : String.format("%,.2f", localPrice));

        ProductFamily productFamily = getProductFamily();
        if (productFamily != null) {
            Utils.setControlValue(root, "cbFamily", productFamily.getName());
            Utils.setControlValue(root, "tfPm", productFamily.getResponsible());
        }
    }

    public boolean isOrderableCalculated() {
        OrderAccessibility oa = CoreModule.getOrdersAccessibility().getOrderAccessibilityByStatusCode(getDchain());
        if (oa != null) return oa.isOrderable();
        else return false;
    }

    public boolean matchFilter(Filter filter) {
        boolean allRecords = ((Boolean) FILTER_ALL_ITEMS.getValue());
        boolean priceMatches = ((Boolean) FILTER_PRICE_ITEMS.getValue() && isPrice());
//        boolean archive = (filter.getFilterSimpleByUIname("cbxArchive").isValue() && isArchive());
//        boolean needAction = (filter.getFilterSimpleByUIname("cbxNeedAction").isValue());// && isNeedaction());
//        boolean lgbk = filter.getLgbk().equals(FILTER_VALUE_ALL_LGBK) ? true : filter.getLgbk().equals(getLgbk());
        boolean lgbkMatches = true;
        LgbkAndParent lgbkAndParent = CoreModule.getProductLgbkGroups().getLgbkAndParent(new ProductLgbk(this));

        while (lgbkAndParent == null || lgbkAndParent.getLgbkParent() == null || lgbkAndParent.getLgbkItem() == null) {
            System.out.println(article + ", new lgbk/hierarchy");
            CoreModule.getProductLgbkGroups().checkConsistency();
            lgbkAndParent = CoreModule.getProductLgbkGroups().getLgbkAndParent(new ProductLgbk(getLgbk(), getHierarchy()));
        }

//        boolean globalNotUsed = lgbkAndParent.getLgbkItem().isNotUsed() || lgbkAndParent.getLgbkParent().isNotUsed();
//        boolean summaryNotUsed = isNotused() || globalNotUsed;
//        boolean notUsed = filter.getFilterSimpleByUIname("cbxNotUsed").isValue() && summaryNotUsed;

        boolean matchChanges = false;
       /* if (needAction) {
            if (!isNeedaction()) return false;

            ArrayList<String> changeItems = new ArrayList<>(Arrays.asList(CoreModule.getFilter().getChangeCode().split("\\,")));

            matchChanges = false;
            for (String chIt : changeItems) {
                if ((!changecodes.trim().isEmpty() && changecodes.contains(chIt.trim())) ||
                        (!lastImportcodes.trim().isEmpty() && lastImportcodes.contains(chIt.trim())))
                    matchChanges = true;
            }

            if ((allRecords || price*//* && (!summaryNotUsed || notUsed)*//* || archive *//*|| !price && notUsed*//*) && matchChanges && lgbk)
                return true;
        } else {*/
        if ((allRecords || priceMatches) && lgbkMatches)
            return true;
//        }

        return false;
    }

    public String getTextForComparing() {
        String[] parts = getMaterial().split("\\-");

        if (parts.length < 3) return getMaterial();

        int num = Math.max(4 - parts[1].length(), 0);
        String addedText = "00".substring(0, num);
        String middlePart = parts[1].substring(0, 1).concat(addedText).concat(parts[1].substring(1));

        return (parts[0] + middlePart + parts[2]);
    }

    @Override
    public String toString() {
        return material + ", " + article;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(String hierarchy) {
        this.hierarchy = hierarchy;
    }

    public String getLgbk() {
        return lgbk;
    }

    public void setLgbk(String lgbk) {
        this.lgbk = lgbk;
    }

    public String getEndOfService() {
        return endOfService;
    }

    public void setEndOfService(String endOfService) {
        this.endOfService = endOfService;
    }

    public String getDangerous() {
        return dangerous;
    }

    public void setDangerous(String dangerous) {
        this.dangerous = dangerous;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDchain() {
        return dchain;
    }

    public void setDchain(String dchain) {
        this.dchain = dchain;
    }

    public String getDescriptionRu() {
        return descriptionRu;
    }

    public void setDescriptionRu(String descriptionRu) {
        this.descriptionRu = descriptionRu;
    }

    public boolean isPrice() {
        return price;
    }

    public void setPrice(boolean price) {
        this.price = price;
    }

    public int getType_id() {
        return type_id;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
    }

    public int getProductLine_id() {
        return productLine_id;
    }

    public void setProductLine_id(int productLine_id) {
        this.productLine_id = productLine_id;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getLastChangeDate() {
        return lastChangeDate;
    }

    public void setLastChangeDate(String lastChangeDate) {
        this.lastChangeDate = lastChangeDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public int getFamily() {
        return family;
    }

    public void setFamily(int family) {
        this.family = family;
    }

    public ProductFamily getProductFamily() {
        if (family > 0) {
            return CoreModule.getProductFamilies().getFamilyById(family);
        } else {
            LgbkAndParent lgbkAndParent = CoreModule.getProductLgbkGroups().getLgbkAndParent(new ProductLgbk(this));
            return lgbkAndParent != null ? lgbkAndParent.getProductFamily() : null;
        }
    }

    public boolean isSpProduct() {
        ProductFamily pf = getProductFamily();
        return pf != null && pf.getId() == 24;
    }

    public String getProductForPrint() {
        return productForPrint;
    }

    public void setProductForPrint(String productForPrint) {
        this.productForPrint = productForPrint;
    }

    public String getLastImportCodes() {
        return lastImportCodes;
    }

    public void setLastImportCodes(String lastImportCodes) {
        this.lastImportCodes = lastImportCodes;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    private String nullToEmpty(String text) {
        return text == null ? "" : text;
    }

    public NormsList getNormsList() {
        return normsList;
    }

    public void setNormsList(NormsList normsList) {
        this.normsList = normsList;
    }

    public int getNormsMode() {
        return normsMode;
    }

    public void setNormsMode(int normsMode) {
        this.normsMode = normsMode;
    }

    public int getMinOrder() {
        return minOrder;
    }

    public int getPacketSize() {
        return packetSize;
    }

    public int getLeadTime() {
        return leadTime;
    }

    public double getWeight() {
        return weight;
    }

    public double getLocalPrice() {
        return localPrice;
    }

    public int getPreparedLeadTime() {
        return getLeadTime() > 0 ? getLeadTime() + 14 : 0;
    }

    public String getDescriptionRuEn() {
        return getDescriptionRu().isEmpty() ? getDescriptionEn() : getDescriptionRu();
    }
}


