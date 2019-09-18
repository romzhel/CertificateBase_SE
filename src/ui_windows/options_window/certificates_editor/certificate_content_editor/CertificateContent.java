package ui_windows.options_window.certificates_editor.certificate_content_editor;

import core.CoreModule;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CertificateContent {
    private int id;
    private int certId;
    private String equipmentType;
    private String tnved;
    private String equipmentName;
    private boolean wasChanged;

    public CertificateContent(int id, int certId, String equipmentType, String tnved, String equipmentName) {
        this.id = id;
        this.certId = certId;
        this.equipmentType = equipmentType;
        this.tnved = tnved;
        this.equipmentName = equipmentName;
        wasChanged = false;
    }

    public CertificateContent(ResultSet rs) {
        try {
            id = rs.getInt("id");
            certId = rs.getInt("cert_id");
            equipmentType = CoreModule.getProductTypes().getTypeById(rs.getInt("product_type_id"));
            tnved = CoreModule.getProductTypes().getTnVedById(rs.getInt("product_type_id"));
            equipmentName = rs.getString("product_names");
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

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getTnved() {
        return tnved;
    }

    public void setTnved(String tnved) {
        this.tnved = tnved;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public boolean isWasChanged() {
        return wasChanged;
    }

    public void setWasChanged(boolean wasChanged) {
        this.wasChanged = wasChanged;
    }
}
