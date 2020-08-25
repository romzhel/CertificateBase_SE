package core.reports;

import ui_windows.product.Product;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CertificatesReportResult {
    private Product product;
    private HashMap<String, List<Path>> certFilesGroupedByNorms;

    public CertificatesReportResult(Product product) {
        this.product = product;
        certFilesGroupedByNorms = new HashMap<>();
    }

    public void addCertificateFile(String norm, String fileName) {
        if (certFilesGroupedByNorms.containsKey(norm)) {
            List<Path> list = new ArrayList<>(certFilesGroupedByNorms.get(norm));
            list.add(Paths.get(fileName));
            certFilesGroupedByNorms.put(norm, list);
        } else {
            certFilesGroupedByNorms.put(norm, Collections.singletonList(Paths.get(fileName)));
        }
    }

    public Product getProduct() {
        return product;
    }

    public HashMap<String, List<Path>> getCertFilesGroupedByNorms() {
        return certFilesGroupedByNorms;
    }
}
