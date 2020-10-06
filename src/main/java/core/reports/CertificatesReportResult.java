package core.reports;

import ui_windows.product.Product;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CertificatesReportResult {
    private Product product;
    private HashMap<String, Set<Path>> certFilesGroupedByNorms;

    public CertificatesReportResult(Product product) {
        this.product = product;
        certFilesGroupedByNorms = new HashMap<>();
    }

    public void addCertificateFile(String norm, String fileName) {
        if (certFilesGroupedByNorms.containsKey(norm)) {
            Set<Path> set = new HashSet<>(certFilesGroupedByNorms.get(norm));
            set.add(Paths.get(fileName));
            certFilesGroupedByNorms.put(norm, set);
        } else {
            certFilesGroupedByNorms.put(norm, Collections.singleton(Paths.get(fileName)));
        }
    }

    public Product getProduct() {
        return product;
    }

    public HashMap<String, Set<Path>> getCertFilesGroupedByNorms() {
        return certFilesGroupedByNorms;
    }
}
