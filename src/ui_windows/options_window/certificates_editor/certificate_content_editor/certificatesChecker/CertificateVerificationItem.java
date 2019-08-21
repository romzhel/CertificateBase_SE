package ui_windows.options_window.certificates_editor.certificate_content_editor.certificatesChecker;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ui_windows.main_window.Product;
import ui_windows.options_window.certificates_editor.Certificate;

public class CertificateVerificationItem {
    public static final String ABSENT_TEXT = "НЕ ОК, отсутствует";
    private StringProperty norm;
    private StringProperty matchedPart;
    private StringProperty prodType;
    private StringProperty file;
    private StringProperty status;
    private String expirationDate;
    private Certificate certificate;
    private Product product;

    public CertificateVerificationItem(String norm) {
        this.norm = new SimpleStringProperty(norm);
        this.matchedPart = new SimpleStringProperty("");
        this.prodType = new SimpleStringProperty("");
        this.file = new SimpleStringProperty("");
        this.status = new SimpleStringProperty(ABSENT_TEXT);
        expirationDate = "";
        this.certificate = null;
        this.product = null;
    }

    public CertificateVerificationItem(String norm, String matchedPart, String prodType, String file, String status,
                                       String expDate, Certificate certificate, Product product) {
        this.norm = new SimpleStringProperty(norm);
        this.matchedPart = new SimpleStringProperty(matchedPart);
        this.prodType = new SimpleStringProperty(prodType);
        this.file = new SimpleStringProperty(file);
        this.status = new SimpleStringProperty(status);
        expirationDate = expDate;
        this.certificate = certificate;
        this.product = product;
    }

    @Override
    public String toString() {
        return norm + ", " + matchedPart + ", " + prodType + ", " + file + ", " + status;
    }

    public String getNorm() {
        return norm.get();
    }

    public void setNorm(String norm) {
        this.norm.set(norm);
    }

    public StringProperty normProperty() {
        return norm;
    }

    public String getMatchedPart() {
        return matchedPart.get();
    }

    public void setMatchedPart(String matchedPart) {
        this.matchedPart.set(matchedPart);
    }

    public StringProperty matchedPartProperty() {
        return matchedPart;
    }

    public String getFile() {
        return file.get();
    }

    public void setFile(String file) {
        this.file.set(file);
    }

    public StringProperty fileProperty() {
        return file;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getProdType() {
        return prodType.get();
    }

    public StringProperty prodTypeProperty() {
        return prodType;
    }

    public void setProdType(String prodType) {
        this.prodType.set(prodType);
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
