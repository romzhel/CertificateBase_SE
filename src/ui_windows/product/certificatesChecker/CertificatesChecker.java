package ui_windows.product.certificatesChecker;

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

import static ui_windows.product.certificatesChecker.CheckStatusResult.*;

public class CertificatesChecker {
    public final static String NOT_OK = "НЕ ОК";
    public final static String OK = "ОК";
    public final static String EXPIRED = ", истек ";
    public final static String BAD_COUNTRY = ", нет страны ";
    public final static String CERT_NO_NEEDED = "Сертификаты не требуются";
    public final static String ALL_COUNTRIES = "--";
    private TreeSet<CertificateVerificationItem> resultTableItems;
    private CheckStatusResult checkStatusResult = NO_DATA;
    private ArrayList<Integer> satisfiedNorms;
    private HashSet<Integer> globalNeededNorms;
    private HashSet<Integer> productNeededNorms;
    private int certsErr;
    private TreeSet<String> productTypes;

    public CertificatesChecker(Product product) {
        init();

        checkExistingCertificates(product, new CheckParameters());
        checkNorms(product);
    }

    public CertificatesChecker(Product product, CheckParameters checkParameters) {
        init();

        checkExistingCertificates(product, checkParameters);
        checkNorms(product);
    }

    public CertificatesChecker(ObservableList<Product> checkedProducts, CheckParameters checkParameters) {
        init();

        for (Product product : checkedProducts) {
            checkExistingCertificates(product, checkParameters);
            checkNorms(product);
        }
    }

    private void init() {
        resultTableItems = new TreeSet<>((o1, o2) -> {
            String o1c = o1.getNorm().concat(o1.getMatchedPart()).concat(o1.getFile()).concat(o1.getStatus());
            String o2c = o2.getNorm().concat(o2.getMatchedPart()).concat(o2.getFile()).concat(o2.getStatus());
            return o1c.compareTo(o2c);
        });
        satisfiedNorms = new ArrayList<>();
        globalNeededNorms = new HashSet<>();
        productNeededNorms = new HashSet<>();
        productTypes = new TreeSet<>();
    }

    private void checkExistingCertificates(Product product, CheckParameters checkParameters) {
        ArrayList<String> prodNames = new ArrayList<>();

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
                boolean changedProductTypeMatch = checkParameters.getTemporaryTypeId() > 0 && checkParameters.getTemporaryTypeId() == contentTypeId;
                boolean changedProductTypeNotDefined = checkParameters.getTemporaryTypeId() == 0;

                boolean usualWay = !checkParameters.isUseTemporaryTypeId() && (productTypeNotDefined || productTypeMatches);
                boolean temporaryWay = checkParameters.isUseTemporaryTypeId() && (changedProductTypeNotDefined || changedProductTypeMatch);

                if (fullNameMatch || usualWay || temporaryWay) {
                    for (String contentName : Utils.stringToList(content.getEquipmentName())) {//check all content names

                        for (String prod : prodNames) {//compare product article / material with certificate content

                            String contentValue = getContentValueForComparing(cert, contentName);
                            prod = prod.replaceAll("\\s", "");

                            if (prod.matches(contentValue)) {
                                if (content.getEquipmentType() != null && !content.getEquipmentType().isEmpty()) {
                                    productTypes.add(content.getEquipmentType());
                                }

                                boolean typeNotDefined = (!checkParameters.isUseTemporaryTypeId() && productTypeNotDefined) ||
                                        (checkParameters.isUseTemporaryTypeId() && changedProductTypeNotDefined);
                                if (!content.getEquipmentName().isEmpty() && !fullNameMatch && typeNotDefined && checkParameters.isEqTypeFiltered() && !isMatchEquipTypeName(product, content))
                                    continue;

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
        String[] productDescriptionParts = product.getDescriptionRu().replaceAll("[\\(\\)\\[\\]]", "").split("\\s");
        String certEqType = content.getEquipmentType().toLowerCase();

        for (String partOfDesc : productDescriptionParts) {
            String searchPart;
            if (partOfDesc.length() > 3) {
                searchPart = partOfDesc.substring(0, 3).toLowerCase();

                if (certEqType.startsWith(searchPart) || certEqType.matches(".*\\s" + searchPart + ".*")) {
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
        } else contentValue = contentName.trim().replaceAll("\\s", "").toUpperCase();

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
            checkStatusResult = STATUS_NOT_OK;
        } else if (normsForCheckingCount > 0) {
            checkStatusResult = STATUS_OK;
        } else {
            checkStatusResult = NO_DATA;
        }
    }

    public String getCheckStatusResultStyle(ObservableList<String> styles) {
        if (styles != null) {
            styles.removeAll("itemStrikethroughRed", "itemStrikethroughBrown", "itemStrikethroughGreen",
                    "itemStrikethroughBlack");
        }
        switch (checkStatusResult) {
            case NO_CERT:
            case CERT_WITH_ERR:
            case STATUS_NOT_OK:
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

    public TreeSet<String> getProductTypes() {
        return productTypes;
    }
}
