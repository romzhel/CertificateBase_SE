package ui_windows.options_window.certificates_editor.certificate_content_editor.certificatesChecker;

import core.CoreModule;
import ui_windows.main_window.Product;
import ui_windows.options_window.certificates_editor.Certificate;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import ui_windows.options_window.product_lgbk.LgbkAndParent;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class CertificatesChecker {
    public final static String NOT_OK = "НЕ ОК";
    public final static String OK = "ОК";
    public final static String EXPIRED = ", истек";
    public final static String BAD_COUNTRY = ", нет страны";
    private ArrayList<CertificateVerificationItem> resultTableItems;
    private ArrayList<Integer> satisfiedNorms;
    private HashSet<Integer> globalNeededNorms;
    private HashSet<Integer> productNeededNorms;


    public CertificatesChecker() {
        resultTableItems = new ArrayList<>();
        satisfiedNorms = new ArrayList<>();
        globalNeededNorms = new HashSet<>();
        productNeededNorms = new HashSet<>();
    }

    public void check(Product product) {
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

            for (CertificateContent content : cert.getContent()) {//check all content
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
                            }

                            if (product.getCountry().trim().length() > 0 && !cert.getCountries().toLowerCase().//no country
                                    contains(product.getCountry().toLowerCase())) {
                                status = status.isEmpty() ? NOT_OK + BAD_COUNTRY + " (" + product.getCountry().toUpperCase() + ")"
                                        : status + BAD_COUNTRY + " (" + product.getCountry().toUpperCase() + ")";
                            }

                            if (status.isEmpty()) status = OK + ", до " + cert.getExpirationDate();

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

    private void checkNorms(Product product) {
        globalNeededNorms.clear();
        productNeededNorms.clear();

        HashSet<Integer> normsForChecking = new HashSet<>();

        productNeededNorms.addAll(product.getNormsList().getIntegerItems());
        LgbkAndParent lgbkAndParent = CoreModule.getProductLgbkGroups().getLgbkAndParent(
                new ProductLgbk(product.getLgbk(), product.getHierarchy()));
        globalNeededNorms.addAll(CoreModule.getProductLgbkGroups().getRootNode().getNormsList().getIntegerItems());
        globalNeededNorms.addAll(lgbkAndParent.getLgbkParent().getNormsList().getIntegerItems());
        globalNeededNorms.addAll(lgbkAndParent.getLgbkItem().getNormsList().getIntegerItems());

        if (product.getNormsMode() == NormsList.ADD_TO_GLOBAL) {
            normsForChecking.addAll(globalNeededNorms);
        }
        normsForChecking.addAll(productNeededNorms);

        normsForChecking.removeAll(satisfiedNorms);
        for (int normIndex : normsForChecking) {
            String shortName = CoreModule.getRequirementTypes().getRequirementByID(normIndex).getShortName();
            resultTableItems.add(new CertificateVerificationItem(shortName));
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
}
