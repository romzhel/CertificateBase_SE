package ui_windows.options_window.certificates_editor;

import core.CoreModule;
import core.Dialogs;
import database.CertificatesDB;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import utils.Utils;

import java.util.ArrayList;

public class Certificates {
    //    private CertificatesChecker certificatesChecker;
    private ArrayList<Certificate> certificates;

    public Certificates() {
//        certificatesChecker = new CertificatesChecker();
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
        if (cert != null && !cert.getContent().remove(cc)) {
            cert.getContent().remove(CoreModule.getCertificatesContent().getById(cc.getId()));
        }
    }

    public Certificate getCertificateByID(int id) {
        for (Certificate cert : certificates) {
            if (cert.getId() == id) return cert;
        }
        return null;
    }

//    public CertificatesChecker getCertificatesChecker() {
//        return certificatesChecker;
//    }
}
