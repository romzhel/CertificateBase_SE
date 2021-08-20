package ui_windows.options_window.certificates_editor.certificate_content_editor;

import database.CertificatesContentDB;
import lombok.extern.log4j.Log4j2;
import ui.Dialogs;
import ui_windows.options_window.certificates_editor.Certificate;
import ui_windows.product.ProductType;
import ui_windows.product.ProductTypes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j2
public class CertificateContentActions {

    private static HashSet<CertificateContent> forDelete = new HashSet<>();

    public static CertificateContent addItem(CertificatesContentTable certificatesContentTable) {
        CertificateContent newItem = new CertificateContent(0, 0, new ProductType(0, "", ""), "");
        CertificatesContentTable.getInstance().getTableView().getItems().add(newItem);
        certificatesContentTable.setEditMode(CertificatesContentTable.getInstance().getTableView().getItems().indexOf(newItem));

        return newItem;
    }

    public static void saveContent(Certificate cert) {
        log.trace("save content for certificate: {}", cert);
        Set<CertificateContent> changedContent = new HashSet<>();
        Set<CertificateContent> newContent = new HashSet<>();

        List<CertificateContent> tableItems = CertificatesContentTable.getInstance().getTableView().getItems();
        for (CertificateContent cc : tableItems) {
            log.trace("saving cert content: {} from {}: {}", tableItems.indexOf(cc), tableItems.size(), cc);

            ProductType inputProductType = cc.getProductType();
            log.trace("cert content product type {}", inputProductType);
            if (inputProductType.isWasChanged()) {//changed or new
                inputProductType = ProductTypes.getInstance().addItem(inputProductType);
                inputProductType.setWasChanged(false);
                cc.setProductType(inputProductType);
            }
            log.trace("treated product type: {}", inputProductType);

            log.trace("treating cert content: {}", cc);
            if (cc.isWasChanged()) {
                if (cc.getId() == 0) {//there is a new content
                    cc.setCertId(cert.getId());//get certificate id
                    CertificatesContent.getInstance().addItem(cc);
                    newContent.add(cc);
                } else {//content can be changed
                    changedContent.add(cc);
                }

                cc.setWasChanged(false);
            }
            log.trace("treated cert content: {}", cc);

            cert.getContent().add(cc);
        }

        log.trace("content new/changed/delete: {}/{}/{}", newContent.size(), changedContent.size(), forDelete.size());

        if (changedContent.size() > 0) {
            new CertificatesContentDB().updateData(changedContent);
        }

        if (newContent.size() > 0) {
            new CertificatesContentDB().putData(newContent);
        }

        if (forDelete.size() > 0) {
            if (new CertificatesContentDB().deleteData(forDelete)) {//remove from DB
                log.trace("certificate content deleted from DB");
//                CoreModule.getProductTypes().delete();
                for (CertificateContent cc : forDelete) {
//                    Certificates.getInstance().removeContent(cc); //remove from cert
                    CertificatesContent.getInstance().delete(cc); //remove from contents
                }
            }
        }
    }

    public static void deleteContent(CertificateContent cc) {
        if (Dialogs.confirm("Удаление записи", "Действительно желаете удалить запись?")) {
            CertificatesContentTable.getInstance().getTableView().getItems().remove(cc); //remove from table
            CertificatesContentTable.getInstance().getTableView().refresh();
            forDelete.add(cc);
        }
    }
}

