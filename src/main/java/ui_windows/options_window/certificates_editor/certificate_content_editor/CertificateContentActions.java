package ui_windows.options_window.certificates_editor.certificate_content_editor;

import database.CertificatesContentDB;
import database.ProductTypesDB;
import ui.Dialogs;
import ui_windows.options_window.certificates_editor.Certificate;
import ui_windows.options_window.certificates_editor.Certificates;
import ui_windows.product.ProductType;
import ui_windows.product.ProductTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CertificateContentActions {

    public static void addItem(CertificatesContentTable certificatesContentTable) {
        CertificateContent newItem = new CertificateContent(0, 0, new ProductType(0, "", ""), "");
        CertificatesContentTable.getInstance().getTableView().getItems().add(newItem);
        certificatesContentTable.setEditMode(CertificatesContentTable.getInstance().getTableView().getItems().indexOf(newItem));
    }

    public static void saveContent(Certificate cert) {
        Set<CertificateContent> changedContent = new HashSet<>();
        Set<CertificateContent> newContent = new HashSet<>();

        for (CertificateContent cc : CertificatesContentTable.getInstance().getTableView().getItems()) {
            ProductType inputProductType = cc.getProductType();
            ProductType existingProductType;

            if (inputProductType.wasChanged()) {//changed or new
                existingProductType = ProductTypes.getInstance().getByEqType(inputProductType.getType());

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
                    ProductTypes.getInstance().addItem(inputProductType);
                }

                inputProductType.setWasChanged(false);
            }

            if (cc.getId() == 0) {//there is a new content
                cc.setCertId(cert.getId());//get certificate id
                newContent.add(cc);

                cert.getContent().add(cc);
                CertificatesContent.getInstance().addItem(cc);
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
            ArrayList<CertificateContent> alcc = new ArrayList<>(Collections.singletonList(cc));

            if (new CertificatesContentDB().deleteData(alcc)) {//remove from DB
                System.out.println("certificate content deleted from DB");
//                CoreModule.getProductTypes().delete();
                Certificates.getInstance().removeContent(cc); //remove from cert
                CertificatesContent.getInstance().delete(cc); //remove from contents
            }
            CertificatesContentTable.getInstance().getTableView().getItems().remove(cc); //remove from table
            CertificatesContentTable.getInstance().getTableView().refresh();
        }

    }
}

