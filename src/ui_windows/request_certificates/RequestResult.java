package ui_windows.request_certificates;

import ui_windows.main_window.Product;
import ui_windows.options_window.certificates_editor.CertificateVerification;

import java.io.File;
import java.util.ArrayList;

public class RequestResult {
    private Product product;
    private ArrayList<File> files;

    public RequestResult(Product product, ArrayList<File> files) {
        this.product = product;
        this.files = files;
    }

    public Product getProduct() {
        return product;
    }

    public ArrayList<File> getFiles() {
        return files;
    }
}
