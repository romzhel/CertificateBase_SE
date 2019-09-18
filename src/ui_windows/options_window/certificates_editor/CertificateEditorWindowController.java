package ui_windows.options_window.certificates_editor;

import core.CoreModule;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContentActions;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificatesContentTable;
import utils.AutoCompleteComboBoxListener;

import java.net.URL;
import java.util.ResourceBundle;

public class CertificateEditorWindowController implements Initializable {
    private CertificatesContentTable certificatesContentTable;

    @FXML
    TableView<CertificateContent> tvContent;

    @FXML
    ComboBox<String> cbCountrySelect;

    @FXML
    ComboBox<String> cbNormSelect;

    @FXML
    ListView<String> lvNorms;

    @FXML
    ListView<String> lvCountries;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        certificatesContentTable = new CertificatesContentTable(tvContent);
        CoreModule.setCertificatesContentTable(certificatesContentTable);

        lvNorms.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                int index = lvNorms.getSelectionModel().getSelectedIndex();
                if (index >= 0 && index < lvNorms.getItems().size()) {
                    lvNorms.getItems().remove(index);
                }
            }
        });

        lvCountries.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                int index = lvCountries.getSelectionModel().getSelectedIndex();
                if (index >= 0 && index < lvCountries.getItems().size()) {
                    lvCountries.getItems().remove(index);
                }
            }
        });

        new AutoCompleteComboBoxListener<>(cbCountrySelect, true);
        new AutoCompleteComboBoxListener<>(cbNormSelect, false);
    }

    public void selectFolder() {
        CertificateEditorWindowActions.selectFile();
    }

    public void apply() {
        CertificateEditorWindowActions.apply();
    }

    public void cancel() {
        certificatesContentTable.restoreBackupedItems();
        CertificateEditorWindowActions.close();
    }

    public void addCountry() {
        CertificateEditorWindowActions.addCountry();
    }

    public void addItem() {
        CertificateContentActions.addItem(certificatesContentTable);
    }

    public void editItem() {
        certificatesContentTable.setEditModeActive(true);
        certificatesContentTable.setEditMode(tvContent.getSelectionModel().getSelectedIndex());
    }

    public void deleteItem() {
        int selectedContentIndex = tvContent.getSelectionModel().getSelectedIndex();

        if (selectedContentIndex > -1) {
            CertificateContentActions.deleteContent(tvContent.getItems().get(selectedContentIndex));
        }
    }

    public void addNorm() {
        CertificateEditorWindowActions.addNorm();
    }

    public void normsKey() {

    }

    public void countryKey() {

    }

    public CertificatesContentTable getCertificatesContentTable() {
        return certificatesContentTable;
    }

    public void setCertificatesContentTable(CertificatesContentTable certificatesContentTable) {
        this.certificatesContentTable = certificatesContentTable;
    }
}
