package ui_windows.options_window.certificates_editor.certificate_content_editor;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import ui_windows.product.ProductType;
import ui_windows.product.ProductTypes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@Data
@Log4j2
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
            ProductType temp = ProductTypes.getInstance().getById(rs.getInt("product_type_id"));
            productType = temp == null ? new ProductType(0, "", "") : temp;
        } catch (SQLException e) {
            log.error("exception Certificate Content constructor from DB: {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CertificateContent that = (CertificateContent) o;
        return certId == that.certId && Objects.equals(equipmentName, that.equipmentName) && Objects.equals(productType, that.productType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certId, equipmentName, productType);
    }
}
