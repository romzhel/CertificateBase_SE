package ui_windows.options_window.certificates_editor;

import core.CoreModule;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import utils.Countries;
import utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Certificate {
    private int id = 0;
    private StringProperty name;
    private StringProperty expirationDate;
    private String countries;
    private String norms;
    private String fileName;
    private ArrayList<CertificateContent> content;
    private boolean fullNameMatch;
    private boolean materialMatch;
    private int userId;

    public Certificate(ResultSet rs) {
        try {
            id = rs.getInt("id");
            name = new SimpleStringProperty(rs.getString("name"));
            expirationDate = new SimpleStringProperty(rs.getString("expiration_date"));
            countries = rs.getString("countries");
            norms = rs.getString("norms");
            fileName = rs.getString("file_name");
            content = CoreModule.getCertificatesContent().getContentByCertID(id);
            fullNameMatch = rs.getBoolean("name_match");
            materialMatch = rs.getBoolean("material_match");
            userId = rs.getInt("user_id");
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    public Certificate(Stage stage) {
        AnchorPane root = (AnchorPane) stage.getScene().getRoot();

        name = new SimpleStringProperty(Utils.getControlValue(root, "tfCertName"));
        expirationDate = new SimpleStringProperty(Utils.getControlValue(root, "dpDatePicker"));
        countries = Countries.getShortNames(Utils.getALControlValueFromLV(root, "lvCountries"));
        norms = CoreModule.getRequirementTypes().getReqIdsLineFromShortNamesAL(Utils.getALControlValueFromLV(root, "lvNorms"));
        fileName = Utils.getControlValue(root, "tfFileName");
        fullNameMatch = Utils.getControlValue(root, "ckbNameMatch") == "true" ? true : false;
        materialMatch = Utils.getControlValue(root, "ckbMaterialMatch") == "true" ? true : false;
        content = new ArrayList<>();
    }

    @Override
    public String toString() {
        System.out.println("cert: ID=" + id + ", name=" + getName() + ", exp date=" + getExpirationDate() +
                ", countries=" + getCountries() + ", fileName=" + getFileName());

//        for (CertificateContent cc : content) {
//            System.out.println(cc.toString());
//        }

        return "";
    }

    public String getMonthToExpiration(){
        Date certDate = Utils.getDate(getExpirationDate());
        Date now = new Date();
        long diff = certDate.getTime() - now.getTime();

        long months = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) / 30;
        return Long.toString(months);
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getExpirationDate() {
        return expirationDate.get();
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate.set(expirationDate);
    }

    public StringProperty expirationDateProperty() {
        return expirationDate;
    }

    public String getCountries() {
        return countries;
    }

    public void setCountries(String countries) {
        this.countries = countries;
    }

    public String getNorms() {
        return norms;
    }

    public void setNorms(String norms) {
        this.norms = norms;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ArrayList<CertificateContent> getContent() {
        return content;
    }

    public void setContent(ArrayList<CertificateContent> content) {
        this.content = content;
    }

    public boolean isFullNameMatch() {
        return fullNameMatch;
    }

    public void setFullNameMatch(boolean fullNameMatch) {
        this.fullNameMatch = fullNameMatch;
    }

    public boolean isMaterialMatch() {
        return materialMatch;
    }

    public void setMaterialMatch(boolean materialMatch) {
        this.materialMatch = materialMatch;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


}
