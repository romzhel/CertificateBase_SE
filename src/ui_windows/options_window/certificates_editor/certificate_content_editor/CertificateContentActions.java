package ui_windows.options_window.certificates_editor.certificate_content_editor;

import core.CoreModule;
import core.Dialogs;
import database.CertificatesContentDB;
import database.ProductTypesDB;
import ui_windows.options_window.certificates_editor.Certificate;
import ui_windows.product.Product;
import ui_windows.product.ProductType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CertificateContentActions {

    public static void addItem(CertificatesContentTable certificatesContentTable) {
        CertificateContent newItem = new CertificateContent(0, 0, new ProductType(0, "", ""), "");
        CoreModule.getCertificatesContentTable().getTableView().getItems().add(newItem);
        certificatesContentTable.setEditMode(CoreModule.getCertificatesContentTable().getTableView().getItems().indexOf(newItem));
    }

    public static void saveContent(Certificate cert) {
        Set<CertificateContent> changedContent = new HashSet<>();
        Set<CertificateContent> newContent = new HashSet<>();

        for (CertificateContent cc : CoreModule.getCertificatesContentTable().getTableView().getItems()) {
            ProductType inputProductType = cc.getProductType();
            ProductType existingProductType;

            if (inputProductType.wasChanged()) {//changed or new
                existingProductType = CoreModule.getProductTypes().getByEqType(inputProductType.getType());

                if (existingProductType != null && inputProductType.getType().equals(existingProductType.getType())) {
                    if (!inputProductType.getTen().equals(existingProductType.getTen())) {
                        existingProductType.setTen(inputProductType.getTen());
                        new ProductTypesDB().updateData(existingProductType);
                    }

                    if (inputProductType.getId() != existingProductType.getId()) {
                        cc.setProductType(existingProductType);
                        changedContent.add(cc);
                    }
                } else {
                    CoreModule.getProductTypes().addItem(inputProductType);
                }

                inputProductType.setWasChanged(false);
            }

            if (cc.getId() == 0) {//there is a new content
                cc.setCertId(cert.getId());//get certificate id
                newContent.add(cc);

                cert.getContent().add(cc);
                CoreModule.getCertificatesContent().addItem(cc);
            } else if (cc.wasChanged()) {//content can be changed
                changedContent.add(cc);
                cc.setWasChanged(false);
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

