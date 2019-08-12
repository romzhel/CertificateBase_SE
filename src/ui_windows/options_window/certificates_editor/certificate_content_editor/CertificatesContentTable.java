package ui_windows.options_window.certificates_editor.certificate_content_editor;

import javafx.scene.control.TableView;

import java.util.ArrayList;

public class CertificatesContentTable {
    private TableView<CertificateContent> tableView;

    public CertificatesContentTable(TableView<CertificateContent> tableView) {
        this.tableView = tableView;
    }

    public TableView<CertificateContent> getTableView() {
        return tableView;
    }

    public ArrayList<CertificateContent> getContent() {
        ArrayList<CertificateContent> certContent = new ArrayList<>();

        for (CertificateContent cc : tableView.getItems()) {
            certContent.add(cc);
        }

        return certContent;
    }

    public void setContent(ArrayList<CertificateContent> content) {
        tableView.getItems().clear();
        if (content != null)tableView.getItems().addAll(content);
    }
}
