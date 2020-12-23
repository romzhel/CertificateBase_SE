package ui_windows.product.certificatesChecker;

import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.options_window.certificates_editor.Certificate;
import ui_windows.options_window.certificates_editor.CertificateEditorWindowActions;
import ui_windows.options_window.certificates_editor.Certificates;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificatesContent;
import ui_windows.options_window.requirements_types_editor.RequirementTypes;
import ui_windows.product.Product;
import utils.Utils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ui_windows.product.certificatesChecker.CheckStatusResult.STATUS_OK;

public class CertificatesChecker {
    //                  Pattern pattern = Pattern.compile("^(\\d*?[A-Z]+?\\d*[A-Z]+)?(\\d+)?([0-9-/]*.*)$");
    public final static String NOT_OK = "НЕ ОК";
    public final static String OK = "ОК";
    public final static String EXPIRED = ", истек ";
    public final static String BAD_COUNTRY = ", нет страны ";
    public final static String CERT_NO_NEEDED = "Сертификаты не требуются";
    public final static String ALL_COUNTRIES = "--";
    private static final Logger logger = LogManager.getLogger(CertificatesChecker.class);
    //группа 1 - смешанное/буквенное значение, группа 2 - цифровое
    public static Pattern PATTERN = Pattern.compile("^(\\d*?[A-Z]*?\\d*[A-Z]+)?(\\d+)?([0-9-/]*.*)$");
    public static long minCheckTime = 1000;
    public static long maxCheckTime = 0;
    public static long averageCheckTime = 2;
    public static long minNormsTime = 1000;
    public static long maxNormsTime = 0;
    public static long averageNormsTime = 2;
    public static int count;
    private TreeSet<CertificateVerificationItem> resultTableItems;
    private CheckStatusResult checkStatusResult = STATUS_OK;
    private TreeSet<String> productTypes;

    public CertificatesChecker(Product product) {
        init();

        checkExistingCertificates(product, CheckParameters.getDefault());
        checkStatusResult = new NormsChecker(product, resultTableItems).getCheckStatusResult();
    }

    public CertificatesChecker(Product product, CheckParameters checkParameters) {
        init();
        long t0 = System.currentTimeMillis();
        checkExistingCertificates(product, checkParameters);
        long checkTime = System.currentTimeMillis() - t0;

        long t1 = System.currentTimeMillis();
        checkStatusResult = new NormsChecker(product, resultTableItems).getCheckStatusResult();
        long normsTime = System.currentTimeMillis() - t1;

        minCheckTime = Math.min(minCheckTime, checkTime);
        maxCheckTime = Math.max(maxCheckTime, checkTime);
        averageCheckTime = (averageCheckTime + checkTime) / 2;

        minNormsTime = Math.min(minNormsTime, normsTime);
        maxNormsTime = Math.max(maxNormsTime, normsTime);
        averageNormsTime = (averageNormsTime + normsTime) / 2;
    }

    public CertificatesChecker(List<Product> checkedProducts, CheckParameters checkParameters) {
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
        /*if (product.getArticle().matches("M3C700")) {
            System.out.println();
        }
*/
        int nameCount = 0;
        for (String prodName : new String[]{product.getArticle(), product.getMaterial()}) {
            nameCount++;

            String calcName = "";
            Matcher calcNameMatcher = PATTERN.matcher(prodName);
            if (calcNameMatcher.matches()) {
                calcName = calcNameMatcher.group(1) != null ? calcNameMatcher.group(1) : calcNameMatcher.group(2);
            }

            int originalLength = prodName.length();
            try {
                while (prodName.length() >= calcName.length()) {
                    for (CertificateContent cc : CertificatesContent.getInstance().getMapContent().getOrDefault(prodName, Collections.emptySet())) {
                        Certificate cert = Certificates.getInstance().getCertificateByID(cc.getCertId());

                        if (cert.getName().startsWith(CertificateEditorWindowActions.DELETED_MARK)) {
                            continue;
                        }

                        if (cert.isFullNameMatch() && prodName.length() < originalLength) {
                            continue;
                        }

                        if (!cert.isMaterialMatch() && nameCount > 1) {
                            continue;
                        }

                        boolean fullNameMatch = cert.isFullNameMatch() && prodName.length() == originalLength;
                        boolean productTypeNotDefined = product.getType_id() == 0;
                        boolean productTypeDefinedAndMatches = product.getType_id() > 0 &&
                                product.getType_id() == cc.getProductType().getId();
                        boolean changedProductTypeNotDefined = checkParameters.getTemporaryTypeId() == 0;
                        boolean changedProductTypeDefinedAndMatch = checkParameters.getTemporaryTypeId() > 0 &&
                                checkParameters.getTemporaryTypeId() == cc.getProductType().getId();

                        boolean usualWay = !checkParameters.isUseTemporaryTypeId() && (productTypeNotDefined || productTypeDefinedAndMatches);
                        boolean temporaryWay = checkParameters.isUseTemporaryTypeId() && (changedProductTypeNotDefined || changedProductTypeDefinedAndMatch);

                        if (fullNameMatch || usualWay || temporaryWay) {
                            if (cc.getProductType().getType() != null && !cc.getProductType().getType().isEmpty()) {
                                productTypes.add(cc.getProductType().getType());
                            }

                            boolean typeNotDefined = (!checkParameters.isUseTemporaryTypeId() && productTypeNotDefined) ||
                                    (checkParameters.isUseTemporaryTypeId() && changedProductTypeNotDefined);
                            if (!cc.getEquipmentName().isEmpty() && !fullNameMatch && typeNotDefined &&
                                    checkParameters.isEqTypeFiltered() && !isMatchEquipTypeName(product, cc))
                                continue;

                            count++;

                            String status = getStatusString(product, cert);

                            //                                foundNorms.addAll(Utils.getNumberALfromStringEnum(cert.getNorms()));
                            String norms = RequirementTypes.getInstance().getNormsShortNamesByIds(cert.getNorms());

                            for (String normName : norms.split("\\,")) {
                                resultTableItems.add(new CertificateVerificationItem(normName.trim(), prodName,
                                        cc.getProductType().getType(), cert.getFileName(), status,
                                        cert.getExpirationDate(), cert, product));
                            }
                        }
                    }
                    prodName = prodName.substring(0, prodName.length() - 1);
                }
            } catch (Exception e) {
                logger.warn("ошибка поиска сертификатов для: '{}' - {}", prodName, e.getMessage());
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
        String certEqType = content.getProductType().getType().toLowerCase();

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
