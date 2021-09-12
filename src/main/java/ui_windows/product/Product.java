package ui_windows.product;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ui_windows.options_window.families_editor.ProductFamilies;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.order_accessibility_editor.OrdersAccessibility;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.product_lgbk.ProductLgbkGroups;
import ui_windows.product.data.DataItem;
import ui_windows.product.productEditorWindow.ProductEditorWindowController;
import ui_windows.product.vendors.VendorEnum;
import utils.Countries;
import utils.PriceUtils;
import utils.comparation.se.Cloneable;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Log4j2
@NoArgsConstructor
public class Product implements Cloneable {
    public static final String NO_DATA = "нет данных";
    private Set<DataItem> protectedData = new HashSet<>();
    private int id;
    private VendorEnum vendor = VendorEnum.SIEMENS;
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
    private NormsList normsList = new NormsList();
    private Integer normsMode = NormsList.ADD_TO_GLOBAL;
    private Integer minOrder = 0;
    private Integer packetSize = 0;
    private Integer leadTime = 0;
    private Double weight = 0.0;
    private Double localPrice = 0.0;

    public Product(ProductEditorWindowController pewc) {
        id = 0;
        material = pewc.tfMaterial.getText();
        article = pewc.tfArticle.getText();

        lgbk = pewc.tfLgbk.getText();
        hierarchy = pewc.tfHierarchy.getText();
        ProductFamily pf = ProductFamilies.getInstance().getFamilyByLgbk(new ProductLgbk(lgbk, hierarchy));
        int calcFamilyId = pf.getId();
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
        commentsPrice = pewc.taCommentsPrice.getText();
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
        pewc.tfVendor.setText(vendor.name());

        String plt = DataItem.DATA_IN_WHICH_PRICE_LIST.getValue(this).toString();
        pewc.tfPriceListIncl.setText(plt);
        double pr = (double) DataItem.DATA_LOCAL_PRICE_LIST.getValue(this);
        pewc.getPriceBox().setValue(plt == null || plt.isEmpty() ? "" :
                pr == 0.0 ? "Нет данных" : String.format("%,.2f", pr));
        pewc.getPriceBox().setButtonStatus(priceHidden);

        if (blocked != null) {
            pewc.cbxBlocked.setSelected(blocked);
            if (blocked) {
                pewc.cbxBlocked.setStyle("-fx-text-fill: red; -fx-border-color: red; -fx-outer-border: red; mark-color: red; -fx-mark-color: red;");
                pewc.cbxPrice.setDisable(true);

                pewc.tfAccessibility.setText("Заблокировано");
                pewc.tfAccessibility.setStyle("-fx-text-fill: red;");
            } else {
                pewc.cbxBlocked.setStyle("");
                pewc.cbxPrice.setDisable(false);

                if (dchain != null)
                    pewc.tfAccessibility.setText(OrdersAccessibility.getInstance().getOrderAccessibility(this).toString());
            }
        } else {
            pewc.cbxBlocked.setIndeterminate(true);
            pewc.cbxBlocked.setDisable(true);
        }

        if (price != null) {
            pewc.cbxPrice.setSelected(price);
        } else {
            pewc.cbxPrice.setIndeterminate(true);
            pewc.cbxPrice.setDisable(true);
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
        pewc.taComments.setEditable(comments != null);
        pewc.taCommentsPrice.setText(commentsPrice);
        pewc.tfReplacement.setText(replacement);
        pewc.tfReplacement.setEditable(replacement != null);
        if (type_id != null) pewc.cbType.setValue(ProductTypes.getInstance().getTypeById(type_id));
        pewc.cbType.setDisable(type_id == null);
        if (minOrder != null) pewc.tfMinOrder.setText(minOrder == 0 ? NO_DATA : String.valueOf(minOrder));
        if (packetSize != null) pewc.tfPacketSize.setText(packetSize == 0 ? NO_DATA : String.valueOf(packetSize));
        if (leadTime != null)
            pewc.tfLeadTime.setText(leadTime == 0 ? NO_DATA : String.valueOf(Products.getInstance().getLeadTimeRu(this)));
        if (weight != null) pewc.tfWeight.setText(weight == 0 ? NO_DATA : String.valueOf(weight));
        if (localPrice != null)
            pewc.tfLocalPrice.setText(localPrice == 0 ? NO_DATA : String.format("%,.2f", PriceUtils.roundCost(localPrice)));

        List<String> items = ProductFamilies.getInstance().getFamiliesNames();//add all families and display value
        items.add(0, "");
        pewc.cbFamily.getItems().addAll(items);
        ProductFamily productFamily = ProductFamilies.getInstance().getProductFamily(this);
        if (productFamily != null) {
            pewc.cbFamily.setValue(productFamily.getName());
            pewc.tfPm.setText(productFamily.getResponsible());
        }

        pewc.tfWarranty.setText(DataItem.DATA_WARRANTY.getValue(this).toString());
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
            log.error("Product '{}' clone error {}", this, e.getMessage(), e);
        }

        return cloneItem;
    }
}