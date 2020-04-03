package ui_windows.options_window.certificates_editor.certificate_content_editor;

import core.CoreModule;
import core.Dialogs;
import database.CertificatesContentDB;
import database.ProductTypesDB;
import ui_windows.options_window.certificates_editor.Certificate;
import ui_windows.product.ProductType;

import java.util.ArrayList;
import java.util.List;

public class CertificateContentActions {

    public static void addItem(CertificatesContentTable certificatesContentTable) {
        CertificateContent newItem = new CertificateContent(0, 0, "", "", "");
        CoreModule.getCertificatesContentTable().getTableView().getItems().add(newItem);
        certificatesContentTable.setEditMode(CoreModule.getCertificatesContentTable().getTableView().getItems().indexOf(newItem));
    }

    public static void saveContent(Certificate cert) {
        List<CertificateContent> changedContent = new ArrayList<>();
        List<CertificateContent> newContent = new ArrayList<>();

        for (CertificateContent cc : CoreModule.getCertificatesContentTable().getTableView().getItems()) {
            ProductType productType = CoreModule.getProductTypes().getProductTypeByType(cc.getEquipmentType().trim());
            if (productType == null) {
                productType = new ProductType(0, cc.getEquipmentType(), cc.getTnved());
                CoreModule.getProductTypes().addItem(productType);
                cc.setEqTypeId(productType.getId());
            } else if (!productType.getTen().equals(cc.getTnved().trim())) {
                productType.setTen(cc.getTnved().trim());
                new ProductTypesDB().updateData(productType);
            }

            if (cc.getId() != 0 && cc.wasChanged()) {//content can be changed

                changedContent.add(cc);
                cc.setWasChanged(false);
            } else {//there is a new content
                cc.setCertId(cert.getId());//get certificate id
                newContent.add(cc);
                cc.setWasChanged(false);

                cert.getContent().add(cc);
                CoreModule.getCertificatesContent().addItem(cc);
            }
        }

        if (changedContent.size() > 0) {
            new CertificatesContentDB().updateData(changedContent);
        }

        if (newContent.size() > 0) {
            new CertificatesContentDB().putData(newContent);
        }
    }

    public static void deleteContent(CertificateContent cc) {
        if (Dialogs.confirm("Удаление записи", "Действительно желаете удалить запись?")) {
            ArrayList<CertificateContent> alcc = new ArrayList<>();
            alcc.add(cc);

            if (new CertificatesContentDB().deleteData(alcc)) {//remove from DB
                System.out.println("certificate content deleted");
//                CoreModule.getProductTypes().delete();
                CoreModule.getCertificates().removeContent(cc); //remove from cert
                CoreModule.getCertificatesContent().delete(cc); //remove from contents
            }
            CoreModule.getCertificatesContentTable().getTableView().getItems().remove(cc); //remove from table
            CoreModule.getCertificatesContentTable().getTableView().refresh();
        }

    }
}

