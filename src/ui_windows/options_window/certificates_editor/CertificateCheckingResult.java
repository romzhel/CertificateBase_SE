package ui_windows.options_window.certificates_editor;

import ui_windows.product.Product;
import ui_windows.product.certificatesChecker.CertificateVerificationItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class CertificateCheckingResult {
    private ArrayList<CertificateVerificationItem> problemCv;
    private HashSet<Certificate> problemCertificates;
    private HashSet<Product> problemProducts;
    private HashSet<File> problemCertsFiles;

    public CertificateCheckingResult(ArrayList<CertificateVerificationItem> problemCv,
                                     HashSet<Certificate> problemCertificates, HashSet<Product> problemProducts,
                                     HashSet<File> problemCertsFiles) {
        this.problemCv = problemCv;
        this.problemCertificates = problemCertificates;
        this.problemProducts = problemProducts;
        this.problemCertsFiles = problemCertsFiles;
    }

    public ArrayList<CertificateVerificationItem> getProblemCv() {
        return problemCv;
    }

    public HashSet<Certificate> getProblemCertificates() {
        return problemCertificates;
    }

    public HashSet<Product> getProblemProducts() {
        return problemProducts;
    }

    public HashSet<File> getProblemCertsFiles() {
        return problemCertsFiles;
    }
}
