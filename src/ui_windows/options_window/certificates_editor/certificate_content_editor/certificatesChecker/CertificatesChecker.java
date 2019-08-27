package ui_windows.options_window.certificates_editor.certificate_content_editor.certificatesChecker;

import core.CoreModule;
import ui_windows.options_window.certificates_editor.Certificate;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.Product;
import utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import static ui_windows.options_window.certificates_editor.certificate_content_editor.certificatesChecker.CheckStatusResult.*;

public class CertificatesChecker {
    public final static String NOT_OK = "НЕ ОК";
    public final static String OK = "ОК";
    public final static String EXPIRED = ", истек";
    public final static String BAD_COUNTRY = ", нет страны";
    private ArrayList<CertificateVerificationItem> resultTableItems;
    private ArrayList<Integer> satisfiedNorms;
    private HashSet<Integer> globalNeededNorms;
    private HashSet<Integer> productNeededNorms;
    private CheckStatusResult checkStatusResult = NO_DATA;
    private int certsOk;
    private int certsErr;
    private int certTotal;
    private int certsAbs;
    private int temporaryTypeId;
    private boolean useTemporaryTypeId;


    public CertificatesChecker() {
        resultTableItems = new ArrayList<>();
        satisfiedNorms = new ArrayList<>();
        globalNeededNorms = new HashSet<>();
        productNeededNorms = new HashSet<>();
    }

    public void check(Product product) {
        certsOk = 0;
        certsErr = 0;
        certTotal = 0;
        certsAbs = 0;
        checkExistingCertificates(product);
        checkNorms(product);
    }

    private void checkExistingCertificates(Product product) {
        resultTableItems.clear();
        satisfiedNorms.clear();
        ArrayList<String> prodNames = new ArrayList<>();

        for (Certificate cert : CoreModule.getCertificates().getCertificates()) {//check all certificates

            prodNames.clear();//forming comparing product values (article / material)
            prodNames.add(Utils.toEN(product.getArticle()).toUpperCase());
            if (cert.isMaterialMatch()) prodNames.add(Utils.toEN(product.getMaterial()).toUpperCase());

            int contentTypeId;
            Certificate certificate;
            for (CertificateContent content : cert.getContent()) {//check all content
                contentTypeId = CoreModule.getProductTypes().getIDbyType(content.getEquipmentType());
                certificate = CoreModule.getCertificates().getCertificateByID(content.getCertId());
                boolean fullNameMatch = certificate.isFullNameMatch();

                boolean productTypeNotDefined = product.getType_id() == 0;
                boolean productTypeMatches = product.getType_id() > 0 && product.getType_id() == contentTypeId;
                boolean changedProductTypeMatch = temporaryTypeId > 0 && temporaryTypeId == contentTypeId;
                boolean changedProductTypeNotDefined = temporaryTypeId == 0;

                boolean usualWay = !useTemporaryTypeId && (productTypeNotDefined || productTypeMatches);
                boolean temporaryWay = useTemporaryTypeId && (changedProductTypeNotDefined || changedProductTypeMatch);

                if (fullNameMatch || usualWay || temporaryWay) {

                    for (String contentName : Utils.stringToList(content.getEquipmentName())) {//check all content names
                        for (String prod : prodNames) {//compare product article / material with certificate content

                            String status = "";
                            String contentValue = "";

                            if (contentName.matches(".+[x]{2,}.*")) {
                                contentValue = contentName.replaceAll("[x]{2,}", ".*").trim().toUpperCase();
                            } else if (!cert.isFullNameMatch()) {
                                contentValue = contentName.trim().toUpperCase() + "\\s*\\d+.*";
                            } else contentValue = contentName.trim().toUpperCase();

                            contentValue = contentValue.replaceAll("\\(", "\\\\(")
                                    .replaceAll("\\)", "\\\\)");

                            if (prod.matches(contentValue)) {
                                if ((new Date()).after(Utils.getDate(cert.getExpirationDate()))) {//expired
                                    status = NOT_OK + EXPIRED + cert.getExpirationDate();
                                    certsErr++;
                                }

                                if (product.getCountry().trim().length() > 0 && !cert.getCountries().toLowerCase().//no country
                                        contains(product.getCountry().toLowerCase())) {
                                    status = status.isEmpty() ? NOT_OK + BAD_COUNTRY + " (" + product.getCountry().toUpperCase() + ")"
                                            : status + BAD_COUNTRY + " (" + product.getCountry().toUpperCase() + ")";
                                    certsErr++;
                                }

                                if (status.isEmpty()) {
                                    status = OK + ", до " + cert.getExpirationDate();
                                    certsOk++;
                                }

                                satisfiedNorms.addAll(Utils.getNumberALfromStringEnum(cert.getNorms()));
                                String norms = CoreModule.getRequirementTypes().getNormsShortNamesByIds(cert.getNorms());
                                resultTableItems.add(new CertificateVerificationItem(norms, contentName, content.getEquipmentType(),
                                        cert.getFileName(), status, cert.getExpirationDate(), cert, product));
                            }
                        }
                    }
                }
            }
        }
        useTemporaryTypeId = false;
    }

    private void checkNorms(Product product) {
        globalNeededNorms.clear();
        productNeededNorms.clear();

        HashSet<Integer> normsForChecking = new HashSet<>();

        productNeededNorms.addAll(product.getNormsList().getIntegerItems());
        globalNeededNorms.addAll(CoreModule.getProductLgbkGroups().getGlobalNormIds(
                new ProductLgbk(product.getLgbk(), product.getHierarchy())));

        if (product.getNormsMode() == NormsList.ADD_TO_GLOBAL) {
            normsForChecking.addAll(globalNeededNorms);

            if (product.getArticle().endsWith("-EX")) {
                int normExId = CoreModule.getRequirementTypes().getExNormId();
                if (normExId > 0) normsForChecking.add(normExId);
            }
        }

        normsForChecking.addAll(productNeededNorms);
        int normsForCheckingCount = normsForChecking.size();

        normsForChecking.removeAll(satisfiedNorms);
        for (int normIndex : normsForChecking) {
            String shortName = CoreModule.getRequirementTypes().getRequirementByID(normIndex).getShortName();
            resultTableItems.add(new CertificateVerificationItem(shortName));
            certsAbs++;
        }

        certTotal = resultTableItems.size();

        if (certTotal == 0) {
            checkStatusResult = NO_NORMS;
        } else if (certTotal > 0 && normsForCheckingCount == 0) {
            checkStatusResult = NO_NORMS;
        } else if (certTotal == certsOk && certsErr == 0) {
            checkStatusResult = STATUS_OK;
        } else if (certsOk > 0 && certsAbs > 0 && certsErr == 0) {
            checkStatusResult = PART_OF_CERT;
        } else if (certsErr > 0) {
            checkStatusResult = CERT_WITH_ERR;
        } else if (certTotal == certsAbs) {
            checkStatusResult = NO_CERT;
        }
    }

    public String getCheckStatusResultStyle() {
        switch (checkStatusResult) {
            case NO_CERT:
            case CERT_WITH_ERR:
//                return "-fx-text-fill: red;";
                return "itemStrikethroughRed";
            case PART_OF_CERT:
                return "itemStrikethroughBrown";
            case STATUS_OK:
                return "itemStrikethroughGreen";
            default:
                return "itemStrikethroughBlack";
        }
    }

    public ArrayList<CertificateVerificationItem> getResultTableItems() {
        return resultTableItems;
    }

    public ArrayList<Integer> getSatisfiedNorms() {
        return satisfiedNorms;
    }

    public HashSet<Integer> getGlobalNeededNorms() {
        return globalNeededNorms;
    }

    public HashSet<Integer> getProductNeededNorms() {
        return productNeededNorms;
    }

    public String getCheckStatusResult() {
        return checkStatusResult.getText();
    }

    public void setTemporaryTypeId(int temporaryTypeId) {
        this.temporaryTypeId = temporaryTypeId;
    }

    public void setUseTemporaryTypeId(boolean useTemporaryTypeId) {
        this.useTemporaryTypeId = useTemporaryTypeId;
    }
}
