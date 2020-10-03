package ui_windows.options_window.certificates_editor.certificate_content_editor;

import core.Initializable;
import database.CertificatesContentDB;
import ui_windows.product.ProductTypes;
import ui_windows.product.Products;

import java.util.*;

public class CertificatesContent implements Initializable {
    private static CertificatesContent instance;
    private List<CertificateContent> content;
    private Map<String, Set<CertificateContent>> mapContent;

    private CertificatesContent() {
    }

    public static CertificatesContent getInstance() {
        if (instance == null) {
            instance = new CertificatesContent();
        }
        return instance;
    }

    @Override
    public void init() {
        content = new CertificatesContentDB().getData();
        mapContent = new HashMap<>();
        for (CertificateContent cont : content) {
            String[] names = cont.getEquipmentName().split(",");
            for (String name : names) {
                name = name.length() > 2 ? name.substring(0, 2) : name.trim();
                mapContent.computeIfPresent(name, (s, certificateContents) -> {
                    Set<CertificateContent> resultSet = new HashSet<>(certificateContents);
                    resultSet.add(cont);
                    return resultSet;
                });
                mapContent.putIfAbsent(name, Collections.singleton(cont));
            }
        }
    }

    public List<CertificateContent> getItems() {
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
        if (!content.remove(item)) {
            CertificateContent forDelete = getById(item.getId());
            if (forDelete != null) {
                content.remove(forDelete);
            }
        }

        if (!isProductTypeUsed(item.getProductType().getType()) &&  //product type don't used any more
                !Products.getInstance().isProductTypeIsUsed(item.getProductType().getType())) {
            ProductTypes.getInstance().delete(item.getProductType().getType());
        }
    }

    public CertificateContent getById(int id) {
        for (CertificateContent cc : content) {
            if (cc.getId() == id) {
                return cc;
            }
        }

        return null;
    }

    public Map<String, Set<CertificateContent>> getMapContent() {
        return mapContent;
    }
}
