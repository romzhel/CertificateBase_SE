package ui_windows.product.certificatesChecker;

import java.util.ArrayList;
import java.util.TreeSet;

public class CertificateCheckResult {
    private CheckStatusResult checkStatusResult;
    private TreeSet<CertificateVerificationItem> certsVerificationItemsResult;
    private String style;


    public CertificateCheckResult(CheckStatusResult checkStatusResult, TreeSet<CertificateVerificationItem> certsVerificationItemsResult, String style) {
        this.checkStatusResult = checkStatusResult;
        this.certsVerificationItemsResult = certsVerificationItemsResult;
        this.style = style;
    }

    public CheckStatusResult getCheckStatusResult() {
        return checkStatusResult;
    }

    public void setCheckStatusResult(CheckStatusResult checkStatusResult) {
        this.checkStatusResult = checkStatusResult;
    }

    public TreeSet<CertificateVerificationItem> getCertsVerificationItemsResult() {
        return certsVerificationItemsResult;
    }

    public void setCertsVerificationItemsResult(TreeSet<CertificateVerificationItem> certsVerificationItemsResult) {
        this.certsVerificationItemsResult = certsVerificationItemsResult;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
