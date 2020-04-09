package ui_windows.options_window.certificates_editor.certificate_content_editor;

import core.CoreModule;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ui_windows.product.ProductType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CertificateContent {
    private int id;
    private int certId;
    private String equipmentName;
    private ProductType productType;
    private boolean wasChanged;

    public CertificateContent(int id, int certId, ProductType productType, String equipmentName) {
        this.id = id;
        this.certId = certId;
        this.equipmentName = equipmentName;
        this.productType = new ProductType(productType.getId(), productType.getType(), productType.getTen());
        wasChanged = false;
    }

    public CertificateContent(ResultSet rs) {
        try {
            id = rs.getInt("id");
            certId = rs.getInt("cert_id");
            equipmentName = rs.getString("product_names");
            ProductType temp = CoreModule.getProductTypes().getById(rs.getInt("product_type_id"));
            productType = temp == null ? new ProductType(0, "", "") : temp;
        } catch (SQLException e) {
            System.out.println("exception Certificate Content constructor from DB: " + e.getMessage());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCertId() {
        return certId;
    }

    public void setCertId(int certId) {
        this.certId = certId;
    }

    public boolean wasChanged() {
        return wasChanged;
    }

    public void setWasChanged(boolean wasChanged) {
        this.wasChanged = wasChanged;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }
}
