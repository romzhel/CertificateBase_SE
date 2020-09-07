package ui_windows.product.certificatesChecker;

import ui_windows.options_window.certificates_editor.Certificate;
import ui_windows.product.Product;

public class CertificateVerificationItem {
    public static final String ABSENT_TEXT = "НЕ ОК, отсутствует";
    public static final String NOT_OK_TEXT = "НЕ ОК";
    private String norm;
    private String matchedPart;
    private String prodType;
    private String file;
    private String status;
    private String expirationDate;
    private Certificate certificate;
    private Product product;

    public CertificateVerificationItem(String norm) {
        this.norm = norm;
        this.matchedPart = "";
        this.prodType = "";
        this.file = "";
        this.status = ABSENT_TEXT;
        expirationDate = "";
        this.certificate = null;
        this.product = null;
    }

    public CertificateVerificationItem(String norm, String matchedPart, String prodType, String file, String status,
                                       String expDate, Certificate certificate, Product product) {
        this.norm = norm;
        this.matchedPart = matchedPart;
        this.prodType = prodType;
        this.file = file;
        this.status = status;
        expirationDate = expDate;
        this.certificate = certificate;
        this.product = product;
    }

    @Override
    public String toString() {
        return norm + ", " + matchedPart + ", " + prodType + ", " + file + ", " + status;
    }

    public String getNorm() {
        return norm;
    }

    public void setNorm(String norm) {
        this.norm = norm;
    }

    public String getMatchedPart() {
        return matchedPart;
    }

    public void setMatchedPart(String matchedPart) {
        this.matchedPart = matchedPart;
    }

    public String getProdType() {
        return prodType;
    }

    public void setProdType(String prodType) {
        this.prodType = prodType;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
