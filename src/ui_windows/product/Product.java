package ui_windows.product;

import core.CoreModule;
import javafx.scene.paint.Color;
import ui_windows.main_window.file_import_window.ColumnsMapper2;
import ui_windows.main_window.file_import_window.NamesMapping;
import ui_windows.main_window.file_import_window.RowData;
import ui_windows.main_window.filter_window.Filter;
import ui_windows.options_window.product_lgbk.LgbkAndParent;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.main_window.file_import_window.ColumnsMapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.AnchorPane;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import utils.Countries;
import utils.Utils;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static ui_windows.main_window.file_import_window.NamesMapping.*;

public class Product {
    public static final String NO_DATA = "нет данных";
    private int id;
    private StringProperty material;
    private StringProperty productForPrint;
    private StringProperty article;
    private StringProperty hierarchy;
    private StringProperty lgbk;
    private StringProperty endofservice;
    private StringProperty dangerous;
    private StringProperty country;
    private StringProperty dchain;
    private StringProperty descriptionru = new SimpleStringProperty("");
    private StringProperty descriptionen = new SimpleStringProperty("");
    private BooleanProperty price = new SimpleBooleanProperty(true);
    private BooleanProperty archive = new SimpleBooleanProperty(false);
    private BooleanProperty needaction = new SimpleBooleanProperty(true);
    private BooleanProperty notused = new SimpleBooleanProperty(false);
    private String changecodes = "";
    private String lastImportcodes = "";

    private int family;
    private int type_id;
    private int productLine_id;
    private String history = "";
    private String lastChangeDate = "";
    //    private last
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

    public Product() {}

    public Product(AnchorPane root) {
        id = 0;
        material = new SimpleStringProperty(Utils.getControlValue(root, "tfMaterial"));
        productForPrint = new SimpleStringProperty((Utils.getControlValue(root, "tfProductPrint")));
        article = new SimpleStringProperty(Utils.getControlValue(root, "tfArticle"));
//        hierarchy = new SimpleStringProperty(Utils.getControlValue(root, "tfHierarchy"));
//        lgbk = new SimpleStringProperty(Utils.getControlValue(root, "tfLgbk"));
        family = CoreModule.getProductFamilies().getFamilyIdByName(Utils.getControlValue(root, "cbFamily"));
//        endofservice = new SimpleStringProperty(Utils.getControlValue(root, "tfEndOfService"));
//        dangerous = new SimpleStringProperty(Utils.getControlValue(root, "tfDangerous"));
//        country = new SimpleStringProperty(Utils.getValueInBrackets(Utils.getControlValue(root, "tfCountry")));
//        dchain = new SimpleStringProperty(Utils.getValueInBrackets(Utils.getControlValue(root, "tfAccessibility")));
        descriptionru = new SimpleStringProperty(Utils.getControlValue(root, "taDescription"));
        descriptionen = new SimpleStringProperty(Utils.getControlValue(root, "taDescriptionEn"));
        price = new SimpleBooleanProperty(Utils.getControlValue(root, "cbxPrice") == "true" ? true : false);
        archive = new SimpleBooleanProperty(Utils.getControlValue(root, "cbxArchive") == "true" ? true : false);
        needaction = new SimpleBooleanProperty(Utils.getControlValue(root, "cbxNeedAction") == "true" ? true : false);
        notused = new SimpleBooleanProperty(Utils.getControlValue(root, "cbxNotUsed") == "true" ? true : false);
        changecodes = "";
        lastImportcodes = "";

        type_id = CoreModule.getProductTypes().getIDbyType(Utils.getControlValue(root, "cbType"));
//        productLine_id;
        history = Utils.getControlValue(root, "lHistory");
        lastChangeDate = "";
        //    private last
//        fileName = Utils.getControlValue(root, "tfFileName");
        comments = Utils.getControlValue(root, "taComments");
        replacement = Utils.getControlValue(root, "tfReplacement");
    }

    public Product(ColumnsMapper mapper) {
        id = 0;
        material = new SimpleStringProperty(mapper.getMarerial());
        productForPrint = new SimpleStringProperty(mapper.getProductPrint());
        article = new SimpleStringProperty(mapper.getArticle());
        hierarchy = new SimpleStringProperty(mapper.getHierarchy());
        lgbk = new SimpleStringProperty(mapper.getLgbk());
        endofservice = new SimpleStringProperty(mapper.getEndOfService());
        dangerous = new SimpleStringProperty(mapper.getDangerous());
        country = new SimpleStringProperty(mapper.getCntryOfOrigin());
        dchain = new SimpleStringProperty(mapper.getDchain());

        price = new SimpleBooleanProperty(false);
        archive = new SimpleBooleanProperty(false);
        needaction = new SimpleBooleanProperty(true);
        notused = new SimpleBooleanProperty(false);
        descriptionru = new SimpleStringProperty(mapper.getDescriptionRu());
        descriptionen = new SimpleStringProperty(mapper.getDescriptionEn());

        normsList = new NormsList(new ArrayList<Integer>());

        minOrder = (int) getDoubleFromString(mapper.getMinOrder());
        packetSize = (int) getDoubleFromString(mapper.getPacketSize());
        leadTime = (int) getDoubleFromString(mapper.getLeadTime());
        weight = getDoubleFromString(mapper.getWeight());
        localPrice = getDoubleFromString(mapper.getLocalPrice());
    }

    public Product(RowData rowData, ColumnsMapper2 mapper) {
        String cellValue;
        id = 0;
        material = new SimpleStringProperty(rowData.get(mapper.getFieldIndexByName(DESC_ORDER_NUMBER)).replaceAll("\\,", "."));
        productForPrint = new SimpleStringProperty(rowData.get(mapper.getFieldIndexByName(DESC_ORDER_NUMBER_PRINT)).replaceAll("\\,", "."));
        article = new SimpleStringProperty(rowData.get(mapper.getFieldIndexByName(DESC_ARTICLE)).replaceAll("\\,", "."));
        hierarchy = new SimpleStringProperty(rowData.get(mapper.getFieldIndexByName(DESC_HIERARCHY)));
        lgbk = new SimpleStringProperty(rowData.get(mapper.getFieldIndexByName(DESC_LGBK)));

        cellValue = rowData.get(mapper.getFieldIndexByName(DESC_SERVICE_END)).replaceAll("\\,", ".");
        endofservice = new SimpleStringProperty(cellValue.matches("00.00.0000") ? "" : cellValue);
        dangerous = new SimpleStringProperty(rowData.get(mapper.getFieldIndexByName(DESC_LOGISTIC_LIMITATION)));
        country = new SimpleStringProperty(rowData.get(mapper.getFieldIndexByName(DESC_COUNTRY)));
        dchain = new SimpleStringProperty(rowData.get(mapper.getFieldIndexByName(DESC_DCHAIN)));

        price = new SimpleBooleanProperty(false);
        archive = new SimpleBooleanProperty(false);
        needaction = new SimpleBooleanProperty(true);
        notused = new SimpleBooleanProperty(false);
        descriptionru = new SimpleStringProperty(rowData.get(mapper.getFieldIndexByName(DESC_DESCRIPTION_RU)));
        descriptionen = new SimpleStringProperty(rowData.get(mapper.getFieldIndexByName(DESC_DESCRIPTION_EN)));

        normsList = new NormsList(new ArrayList<Integer>());

        minOrder = (int) getDoubleFromString(rowData.get(mapper.getFieldIndexByName(DESC_MIN_ORDER)));
        packetSize = (int) getDoubleFromString(rowData.get(mapper.getFieldIndexByName(DESC_PACKSIZE)));
        leadTime = (int) getDoubleFromString(rowData.get(mapper.getFieldIndexByName(DESC_LEADTIME)));
        weight = getDoubleFromString(rowData.get(mapper.getFieldIndexByName(DESC_WEIGHT)));
        localPrice = getDoubleFromString(rowData.get(mapper.getFieldIndexByName(DESC_LOCAL_PRICE)));
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

    public Product(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        material = new SimpleStringProperty(rs.getString("material"));
        productForPrint = new SimpleStringProperty(rs.getString("product_print"));
        article = new SimpleStringProperty(rs.getString("article"));
        hierarchy = new SimpleStringProperty(rs.getString("hierarchy"));
        lgbk = new SimpleStringProperty(rs.getString("lgbk"));
        family = rs.getInt("family");
        endofservice = new SimpleStringProperty(rs.getString("end_of_service"));
        dangerous = new SimpleStringProperty(rs.getString("dangerous"));
        country = new SimpleStringProperty(rs.getString("country"));
        dchain = new SimpleStringProperty(rs.getString("dchain"));

        descriptionru = new SimpleStringProperty(nullToEmpty(rs.getString("description_ru")));
        descriptionen = new SimpleStringProperty(nullToEmpty(rs.getString("description_en")));
        price = new SimpleBooleanProperty(rs.getBoolean("price"));
        archive = new SimpleBooleanProperty(rs.getBoolean("archive"));
        needaction = new SimpleBooleanProperty(rs.getBoolean("need_action"));
        notused = new SimpleBooleanProperty(rs.getBoolean("not_used"));

        history = rs.getString("history");
        lastChangeDate = rs.getString("last_change_date");
        fileName = rs.getString("file_name");
        comments = rs.getString("comments");
        replacement = rs.getString("replacement");

        type_id = rs.getInt("type_id");
        changecodes = rs.getString("change_codes");
        lastImportcodes = rs.getString("last_import_codes") == null ? "" : rs.getString("last_import_codes");

        normsList = new NormsList(rs.getString("norms_list"));
        normsMode = rs.getInt("norms_mode");

        minOrder = rs.getInt("min_order");
        packetSize = rs.getInt("packet_size");
        leadTime = rs.getInt("lead_time");
        weight = rs.getDouble("weight");
        localPrice = rs.getDouble("local_price");
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

            if (result.length() > 0) result += " (" + getDchain() + ")";
            else result = "(" + getDchain() + ")";

        } else {
            if (getDchain().trim().length() > 0) result = "(" + getDchain() + ")";
        }

        return result;
    }

    public void displayInEditorWindow(AnchorPane root) {
        Utils.setControlValue(root, "tfMaterial", getMaterial());
        Utils.setControlValue(root, "tfProductPrint", getProductForPrint());
        Utils.setControlValue(root, "taDescription", getDescriptionru());
        Utils.setControlValue(root, "taDescriptionEn", getDescriptionen());
        Utils.setControlValue(root, "tfArticle", getArticle());
        Utils.setControlValue(root, "tfHierarchy", getHierarchy());
        Utils.setControlValue(root, "tfLgbk", getLgbk());
        Utils.setControlValue(root, "tfEndOfService", getEndofservice());
        Utils.setControlValue(root, "tfDangerous", getDangerous());
        Utils.setControlValue(root, "tfCountry", Countries.getCombinedName(getCountry()));
        Utils.setControlValue(root, "tfAccessibility", getOrderableStatus());
        Utils.setControlValue(root, "cbxOrderable", isOrderableCalculated());
        Utils.setControlValue(root, "tfDescription", getDescriptionru());
        Utils.setControlValue(root, "cbxPrice", isPrice());
        Utils.setControlValue(root, "cbxArchive", isArchive());
        Utils.setControlValue(root, "cbxNeedAction", isNeedaction());
//        Utils.setControlValue(root, "cbxNotUsed", isNotused());
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
        Utils.setControlValue(root, "tfPacketSize", packetSize  == 0 ? NO_DATA : String.valueOf(packetSize));
        Utils.setControlValue(root, "tfLeadTime", leadTime == 0 ? NO_DATA : String.valueOf(leadTime + 14));
        Utils.setControlValue(root, "tfWeight", weight == 0 ? NO_DATA : String.valueOf(weight));
        Utils.setControlValue(root, "tfLocalPrice", localPrice == 0 ? NO_DATA : String.format("%,.2f", localPrice));

        ProductFamily family;
        if (getFamily() > 0)//individual value
            family = CoreModule.getProductFamilies().getFamilyById(getFamily());
        else {//try to calculate it
            int id = CoreModule.getProductLgbks().
                    getFamilyIdByLgbk(new ProductLgbk(getLgbk(), getHierarchy()));
            family = CoreModule.getProductFamilies().getFamilyById(id);
        }

        if (family != null) {
            Utils.setControlValue(root, "cbFamily", family.getName());
            Utils.setControlValue(root, "tfPm", family.getResponsible());
        }
    }

    public boolean isOrderableCalculated() {
        OrderAccessibility oa = CoreModule.getOrdersAccessibility().getOrderAccessibilityByStatusCode(getDchain());
        if (oa != null) return oa.isOrderable();
        else return false;
    }

    public boolean matchFilter(Filter filter) {
        boolean allRecords = (filter.getFilterSimpleByUIname("cbxAllRecords").isValue());
        boolean price = (filter.getFilterSimpleByUIname("cbxPrice").isValue() && isPrice());
        boolean archive = (filter.getFilterSimpleByUIname("cbxArchive").isValue() && isArchive());
        boolean needAction = (filter.getFilterSimpleByUIname("cbxNeedAction").isValue());// && isNeedaction());

        LgbkAndParent lgbkAndParent = CoreModule.getProductLgbkGroups().getLgbkAndParent(new ProductLgbk(getLgbk(), getHierarchy()));

        while (lgbkAndParent == null || lgbkAndParent.getLgbkParent() == null || lgbkAndParent.getLgbkItem() == null) {
            System.out.println(article.getValue() + ", new lgbk/hierarchy");
            CoreModule.getProductLgbkGroups().checkConsistency();
            lgbkAndParent = CoreModule.getProductLgbkGroups().getLgbkAndParent(new ProductLgbk(getLgbk(), getHierarchy()));
        }

        boolean globalNotUsed = lgbkAndParent.getLgbkItem().isNotUsed() || lgbkAndParent.getLgbkParent().isNotUsed();
        boolean summaryNotUsed = isNotused() || globalNotUsed;
        boolean notUsed = filter.getFilterSimpleByUIname("cbxNotUsed").isValue() && summaryNotUsed;

        boolean matchChanges = false;
        if (needAction) {
            if (!isNeedaction()) return false;

            ArrayList<String> changeItems = new ArrayList<>(Arrays.asList(CoreModule.getFilter().getChangeCode().split("\\,")));

            matchChanges = false;
            for (String chIt : changeItems) {
                if ((!changecodes.trim().isEmpty() && changecodes.contains(chIt.trim())) ||
                        (!lastImportcodes.trim().isEmpty() && lastImportcodes.contains(chIt.trim())))
                    matchChanges = true;
            }

            if ((allRecords || price && (!summaryNotUsed || notUsed) || archive || !price && notUsed) && matchChanges ) return true;
        } else {
            if (allRecords || price && (!summaryNotUsed || notUsed) || !price && notUsed || archive  ) return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return material.getValue() + ", " + article.getValue();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaterial() {
        return material.get();
    }

    public void setMaterial(String material) {
        this.material.set(material);
    }

    public StringProperty materialProperty() {
        return material;
    }

    public String getArticle() {
        return article.get();
    }

    public void setArticle(String article) {
        this.article.set(article);
    }

    public StringProperty articleProperty() {
        return article;
    }

    public String getHierarchy() {
        return hierarchy.get();
    }

    public void setHierarchy(String hierarchy) {
        this.hierarchy.set(hierarchy);
    }

    public StringProperty hierarchyProperty() {
        return hierarchy;
    }

    public String getLgbk() {
        return lgbk.get();
    }

    public void setLgbk(String lgbk) {
        this.lgbk.set(lgbk);
    }

    public StringProperty lgbkProperty() {
        return lgbk;
    }

    public String getEndofservice() {
        return endofservice.get();
    }

    public void setEndofservice(String endofservice) {
        this.endofservice.set(endofservice);
    }

    public StringProperty endofserviceProperty() {
        return endofservice;
    }

    public String getDangerous() {
        return dangerous.get();
    }

    public void setDangerous(String dangerous) {
        this.dangerous.set(dangerous);
    }

    public StringProperty dangerousProperty() {
        return dangerous;
    }

    public String getCountry() {
        return country.get();
    }

    public void setCountry(String country) {
        this.country.set(country);
    }

    public StringProperty countryProperty() {
        return country;
    }

    public String getDchain() {
        return dchain.get();
    }

    public void setDchain(String dchain) {
        this.dchain.set(dchain);
    }

    public StringProperty dchainProperty() {
        return dchain;
    }

    public String getDescriptionru() {
        return descriptionru.get();
    }

    public void setDescriptionru(String descriptionru) {
        this.descriptionru.set(descriptionru);
    }

    public StringProperty descriptionruProperty() {
        return descriptionru;
    }

    public boolean isPrice() {
        return price.get();
    }

    public void setPrice(boolean price) {
        this.price.set(price);
    }

    public BooleanProperty priceProperty() {
        return price;
    }

    public boolean isArchive() {
        return archive.get();
    }

    public void setArchive(boolean archive) {
        this.archive.set(archive);
    }

    public BooleanProperty archiveProperty() {
        return archive;
    }

    public boolean isNeedaction() {
        return needaction.get();
    }

    public void setNeedaction(boolean needaction) {
        this.needaction.set(needaction);
    }

    public BooleanProperty needactionProperty() {
        return needaction;
    }

    public boolean isNotused() {
        return notused.get();
    }

    public void setNotused(boolean notused) {
        this.notused.set(notused);
    }

    public BooleanProperty notusedProperty() {
        return notused;
    }

    public String getChangecodes() {
        return changecodes;
    }

    public void setChangecodes(String changecodes) {
        this.changecodes = changecodes;
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

    public String getProductForPrint() {
        return productForPrint.get();
    }

    public StringProperty productForPrintProperty() {
        return productForPrint;
    }

    public void setProductForPrint(String productForPrint) {
        this.productForPrint.set(productForPrint);
    }

    public String getLastImportcodes() {
        return lastImportcodes;
    }

    public void setLastImportcodes(String lastImportcodes) {
        this.lastImportcodes = lastImportcodes;
    }

    public String getDescriptionen() {
        return descriptionen.get();
    }

    public StringProperty descriptionenProperty() {
        return descriptionen;
    }

    public void setDescriptionen(String descriptionen) {
        this.descriptionen.set(descriptionen);
    }

    private String nullToEmpty(String text){
        return text == null? "" : text;
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

    public static String getNO_DATA() {
        return NO_DATA;
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

    public int getPreparedLeadTime(){
        return getLeadTime() > 0 ? getLeadTime() + 14 : 0;
    }

    public String getDescriptionRuEn(){
        return getDescriptionru().isEmpty() ? getDescriptionen() : getDescriptionru();
    }
}


