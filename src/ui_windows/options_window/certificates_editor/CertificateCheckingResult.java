package ui_windows.options_window.certificates_editor;

import ui_windows.main_window.Product;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class CertificateCheckingResult {
    private ArrayList<CertificateVerification> problemCv;
    private HashSet<Certificate> problemCertificates;
    private HashSet<Product> problemProducts;
    private HashSet<File> problemCertsFiles;

    public CertificateCheckingResult(ArrayList<CertificateVerification> problemCv,
                                     HashSet<Certificate> problemCertificates, HashSet<Product> problemProducts,
                                     HashSet<File> problemCertsFiles) {
        this.problemCv = problemCv;
        this.problemCertificates = problemCertificates;
        this.problemProducts = problemProducts;
        this.problemCertsFiles = problemCertsFiles;
    }

    public ArrayList<CertificateVerification> getProblemCv() {
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
