package ui_windows.request_certificates;

import core.CoreModule;
import core.Dialogs;
import files.ExportToExcel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import ui_windows.product.Product;
import ui_windows.options_window.certificates_editor.certificatesChecker.CertificateVerificationItem;
import ui_windows.options_window.certificates_editor.certificatesChecker.CertificatesChecker;
import utils.Utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;

public class CertificateRequestWindowController implements Initializable {
    @FXML
    Button btnApply;

    @FXML
    TextArea taData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnApply.requestFocus();
    }

    public void actionRequestCertificatesRun() {
        ArrayList<RequestResult> results = new ArrayList<>();
        HashSet<File> lostFiles = new HashSet<>();
        String linesNotFound = "";

        String[] lines = taData.getText().split("\n");
        String[] words;
        Product product;
        boolean lineWasFound;
        boolean hasCertificates;
        int itemsWithCert = 0;

        for (String line : lines) {
            lineWasFound = false;
            hasCertificates = false;
            words = line.split("\t");

            for (String word : words) {
                if (!word.matches(".*\\s.*")) {
                    product = CoreModule.getProducts().getItemByMaterialOrArticle(word);

                    if (product != null) {
                        lineWasFound = true;

                        CertificatesChecker certificatesChecker = CoreModule.getCertificates().getCertificatesChecker();
                        certificatesChecker.check(product);

                        HashSet<File> files = new HashSet<>();
                        for (CertificateVerificationItem cv : certificatesChecker.getResultTableItems()) {
                            File certFile = new File(CoreModule.getFolders().getCertFolder() + "\\" + cv.getFile());

                            if (certFile.exists() && cv.getStatus().startsWith("OK")) {
                                files.add(certFile);
                                hasCertificates = true;
                            }
                            else if (!certFile.exists()) lostFiles.add(certFile);
                        }

                        results.add(new RequestResult(product, new ArrayList<>(files)));

                        break;
                    }
                }
            }

            if (!lineWasFound) linesNotFound += "- " + line + "\n";
            if (hasCertificates) itemsWithCert++;
        }

        String missedPositions = linesNotFound.length() > 0 ? "\nНе были распознаны следующие позиции:\n" +
                linesNotFound + "\n" : "";

        String lostFilesS = "";
        for (File f : lostFiles) lostFilesS += "- " + f.getName() + "\n";

        String missedFiles = lostFilesS.length() > 0 ? "\nНе удалось скопировать файлы сертификатов:\n" +
                lostFilesS + "\n" : "";

//        if (linesNotFound.length() > 0 || lostFiles.size() > 0 || i) {
        if (!Dialogs.confirm("Запрос сертификатов", "Найдены сертификаты для " + itemsWithCert +
                " позиций из " + lines.length + ".\n" + missedPositions + missedFiles +
                "Желаете продолжить?")) return;
//        }

        CertificateRequestWindow.close();

        HashSet<File> allFiles = new HashSet<>();
        for (RequestResult rr : results) {
            for (File cf : rr.getFiles()) {
                allFiles.add(cf);
                File target = new File(CoreModule.getFolders().getTempFolder() + "\\" + cf.getName());

                try {
                    if (!target.exists()) Files.copy(cf.toPath(), target.toPath());
                } catch (IOException ee) {
                    System.out.println("copying error " + ee.getMessage());
                    Dialogs.showMessage("Ошибка копирования файла", "Произошла ошибка копирования файла\n" +
                            cf.getPath() + "\n\nв папку " + target.getPath());
                }
            }
        }

//        File excelFile = ExcelFile.exportToFile(results);
        File excelFile = new ExportToExcel(results).getFile();

        if (excelFile == null) {
            Dialogs.showMessage("Ошибка создания файла Excel", "Не удалось создать файл Excel, тем не " +
                    "менее файлы сертификатов помещены в буфер обмена");
            Utils.copyFilesToClipboard(new ArrayList<>(allFiles));
            return;
        }

        allFiles.add(excelFile);
        Utils.copyFilesToClipboard(new ArrayList<>(allFiles));

        try {
            Desktop.getDesktop().open(excelFile);
        } catch (Exception e) {
            System.out.println("Can't open created file");
        }

    }

    public void actionRequestCertificatesCancel() {
        CertificateRequestWindow.close();
    }


}
