package ui_windows.options_window.certificates_editor.certificate_content_editor;

import core.Initializable;
import database.CertificatesContentDB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.product.ProductTypes;
import ui_windows.product.Products;

import java.util.*;
import java.util.stream.Collectors;

public class CertificatesContent implements Initializable {
    public static final Logger logger = LogManager.getLogger(CertificatesContent.class);
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
        long t0 = System.currentTimeMillis();
        mapContent = new HashMap<>(2000);
        addContent(content);
        logger.trace("хэш мэп контента сертификатов сформирован за {} мсек", System.currentTimeMillis() - t0);
        System.out.print("");
    }

    public void addContent(Collection<CertificateContent> contentList) {
        for (CertificateContent cont : contentList) {
            String[] names = cont.getEquipmentName().split("[,;]");
            for (String name : names) {
                name = name.replaceAll("\\s*(x+.*$)*", "");
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

    public List<CertificateContent> getContentByCertID(int certID) {
        return content.stream()
                .filter(cc -> cc.getCertId() == certID)
                .collect(Collectors.toList());
    }

    public CertificateContent addItem(CertificateContent newContent) {
        final CertificateContent fNewContent = newContent;
        Optional<CertificateContent> optionalContent = content.stream()
                .filter(cc -> cc.equals(fNewContent))
                .findFirst();

        if (optionalContent.isPresent()) {
            newContent = optionalContent.get();
        } else {
            content.add(newContent);
        }

        return newContent;
    }

    public boolean isProductTypeUsed(String type) {
        for (CertificateContent cc : content) {
            if (cc.getProductType().getType().equals(type)) return true;
        }
        return false;
    }

    public void delete(List<CertificateContent> listToDelete) {
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
