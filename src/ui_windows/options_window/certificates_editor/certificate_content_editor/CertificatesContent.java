package ui_windows.options_window.certificates_editor.certificate_content_editor;

import core.CoreModule;
import database.CertificatesContentDB;

import java.util.ArrayList;

public class CertificatesContent {
    private ArrayList<CertificateContent> content;

    public CertificatesContent() {
        content = new CertificatesContentDB().getData();
    }

    public ArrayList<CertificateContent> getItems() {
        return content;
    }

    public ArrayList<CertificateContent> getContentByCertID(int certID) {
        ArrayList<CertificateContent> certContent = new ArrayList<>();

        for (CertificateContent cc : content) {
            if (cc.getCertId() == certID) certContent.add(cc);
        }

        return certContent;
    }

    public void addItem(CertificateContent newContent) {
        content.add(newContent);
    }

    public boolean isProductTypeUsed(String type) {
        for (CertificateContent cc : content) {
            if (cc.getProductType().getType().equals(type)) return true;
        }
        return false;
    }

    public void delete(ArrayList<CertificateContent> listToDelete) {
        if (listToDelete == null) return;

        for (CertificateContent cc : listToDelete) {
            delete(cc);
        }
    }

    public void delete(CertificateContent item) {
        content.remove(item);

        if (!isProductTypeUsed(item.getProductType().getType()) &&  //product type don't used any more
                !CoreModule.getProducts().isProductTypeIsUsed(item.getProductType().getType())) {
            CoreModule.getProductTypes().delete(item.getProductType().getType());
        }
    }
}
