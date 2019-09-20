package ui_windows.options_window.certificates_editor.certificatesChecker;

import core.CoreModule;
import javafx.collections.ObservableList;
import ui_windows.options_window.certificates_editor.Certificate;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.Product;
import utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.TreeSet;

import static ui_windows.options_window.certificates_editor.certificatesChecker.CheckStatusResult.*;

public class CertificatesChecker {
    public final static String NOT_OK = "НЕ ОК";
    public final static String OK = "ОК";
    public final static String EXPIRED = ", истек ";
    public final static String BAD_COUNTRY = ", нет страны ";
    public final static String CERT_NO_NEEDED = "Сертификаты не требуются";
    public final static String ALL_COUNTRIES = "--";
    private TreeSet<CertificateVerificationItem> resultTableItems;
    private ArrayList<Integer> satisfiedNorms;
    private HashSet<Integer> globalNeededNorms;
    private HashSet<Integer> productNeededNorms;
    private CheckStatusResult checkStatusResult = NO_DATA;
    private int certsErr;
    private int temporaryTypeId;
    private boolean useTemporaryTypeId;

    public CertificatesChecker() {
        resultTableItems = new TreeSet<>((o1, o2) -> {
            String o1c = o1.getNorm().concat(o1.getMatchedPart()).concat(o1.getFile()).concat(o1.getStatus());
            String o2c = o2.getNorm().concat(o2.getMatchedPart()).concat(o2.getFile()).concat(o2.getStatus());
            return o1c.compareTo(o2c);
        });
        satisfiedNorms = new ArrayList<>();
        globalNeededNorms = new HashSet<>();
        productNeededNorms = new HashSet<>();
    }

    public void check(ObservableList<Product> products, boolean isEqTypeFiltered) {
        clearData();
        for (Product product : products) {
            checkExistingCertificates(product, isEqTypeFiltered);
            checkNorms(product);
        }
        useTemporaryTypeId = false;
    }

    public void check(Product product, boolean isEqTypeFiltered) {
        clearData();
        checkExistingCertificates(product, isEqTypeFiltered);
        checkNorms(product);
        useTemporaryTypeId = false;
    }

    private void clearData() {
        certsErr = 0;
        resultTableItems.clear();
        satisfiedNorms.clear();
    }

    private void checkExistingCertificates(Product product, boolean isEqTypeFiltered) {
        ArrayList<String> prodNames = new ArrayList<>();

        int results = 0;
//        boolean isHardMode =  true;

//        do {
            for (Certificate cert : CoreModule.getCertificates().getCertificates()) {//check all certificates

                prodNames.clear();//forming comparing product values (article / material)
                prodNames.add(Utils.toEN(product.getArticle()).toUpperCase());
                if (cert.isMaterialMatch()) prodNames.add(Utils.toEN(product.getMaterial()).toUpperCase());

                int contentTypeId;

                for (CertificateContent content : cert.getContent()) {//check all content
                    contentTypeId = CoreModule.getProductTypes().getIDbyType(content.getEquipmentType());

                    boolean fullNameMatch = cert.isFullNameMatch();
                    boolean productTypeNotDefined = product.getType_id() == 0;
                    boolean productTypeMatches = product.getType_id() > 0 && product.getType_id() == contentTypeId;
                    boolean changedProductTypeMatch = temporaryTypeId > 0 && temporaryTypeId == contentTypeId;
                    boolean changedProductTypeNotDefined = temporaryTypeId == 0;
                    boolean typeNotDefined = productTypeNotDefined || changedProductTypeNotDefined;

                    boolean usualWay = !useTemporaryTypeId && (productTypeNotDefined || productTypeMatches);
                    boolean temporaryWay = useTemporaryTypeId && (changedProductTypeNotDefined || changedProductTypeMatch);

                    if (fullNameMatch || usualWay || temporaryWay) {
                        for (String contentName : Utils.stringToList(content.getEquipmentName())) {//check all content names

                            for (String prod : prodNames) {//compare product article / material with certificate content

                                String contentValue = getContentValueForComparing(cert, contentName);

                                if (prod.matches(contentValue)) {
                                    if (!content.getEquipmentName().isEmpty() && !fullNameMatch && typeNotDefined && isEqTypeFiltered && !isMatchEquipTypeName(product, content)) continue;

                                    results++;
                                    String status = getStatusString(product, cert);

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

//            if (results == 0) {
//                isHardMode = !isHardMode;
//                isEqTypeFiltered = !isEqTypeFiltered;
//            }
//        } while (results == 0 && !isHardMode);
//        } while (results == 0 && !isEqTypeFiltered);
    }

    private String getStatusString(Product product, Certificate cert) {
        String status = "";
        if ((new Date()).after(Utils.getDate(cert.getExpirationDate()))) {//expired
            status = NOT_OK + EXPIRED + cert.getExpirationDate();
            certsErr++;
        }

        boolean productHasCert = !product.getCountry().trim().isEmpty();
        boolean certCountryMatch = cert.getCountries().toLowerCase().contains(product.getCountry().toLowerCase());
        boolean certAllCountries = cert.getCountries().contains(ALL_COUNTRIES);

        if (!certAllCountries && productHasCert && !certCountryMatch) {//no country
            status = status.isEmpty() ? NOT_OK + BAD_COUNTRY + " (" + product.getCountry().toUpperCase() + ")"
                    : status + BAD_COUNTRY + " (" + product.getCountry().toUpperCase() + ")";
            certsErr++;
        }

        if (status.isEmpty()) {
            status = OK + ", до " + cert.getExpirationDate();
        }
        return status;
    }

    private boolean isMatchEquipTypeName(Product product, CertificateContent content) {
        String[] productDescriptionParts = product.getDescriptionru().split("\\s");
        for (String partOfDesc : productDescriptionParts) {
            String searchPart;
            if (partOfDesc.length() > 3) {
                searchPart = partOfDesc.substring(0, 3/*partOfDesc.length() - 1).toLowerCase()*/).toLowerCase();

                if (content.getEquipmentType().toLowerCase().contains(searchPart)) {
                    return true;
                }
            }

        }
        return false;
    }

    private String getContentValueForComparing(Certificate cert, String contentName) {
        String contentValue;
        if (contentName.matches(".+[x]{2,}.*")) {
            contentValue = contentName.replaceAll("[x]{2,}", ".*").trim().toUpperCase();
        } else if (!cert.isFullNameMatch()) {
            contentValue = contentName.trim().toUpperCase() + "\\s*\\d+.*";
        } else contentValue = contentName.trim().toUpperCase();

        contentValue = contentValue.replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");
        return contentValue;
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

        boolean certNotNeeded = false;

        normsForChecking.removeAll(satisfiedNorms);
        if (satisfiedNorms.contains(1)) normsForChecking.remove(9);//НВО СС заменяет НВО ДС
        if (satisfiedNorms.contains(3)) normsForChecking.remove(11);//ЭМС СС заменяет ЭМС ДС

        for (int normIndex : normsForChecking) {
            String shortName = CoreModule.getRequirementTypes().getRequirementByID(normIndex).getShortName();
            CertificateVerificationItem cvi = new CertificateVerificationItem(shortName);
            if (shortName.equals(CERT_NO_NEEDED)) {
                cvi.setStatus(OK);
                certNotNeeded = true;
            } else {
                certsErr++;
            }
            resultTableItems.add(cvi);
        }

        if (certNotNeeded) {
            checkStatusResult = STATUS_OK;
        } else if (certsErr > 0) {
            checkStatusResult = CERT_WITH_ERR;
        } else if (normsForCheckingCount > 0) {
            checkStatusResult = STATUS_OK;
        } else {
            checkStatusResult = NO_DATA;
        }
    }

    public String getCheckStatusResultStyle() {
        switch (checkStatusResult) {
            case NO_CERT:
            case CERT_WITH_ERR:
                return "itemStrikethroughRed";
            case PART_OF_CERT:
                return "itemStrikethroughBrown";
            case STATUS_OK:
                return "itemStrikethroughGreen";
            default:
                return "itemStrikethroughBlack";
        }
    }

    public TreeSet<CertificateVerificationItem> getResultTableItems() {
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
