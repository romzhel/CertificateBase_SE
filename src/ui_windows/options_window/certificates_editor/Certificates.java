package ui_windows.options_window.certificates_editor;

import core.CoreModule;
import core.Dialogs;
import database.CertificatesDB;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import ui_windows.main_window.Product;
import ui_windows.options_window.certificates_editor.certificate_content_editor.certificatesChecker.CertificateVerificationItem;
import ui_windows.options_window.certificates_editor.certificate_content_editor.certificatesChecker.CertificatesChecker;
import utils.Utils;

import java.util.ArrayList;
import java.util.Date;

public class Certificates {
    private CertificatesChecker certificatesChecker;
    private ArrayList<Certificate> certificates;

    public Certificates() {
        certificatesChecker = new CertificatesChecker();
        certificates = new CertificatesDB().getData();
    }

    public ArrayList<Certificate> getCertificates() {
        return certificates;
    }

    public void remove(Certificate cert) {
        certificates.remove(cert);
        CoreModule.getCertificatesTable().getTableView().getItems().remove(cert);
        CoreModule.getCertificatesTable().getTableView().refresh();
    }

    public boolean hasDoubles(Certificate certificate) {
        for (Certificate cert : certificates) {
            if (cert.getId() != certificate.getId())
                if (cert.getName().equals(certificate.getName())) {
                    Dialogs.showMessage("Повторяющееся значения", "Cертификат с таким именем уже существует");
                    return true;
                }
        }
        return false;
    }

    public ArrayList<Certificate> getItems() {
        return certificates;
    }

    public void addItem(Certificate certificate) {
        certificates.add(certificate);
    }

    public boolean isNormUsed(int id) {
        String usedCert = "";
        for (Certificate cert : certificates) {
            if (Utils.stringToList(cert.getNorms()).indexOf(Integer.toString(id)) >= 0) {
                usedCert = usedCert.concat("\n").concat(cert.getName());
            }
        }

        if (usedCert.length() > 0) {
            Dialogs.showMessage("Удаление типа сертификата", "Тип сертификата не может быть удалён, " +
                    "так как он используется в следующих сертификатах:\n" + usedCert);
            return true;
        }
        return false;
    }

    public void removeContent(CertificateContent cc) {
        Certificate cert = getCertificateByID(cc.getCertId());
        if (cert != null) {
            CoreModule.getCertificatesContent().delete(new ArrayList<CertificateContent>());
            cert.getContent().remove(cc);
        }
    }

    public Certificate getCertificateByID(int id) {
        for (Certificate cert : certificates) {
            if (cert.getId() == id) return cert;
        }
        return null;
    }

    /*public ArrayList<CertificateVerificationItem> checkCertificates(Product product) {
        ArrayList<CertificateVerificationItem> result = new ArrayList<>();
        ArrayList<String> prodNames = new ArrayList<>();

        for (Certificate cert : certificates) {//check all certificates

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
    }*/
}
