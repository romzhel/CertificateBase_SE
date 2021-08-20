package ui_windows.options_window.certificates_editor;

import core.Initializable;
import database.CertificatesDB;
import lombok.extern.log4j.Log4j2;
import ui.Dialogs;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificatesContent;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class Certificates implements Initializable {
    private static Certificates instance;
    //    private CertificatesChecker certificatesChecker;
    private ArrayList<Certificate> certificates;

    private Certificates() {
//        certificatesChecker = new CertificatesChecker();

    }

    public static Certificates getInstance() {
        if (instance == null) {
            instance = new Certificates();
        }
        return instance;
    }

    @Override
    public void init() {
        certificates = new CertificatesDB().getData();
    }

    public ArrayList<Certificate> getCertificates() {
        return certificates;
    }

    public void remove(Certificate cert) {
        certificates.remove(cert);
        CertificatesTable.getInstance().getTableView().getItems().remove(cert);
        CertificatesTable.getInstance().getTableView().refresh();
    }

    public boolean hasDoubles(Certificate certificate) {
        for (Certificate cert : certificates) {
            if (cert.getName().equals(certificate.getName()) && cert.getId() != certificate.getId()) {
                log.warn("certificate saving with existing name '{}'", certificate.getName());
                Dialogs.showMessage("Повторяющееся значения", "Сертификат с таким именем уже существует");
                return true;
            }
        }
        return false;
    }

    public List<Certificate> getItems() {
        return certificates;
    }

    public void addItem(Certificate certificate) {
        log.trace("add cert to certificates: {}", certificate);
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
            cert.getContent().remove(CertificatesContent.getInstance().getById(cc.getId()));
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
