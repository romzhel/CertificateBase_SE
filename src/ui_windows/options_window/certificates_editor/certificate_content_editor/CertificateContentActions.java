package ui_windows.options_window.certificates_editor.certificate_content_editor;

import core.CoreModule;
import core.Dialogs;
import database.CertificatesContentDB;
import ui_windows.options_window.certificates_editor.Certificate;

import java.util.ArrayList;

public class CertificateContentActions {

    public static void addItem(CertificatesContentTable certificatesContentTable) {
        CertificateContent newItem = new CertificateContent(0, 0, "введите значение", "введите значение", "введите значение");
        CoreModule.getCertificatesContentTable().getTableView().getItems().add(newItem);
        certificatesContentTable.setEditMode(CoreModule.getCertificatesContentTable().getTableView().getItems().indexOf(newItem));
    }

    public static void saveContent(Certificate cert) {
        for (CertificateContent cc : CoreModule.getCertificatesContentTable().getTableView().getItems()) {

            CoreModule.getProductTypes().getID(cc);//check is it new productType?

            if (cc.getId() != 0) {//content can be changed
                if (cc.isWasChanged()) {//content was changed
                    new CertificatesContentDB().updateData(cc); //update in DB
                    cc.setWasChanged(false);
                }
            } else {//there is a new content
                cc.setCertId(cert.getId());//get certificate id
                cc.setWasChanged(false);

                new CertificatesContentDB().putData(cc); //write to DB add
//                System.out.println("cc = " + cc.toString());

                cert.getContent().add(cc);
                CoreModule.getCertificatesContent().addItem(cc);
            }
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
                CoreModule.getCertificatesContentTable().getTableView().getItems().remove(cc); //remove from table
            }
        }

    }
}

