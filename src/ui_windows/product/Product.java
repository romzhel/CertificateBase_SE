package ui_windows.product;

import com.sun.istack.internal.NotNull;
import core.CoreModule;
import javafx.scene.layout.AnchorPane;
import ui_windows.main_window.file_import_window.ColumnsMapper;
import ui_windows.main_window.file_import_window.RowData;
import ui_windows.main_window.filter_window.Filter;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;
import ui_windows.options_window.product_lgbk.LgbkAndParent;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.data.DataItem;
import ui_windows.product.productEditorWindow.ProductEditorWindowController;
import utils.Countries;
import utils.Utils;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Stream;

import static ui_windows.main_window.filter_window.FilterParameters.FILTER_ALL_ITEMS;
import static ui_windows.main_window.filter_window.FilterParameters.FILTER_PRICE_ITEMS;

public class Product {
    public static final String NO_DATA = "нет данных";
    private int id;
    private String material;
    private String productForPrint;
    private String article;
    private String hierarchy;
    private String lgbk;
    private String endofservice;
    private String dangerous;
    private String country;
    private String dchain;
    private String descriptionru = "";
    private String descriptionen = "";
    private Boolean price = false;
    private String changecodes = "";
    private String lastImportcodes = "";

    private Integer family_id = -1;
    private Integer type_id;
    private String history = "";
    private String lastChangeDate = "";
    //    private last
    private String fileName = "";
    private String comments = "";
    private String replacement = "";
    private NormsList normsList;
    private Integer normsMode = NormsList.ADD_TO_GLOBAL;
    private Integer minOrder;
    private Integer packetSize;
    private Integer leadTime;
    private Double weight;
    private Double localPrice;

    public Product() {
    }

    public Product(ProductEditorWindowController pewc) {
        id = 0;
        material = pewc.tfMaterial.getText();
        article = pewc.tfArticle.getText();

        lgbk = pewc.tfLgbk.getText();
        hierarchy = pewc.tfHierarchy.getText();
        ProductFamily pf = getProductFamily();
        int calcFamilyId = pf != null ? pf.getId() : -1;
        int uiFamilyId = CoreModule.getProductFamilies().getFamilyIdByName(pewc.cbFamily.getValue());
        family_id = calcFamilyId == uiFamilyId ? 0 : uiFamilyId;

        descriptionru = pewc.taDescription.getText();
        descriptionen = pewc.taDescriptionEn.getText();
        price = pewc.cbxPrice.isSelected();
        changecodes = "";
        lastImportcodes = "";

        type_id = CoreModule.getProductTypes().getIDbyType(pewc.cbType.getValue() == null ? "" : pewc.cbType.getValue());
//        history = pewc.lHistory.toString();///!!!!!! check
        lastChangeDate = "";
        comments = pewc.taComments.getText();
        replacement = pewc.tfReplacement.getText();
    }

    public Product(RowData rowData, ColumnsMapper mapper) {
        String cellValue;
        id = 0;
        material = rowData.get(mapper.getFieldIndexByDataItem(DataItem.DATA_ORDER_NUMBER)).replaceAll("\\,", ".");
        productForPrint = rowData.get(mapper.getFieldIndexByDataItem(DataItem.DATA_ORDER_NUMBER_PRINT)).replaceAll("\\,", ".");
        article = rowData.get(mapper.getFieldIndexByDataItem(DataItem.DATA_ARTICLE)).replaceAll("\\,", ".");
        hierarchy = rowData.get(mapper.getFieldIndexByDataItem(DataItem.DATA_HIERARCHY));
        lgbk = rowData.get(mapper.getFieldIndexByDataItem(DataItem.DATA_LGBK));

        cellValue = rowData.get(mapper.getFieldIndexByDataItem(DataItem.DATA_SERVICE_END)).replaceAll("\\,", ".");
        endofservice = cellValue.matches("00.00.0000") ? "" : cellValue;
        dangerous = rowData.get(mapper.getFieldIndexByDataItem(DataItem.DATA_LOGISTIC_NOTES));
        country = rowData.get(mapper.getFieldIndexByDataItem(DataItem.DATA_COUNTRY));
        dchain = rowData.get(mapper.getFieldIndexByDataItem(DataItem.DATA_DCHAIN));

        price = false;
        descriptionru = rowData.get(mapper.getFieldIndexByDataItem(DataItem.DATA_DESCRIPTION_RU));
        descriptionen = rowData.get(mapper.getFieldIndexByDataItem(DataItem.DATA_DESCRIPTION_EN));

        normsList = new NormsList(new ArrayList<>());

        minOrder = (int) getDoubleFromString(rowData.get(mapper.getFieldIndexByDataItem(DataItem.DATA_MIN_ORDER)));
        packetSize = (int) getDoubleFromString(rowData.get(mapper.getFieldIndexByDataItem(DataItem.DATA_PACKSIZE)).replaceAll("\\.", ""));
        leadTime = (int) getDoubleFromString(rowData.get(mapper.getFieldIndexByDataItem(DataItem.DATA_LEAD_TIME_EU)));
        weight = getDoubleFromString(rowData.get(mapper.getFieldIndexByDataItem(DataItem.DATA_WEIGHT)));
        localPrice = getDoubleFromString(rowData.get(mapper.getFieldIndexByDataItem(DataItem.DATA_LOCAL_PRICE)));
    }

    public Product(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        material = rs.getString("material");
        productForPrint = rs.getString("product_print");
        article = rs.getString("article");
        hierarchy = rs.getString("hierarchy");
        lgbk = rs.getString("lgbk");
        family_id = rs.getInt("family");
        endofservice = rs.getString("end_of_service");
        dangerous = rs.getString("dangerous");
        country = rs.getString("country");
        dchain = rs.getString("dchain");

        descriptionru = nullToEmpty(rs.getString("description_ru"));
        descriptionen = nullToEmpty(rs.getString("description_en"));
        price = rs.getBoolean("price");

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

    public static String getNO_DATA() {
        return NO_DATA;
    }

    private double getDoubleFromString(String text) {
        if (text == null || text.isEmpty()) return 0;

        boolean textHasDevider = text.matches("\\d+\\.+\\d+[.,]+\\d+");
        if (textHasDevider) text = text.replaceFirst("\\.", "");

        try {
            text = text.replaceAll("\\,", ".");
            return Double.parseDouble(text);
        } catch (Exception e) {
            System.out.println(article + ", bad double: " + text);
            return 0.0;
        }
    }

    public void displayInEditorWindow(ProductEditorWindowController pewc) {
        pewc.tfMaterial.setText(material);
        pewc.taDescription.setText(descriptionru);
        pewc.taDescription.setEditable(descriptionru != null);
        pewc.taDescriptionEn.setText(descriptionen);
        pewc.taDescriptionEn.setEditable(descriptionen != null);
        pewc.tfArticle.setText(article);
        pewc.tfHierarchy.setText(hierarchy);
        pewc.tfLgbk.setText(lgbk);
        pewc.tfEndOfService.setText(endofservice);
        pewc.tfDangerous.setText(dangerous);
        pewc.tfCountry.setText(Countries.getCombinedName(country));
        if (dchain != null )pewc.tfAccessibility.setText(CoreModule.getOrdersAccessibility().getCombineOrderAccessibility(dchain));
        if (price != null) {
            pewc.cbxPrice.setSelected(price);
        } else {
            pewc.cbxPrice.setIndeterminate(true);
            pewc.cbxPrice.setDisable(true);
        }
        pewc.lHistory.getItems().clear();
        pewc.lHistory.getItems().addAll(history.split("\\|"));
        ProductLgbk pl = new ProductLgbk(this);
        if (pl !=null) pewc.tfManHier.setText(CoreModule.getProductLgbkGroups().getFullDescription(pl));

        String manualFile = "";
        if (fileName == null) {
            pewc.tfFileName.setText("");
        } else if (fileName.isEmpty() && material != null && article != null) {
            manualFile = (material +"_" + article + ".pdf").replaceAll("[\\\\/:*?\"<>|]+", "");
            pewc.tfFileName.setText(manualFile);
            pewc.tfFileName.setStyle(new File(manualFile).exists() ? "-fx-text-inner-color: green;" : "-fx-text-inner-color: red;");
        } else {
            pewc.tfFileName.setText(fileName);
            pewc.tfFileName.setStyle(new File(fileName).exists() ? "-fx-text-inner-color: green;" : "-fx-text-inner-color: red;");
        }

        pewc.taComments.setText(comments);
        pewc.taComments.setEditable(comments != null);
        pewc.tfReplacement.setText(replacement);
        pewc.tfReplacement.setEditable(replacement != null);
        if (type_id != null) pewc.cbType.setValue(CoreModule.getProductTypes().getTypeById(type_id));
        pewc.cbType.setDisable(type_id == null);
        if (minOrder != null) pewc.tfMinOrder.setText(minOrder == 0 ? NO_DATA : String.valueOf(minOrder));
        if (packetSize != null) pewc.tfPacketSize.setText(packetSize == 0 ? NO_DATA : String.valueOf(packetSize));
        if (leadTime != null) pewc.tfLeadTime.setText(leadTime == 0 ? NO_DATA : String.valueOf(getLeadTimeRu()));
        if (weight != null) pewc.tfWeight.setText(weight == 0 ? NO_DATA : String.valueOf(weight));
        if (localPrice != null) pewc.tfLocalPrice.setText(localPrice == 0 ? NO_DATA : String.format("%,.2f", localPrice));

        ArrayList<String> items = CoreModule.getProductFamilies().getFamiliesNames();//add all families and display value
        items.add(0, "");
        pewc.cbFamily.getItems().addAll(items);
        ProductFamily productFamily = getProductFamily();
        if (productFamily != null) {
            pewc.cbFamily.setValue(productFamily.getName());
            pewc.tfPm.setText(productFamily.getResponsible());
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

    public String getEndofservice() {
        return endofservice;
    }

    public void setEndofservice(String endofservice) {
        this.endofservice = endofservice;
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

    public String getDescriptionru() {
        return descriptionru;
    }

    public void setDescriptionru(String descriptionru) {
        this.descriptionru = descriptionru;
    }

    public boolean isPrice() {
        return price;
    }

    public void setPrice(boolean price) {
        this.price = price;
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

    public int getFamily_id() {
        return family_id;
    }

    public void setFamily_id(int family_id) {
        this.family_id = family_id;
    }

    public ProductFamily getProductFamily() {
        if (family_id != null && family_id > 0) {
            return CoreModule.getProductFamilies().getFamilyById(family_id);
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

    public String getLastImportcodes() {
        return lastImportcodes;
    }

    public void setLastImportcodes(String lastImportcodes) {
        this.lastImportcodes = lastImportcodes;
    }

    public String getDescriptionen() {
        return descriptionen;
    }

    public void setDescriptionen(String descriptionen) {
        this.descriptionen = descriptionen;
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

    public int getLeadTimeRu() {
        return getLeadTime() > 0 ? getLeadTime() + 14 : 0;
    }

    public String getDescriptionRuEn() {
        return getDescriptionru().isEmpty() ? getDescriptionen() : getDescriptionru();
    }

    public ArrayList<Integer> getGlobalNorms() {
        return new ArrayList<Integer>(CoreModule.getProductLgbkGroups().getGlobalNormIds(new ProductLgbk(this)));
    }
}