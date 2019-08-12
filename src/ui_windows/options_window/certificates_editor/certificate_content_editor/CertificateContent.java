package ui_windows.options_window.certificates_editor.certificate_content_editor;

import core.CoreModule;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CertificateContent {
    private int id;
    private int certId;
    private StringProperty equipmentType;
    private StringProperty tnved;
    private StringProperty equipmentName;
    private boolean wasChanged;

    public CertificateContent(int id, int certId, String equipmentType, String tnved, String equipmentName) {
        this.id = id;
        this.certId = certId;
        this.equipmentType = new SimpleStringProperty(equipmentType);
        this.tnved = new SimpleStringProperty(tnved);
        this.equipmentName = new SimpleStringProperty(equipmentName);
        wasChanged = false;
    }

    public CertificateContent(ResultSet rs) {
        try {
            id = rs.getInt("id");
            certId = rs.getInt("cert_id");
            equipmentType = new SimpleStringProperty(CoreModule.getProductTypes().getTypeById(rs.getInt("product_type_id")));
            tnved = new SimpleStringProperty( CoreModule.getProductTypes().getTnVedById(rs.getInt("product_type_id")));
            equipmentName = new SimpleStringProperty(rs.getString("product_names"));
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
        return equipmentType.get();
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType.set(equipmentType);
    }

    public StringProperty equipmentTypeProperty() {
        return equipmentType;
    }

    public String getEquipmentName() {
        return equipmentName.get();
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName.set(equipmentName);
    }

    public StringProperty equipmentNameProperty() {
        return equipmentName;
    }

    public boolean isWasChanged() {
        return wasChanged;
    }

    public void setWasChanged(boolean wasChanged) {
        this.wasChanged = wasChanged;
    }

    public String getTnved() {
        return tnved.get();
    }

    public StringProperty tnvedProperty() {
        return tnved;
    }

    public void setTnved(String tnved) {
        this.tnved.set(tnved);
    }

    @Override
    public String toString() {
        return "content id=" + id + ", cert_id=" + certId + ", type=" + equipmentType.getValue() + ", name=" + equipmentName.getValue() + ", wasChanged=" + wasChanged;
    }
}
