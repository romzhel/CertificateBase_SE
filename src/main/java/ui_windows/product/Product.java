package ui_windows.product;

import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.options_window.families_editor.ProductFamilies;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;
import ui_windows.options_window.order_accessibility_editor.OrdersAccessibility;
import ui_windows.options_window.product_lgbk.LgbkAndParent;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.product_lgbk.ProductLgbkGroups;
import ui_windows.product.data.DataItem;
import ui_windows.product.productEditorWindow.ProductEditorWindowController;
import utils.Countries;
import utils.PriceUtils;
import utils.comparation.se.Cloneable;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
public class Product implements Cloneable {
    private static final Logger logger = LogManager.getLogger(Product.class);
    public static final String NO_DATA = "нет данных";

    private int id;
    private String material;
    private String productForPrint;
    private String article;
    private String hierarchy = "";
    private String lgbk = "";
    private String endofservice;
    private String dangerous;
    private String country;
    private String dchain;
    private String descriptionru = "";
    private String descriptionen = "";
    private Boolean price = false;
    private Boolean blocked = false;
    private Boolean priceHidden = false;
    private Integer warranty = 0;

    private String changecodes = "";
    private String lastImportcodes = "";

    private Integer family_id = -1;
    private Integer type_id = 0;
    private String history = "";
    private String lastChangeDate = "";
    //    private last
    private String fileName = "";
    private String comments = "";
    private String commentsPrice = "";
    private String replacement = "";
    private NormsList normsList;
    private Integer normsMode = NormsList.ADD_TO_GLOBAL;
    private Integer minOrder = 0;
    private Integer packetSize = 0;
    private Integer leadTime = 0;
    private Double weight = 0.0;
    private Double localPrice = 0.0;

    private final Set<DataItem> protectedData = new HashSet<>();

    public Product(ProductEditorWindowController pewc) {
        id = 0;
        material = pewc.tfMaterial.getText();
        article = pewc.tfArticle.getText();

        lgbk = pewc.tfLgbk.getText();
        hierarchy = pewc.tfHierarchy.getText();
        ProductFamily pf = getProductFamily();
        int calcFamilyId = pf != null ? pf.getId() : -1;
        int uiFamilyId = ProductFamilies.getInstance().getFamilyIdByName(pewc.cbFamily.getValue());
        family_id = calcFamilyId == uiFamilyId ? 0 : uiFamilyId;

        descriptionru = pewc.taDescription.getText();
        descriptionen = pewc.taDescriptionEn.getText();
        price = pewc.cbxPrice.isSelected();
        blocked = pewc.cbxBlocked.isSelected();
        priceHidden = pewc.getPriceBox().getButtonStatus();
        changecodes = "";
        lastImportcodes = "";

        type_id = ProductTypes.getInstance().getIDbyType(pewc.cbType.getValue() == null ? "" : pewc.cbType.getValue());
//        history = pewc.lHistory.toString();///!!!!!! check
        lastChangeDate = "";
        comments = pewc.taComments.getText();
        replacement = pewc.tfReplacement.getText();
    }

    /*public Product(RowData rowData, Mapper mapper) {
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
        try {
            warranty = Integer.valueOf(rowData.get(mapper.getFieldIndexByDataItem(DataItem.DATA_WARRANTY)));
        } catch (Exception e) {
            warranty = 0;
        }
    }
        commentsPrice = rowData.get(mapper.getFieldIndexByDataItem(DataItem.DATA_COMMENT_PRICE));
    }*/

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
        blocked = rs.getBoolean("not_used");
        priceHidden = rs.getBoolean("archive");
        warranty = rs.getInt("warranty");

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

    public void displayInEditorWindow(ProductEditorWindowController pewc) {
        pewc.tfMaterial.setText(material);
        pewc.tfMaterialPrint.setText(productForPrint);
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

        String plt = DataItem.DATA_IN_WHICH_PRICE_LIST.getValue(this).toString();
        pewc.tfPriceListIncl.setText(plt);
        double pr = (double) DataItem.DATA_LOCAL_PRICE_LIST.getValue(this);
        pewc.getPriceBox().setValue(plt == null || plt.isEmpty() ? "" :
                pr == 0.0 ? "Нет данных" : String.format("%,.2f", pr));
        pewc.getPriceBox().setButtonStatus(priceHidden);

        if (dchain != null)
            pewc.tfAccessibility.setText(OrdersAccessibility.getInstance().getCombineOrderAccessibility(dchain));
        if (price != null) {
            pewc.cbxPrice.setSelected(price);
        } else {
            pewc.cbxPrice.setIndeterminate(true);
            pewc.cbxPrice.setDisable(true);
        }
        if (blocked != null) {
            pewc.cbxBlocked.setSelected(blocked);
            if (blocked) {
                pewc.cbxBlocked.setStyle("-fx-text-fill: red; -fx-border-color: red; -fx-outer-border: red; mark-color: red; -fx-mark-color: red;");
                pewc.cbxPrice.setDisable(true);
            } else {
                pewc.cbxBlocked.setStyle("");
                pewc.cbxPrice.setDisable(false);
            }
        } else {
            pewc.cbxBlocked.setIndeterminate(true);
            pewc.cbxBlocked.setDisable(true);
        }
        pewc.lHistory.getItems().clear();
        pewc.lHistory.getItems().addAll(history.split("\\|"));
        ProductLgbk pl = new ProductLgbk(this);
        if (pl != null) pewc.tfManHier.setText(ProductLgbkGroups.getInstance().getFullDescription(pl));

        String manualFile = "";
        if (fileName == null) {
            pewc.tfFileName.setText("");
        } else if (fileName.isEmpty() && material != null && article != null) {
            manualFile = (material + "_" + article + ".pdf").replaceAll("[\\\\/:*?\"<>|]+", "");
            pewc.tfFileName.setText(manualFile);
            pewc.tfFileName.setStyle(new File(manualFile).exists() ? "-fx-text-inner-color: green;" : "-fx-text-inner-color: red;");
        } else {
            pewc.tfFileName.setText(fileName);
            pewc.tfFileName.setStyle(new File(fileName).exists() ? "-fx-text-inner-color: green;" : "-fx-text-inner-color: red;");
        }

        pewc.taComments.setText(comments);
        pewc.taCommentsPrice.setText(commentsPrice);
        pewc.taComments.setEditable(comments != null);
        pewc.tfReplacement.setText(replacement);
        pewc.tfReplacement.setEditable(replacement != null);
        if (type_id != null) pewc.cbType.setValue(ProductTypes.getInstance().getTypeById(type_id));
        pewc.cbType.setDisable(type_id == null);
        if (minOrder != null) pewc.tfMinOrder.setText(minOrder == 0 ? NO_DATA : String.valueOf(minOrder));
        if (packetSize != null) pewc.tfPacketSize.setText(packetSize == 0 ? NO_DATA : String.valueOf(packetSize));
        if (leadTime != null) pewc.tfLeadTime.setText(leadTime == 0 ? NO_DATA : String.valueOf(getLeadTimeRu()));
        if (weight != null) pewc.tfWeight.setText(weight == 0 ? NO_DATA : String.valueOf(weight));
        if (localPrice != null)
            pewc.tfLocalPrice.setText(localPrice == 0 ? NO_DATA : String.format("%,.2f", PriceUtils.roundCost(localPrice)));

        List<String> items = ProductFamilies.getInstance().getFamiliesNames();//add all families and display value
        items.add(0, "");
        pewc.cbFamily.getItems().addAll(items);
        ProductFamily productFamily = getProductFamily();
        if (productFamily != null) {
            pewc.cbFamily.setValue(productFamily.getName());
            pewc.tfPm.setText(productFamily.getResponsible());
        }

        pewc.tfWarranty.setText(DataItem.DATA_WARRANTY.getValue(this).toString());
    }

    public boolean isOrderableCalculated() {
        OrderAccessibility oa = OrdersAccessibility.getInstance().getOrderAccessibilityByStatusCode(getDchain());
        if (oa != null) return oa.isOrderable();
        else return false;
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
        return String.format("%s (%s)", article, material);
    }

    @Override
    public Product clone() {
        Product cloneItem = new Product();
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);
                field.set(cloneItem, field.get(this));
            }
        } catch (IllegalAccessException e) {
            logger.error("Product '{}' clone error {}", this, e.getMessage(), e);
        }

        return cloneItem;
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

    public Boolean isPrice() {
        return price == null
                ? null
                : blocked == null ? price : price && !blocked;
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

    public void addHistory(String comment) {
        if (history.isEmpty()) {
            history = comment;
        } else {
            history = history.concat("|").concat(comment);
        }
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

    public String getCommentsPrice() {
        return commentsPrice;
    }

    public void setCommentsPrice(String commentsPrice) {
        this.commentsPrice = commentsPrice;
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

    public ProductFamily getProductFamilyDefValue(ProductFamily defaultValue) {
        ProductFamily result = getProductFamily();
        return result == null ? defaultValue : result;
    }

    public int getProductFamilyId() {
        ProductFamily pf = getProductFamily();
        return pf != null ? pf.getId() : -1;
    }

    public ProductFamily getProductFamily() {
        if (family_id != null && family_id > 0) {
            return ProductFamilies.getInstance().getFamilyById(family_id);
        } else {
            if (lgbk == null) return null;

            LgbkAndParent lgbkAndParent = ProductLgbkGroups.getInstance().getLgbkAndParent(new ProductLgbk(this));
            /*if (lgbkAndParent != null) return lgbkAndParent.getProductFamily();

            if ((hierarchy == null || hierarchy.isEmpty()) && material != null) {
                Product product = Products.getInstance().getItemByMaterialOrArticle(
                        material.replaceAll("(VBPZ\\:)*(BPZ\\:)*", ""));

                if (product != null) {
                    hierarchy = product.hierarchy;
                    lgbkAndParent = ProductLgbkGroups.getInstance().getLgbkAndParent(new ProductLgbk(this));
                }

//                System.out.println("product for material: " + material + " not found");
            }*/

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

    public void addLastImportCodes(String codes) {
        if (lastImportcodes.isEmpty()) {
            lastImportcodes = codes;
        } else {
            lastImportcodes = lastImportcodes.concat(",").concat(codes);
        }
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

    public Double getWeight() {
        return weight;
    }

    public Double getLocalPrice() {
        return localPrice;
    }

    public void setLocalPrice(Double localPrice) {
        this.localPrice = localPrice;
    }

    public int getLeadTimeRu() {
        return getLeadTime() + 14;
    }

    public String getDescriptionRuEn() {
        return getDescriptionru().isEmpty() ? getDescriptionen() : getDescriptionru();
    }

    public ArrayList<Integer> getGlobalNorms() {
        return new ArrayList<Integer>(ProductLgbkGroups.getInstance().getGlobalNormIds(new ProductLgbk(this)));
    }

    public Boolean isBlocked() {
        return blocked;
    }

    public Boolean isPriceHidden() {
        return priceHidden;
    }

    public void setPriceHidden(Boolean priceHidden) {
        this.priceHidden = priceHidden;
    }

    public Integer getWarranty() {
        return warranty;
    }

    public void setWarranty(Integer warranty) {
        this.warranty = warranty;
    }

    public Set<DataItem> getProtectedData() {
        return protectedData;
    }
}