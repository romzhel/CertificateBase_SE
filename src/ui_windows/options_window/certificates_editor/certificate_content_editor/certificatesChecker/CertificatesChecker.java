package ui_windows.options_window.certificates_editor.certificate_content_editor.certificatesChecker;

import core.CoreModule;
import ui_windows.main_window.Product;
import ui_windows.options_window.certificates_editor.Certificate;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import utils.Utils;

import java.util.ArrayList;
import java.util.Date;

public class CertificatesChecker {
    public final static String NOT_OK = "НЕ ОК";
    public final static String OK = "ОК";
    public final static String EXPIRED = ", истек";
    public final static String BAD_COUNTRY = ", нет страны";
    private ArrayList<CertificateVerificationItem> checkingResult;
    private ArrayList<String> allNorms;

    public CertificatesChecker(){
        checkingResult = new ArrayList<>();
    }

    public ArrayList<CertificateVerificationItem> check(Product product) {
        ArrayList<CertificateVerificationItem> result = new ArrayList<>();
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

                        contentValue = contentValue.replaceAll("\\(","\\\\(")
                                .replaceAll("\\)","\\\\)");

                        if (prod.matches(contentValue)) {
                            if ((new Date()).after(Utils.getDate(cert.getExpirationDate()))) {//expired
                                status = "НЕ ОК, истек " + cert.getExpirationDate();
                            }

                            if (product.getCountry().trim().length() > 0 && !cert.getCountries().toLowerCase().//no country
                                    contains(product.getCountry().toLowerCase())) {
                                status = status == "" ? "НЕ ОК, нет страны (" + product.getCountry().toUpperCase() + ")"
                                        : status + ", нет страны (" + product.getCountry().toUpperCase() + ")";
                            }

                            if (status == "") status = "OK, до " + cert.getExpirationDate();

                            String norms = CoreModule.getRequirementTypes().getNormsShortNamesByIds(cert.getNorms());
                            result.add(new CertificateVerificationItem(norms, contentName, content.getEquipmentType(),
                                    cert.getFileName(), status, cert.getExpirationDate(), cert, product));
                        }
                    }
                }
            }
        }

        return result;
    }
}
