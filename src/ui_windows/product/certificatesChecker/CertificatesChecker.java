package ui_windows.product.certificatesChecker;

import core.CoreModule;
import javafx.collections.ObservableList;
import ui_windows.options_window.certificates_editor.Certificate;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import ui_windows.product.Product;
import utils.Utils;

import java.util.*;

import static ui_windows.product.certificatesChecker.CheckStatusResult.*;

public class CertificatesChecker {
    public final static String NOT_OK = "НЕ ОК";
    public final static String OK = "ОК";
    public final static String EXPIRED = ", истек ";
    public final static String BAD_COUNTRY = ", нет страны ";
    public final static String CERT_NO_NEEDED = "Сертификаты не требуются";
    public final static String ALL_COUNTRIES = "--";
    private TreeSet<CertificateVerificationItem> resultTableItems;
    private CheckStatusResult checkStatusResult = STATUS_OK;
    private TreeSet<String> productTypes;

    public CertificatesChecker(Product product) {
        init();

        checkExistingCertificates(product, new CheckParameters());
        checkStatusResult = new NormsChecker(product, resultTableItems).getCheckStatusResult();
    }

    public CertificatesChecker(Product product, CheckParameters checkParameters) {
        init();

        checkExistingCertificates(product, checkParameters);
        checkStatusResult = new NormsChecker(product, resultTableItems).getCheckStatusResult();
    }

    public CertificatesChecker(ObservableList<Product> checkedProducts, CheckParameters checkParameters) {
        init();

        for (Product product : checkedProducts) {
            checkExistingCertificates(product, checkParameters);

            CheckStatusResult csr = new NormsChecker(product, resultTableItems).getCheckStatusResult();
            if (csr.getPrio() > checkStatusResult.getPrio()) {
                checkStatusResult = csr;
            }
        }
    }

    private void init() {
        resultTableItems = new TreeSet<>((o1, o2) -> {
            String o1c = o1.getFile().concat(o1.getProdType()).concat(o1.getMatchedPart()).concat(o1.getNorm()).concat(o1.getStatus());
            String o2c = o2.getFile().concat(o1.getProdType()).concat(o2.getMatchedPart()).concat(o2.getNorm()).concat(o2.getStatus());
            return o1c.compareTo(o2c);
        });
        productTypes = new TreeSet<>();
    }

    private void checkExistingCertificates(Product product, CheckParameters checkParameters) {
        TreeSet<String> prodNames = new TreeSet<>();
        for (Certificate cert : CoreModule.getCertificates().getCertificates()) {//check all certificates
            prodNames.clear();//forming comparing product values (article / material)
            prodNames.add(Utils.toEN(product.getArticle()).toUpperCase());
            if (cert.isMaterialMatch()) prodNames.add(Utils.toEN(product.getMaterial()).toUpperCase());

            for (CertificateContent content : cert.getContent()) {//check all content
                boolean fullNameMatch = cert.isFullNameMatch();
                boolean productTypeNotDefined = product.getType_id() == 0;
                boolean productTypeDefinedAndMatches = product.getType_id() > 0 && product.getType_id() == content.getEqTypeId();
                boolean changedProductTypeNotDefined = checkParameters.getTemporaryTypeId() == 0;
                boolean changedProductTypeDefinedAndMatch = checkParameters.getTemporaryTypeId() > 0 && checkParameters.getTemporaryTypeId() == content.getEqTypeId();

                boolean usualWay = !checkParameters.isUseTemporaryTypeId() && (productTypeNotDefined || productTypeDefinedAndMatches);
                boolean temporaryWay = checkParameters.isUseTemporaryTypeId() && (changedProductTypeNotDefined || changedProductTypeDefinedAndMatch);

                if (fullNameMatch || usualWay || temporaryWay) {
                    for (String contentName : Utils.stringToList(content.getEquipmentName())) {//check all content names

                        for (String prod : prodNames) {//compare product article / material with certificate content

                            if (isNamesMatches(prod, cert, contentName)) {//add prod type from certificate for allowing of selection
                                if (content.getEquipmentType() != null && !content.getEquipmentType().isEmpty()) {
                                    productTypes.add(content.getEquipmentType());
                                }

                                boolean typeNotDefined = (!checkParameters.isUseTemporaryTypeId() && productTypeNotDefined) ||
                                        (checkParameters.isUseTemporaryTypeId() && changedProductTypeNotDefined);
                                if (!content.getEquipmentName().isEmpty() && !fullNameMatch && typeNotDefined &&
                                        checkParameters.isEqTypeFiltered() && !isMatchEquipTypeName(product, content))
                                    continue;

                                String status = getStatusString(product, cert);

//                                foundNorms.addAll(Utils.getNumberALfromStringEnum(cert.getNorms()));
                                String norms = CoreModule.getRequirementTypes().getNormsShortNamesByIds(cert.getNorms());

                                for (String normName : norms.split("\\,")) {
                                    resultTableItems.add(new CertificateVerificationItem(normName.trim(), contentName,
                                            content.getEquipmentType(), cert.getFileName(), status,
                                            cert.getExpirationDate(), cert, product));

                                }
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
        }

        boolean productHasCountry = !product.getCountry().trim().isEmpty();
        boolean certCountryMatch = cert.getCountries().toLowerCase().contains(product.getCountry().toLowerCase());
        boolean certAllCountries = cert.getCountries().contains(ALL_COUNTRIES);

        if (!certAllCountries && productHasCountry && !certCountryMatch) {//no country
            status = status.isEmpty() ?
                    NOT_OK + BAD_COUNTRY + " (" + product.getCountry().toUpperCase() + ")" :
                    status + BAD_COUNTRY + " (" + product.getCountry().toUpperCase() + ")";
        }

        if (status.isEmpty()) {
            status = OK + ", до " + cert.getExpirationDate();
        }
        return status;
    }

    private boolean isMatchEquipTypeName(Product product, CertificateContent content) {
        String[] productDescriptionParts = product.getDescriptionru().replaceAll("[\\(\\)\\[\\]]", "").split("[\\s\\,]");
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

    private boolean isNamesMatches(String prodName, Certificate cert, String contentName) {
        prodName = prodName.replaceAll("[\\s\\-\\/]", "")/*.toUpperCase()*/;
        contentName = contentName.replaceAll("[\\s\\-\\/]", "")/*.toUpperCase()*/;
        String contentValue;

        if (contentName.matches(".+[x]{2,}.*")) {
            contentValue = contentName.replaceAll("[x]{2,}", ".*").trim().toUpperCase();
        } else {
            contentValue = contentName.trim().toUpperCase() + "\\d+.*";
        }

        contentValue = contentValue.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)");

        boolean namesWithNumbersAndMatches = prodName.matches(".*\\d+.*") && prodName.matches(contentValue);
        boolean namesTheSame = prodName.equals(contentName);
        boolean namesOnlyTextAndHaveTheSameBegin = prodName.matches("[A-Z]+") && prodName.startsWith(contentName);

        return namesTheSame || namesOnlyTextAndHaveTheSameBegin || namesWithNumbersAndMatches;
    }

    public TreeSet<CertificateVerificationItem> getResultTableItems() {
        return resultTableItems;
    }

    public CheckStatusResult getCheckStatusResult() {
        return checkStatusResult;
    }

    public TreeSet<String> getProductTypes() {
        return productTypes;
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
}
