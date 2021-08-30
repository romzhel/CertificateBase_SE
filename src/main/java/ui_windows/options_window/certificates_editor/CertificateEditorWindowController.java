package ui_windows.options_window.certificates_editor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.Setter;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContentActions;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificatesContentTable;
import utils.AutoCompleteComboBoxListener;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

public class CertificateEditorWindowController implements Initializable {

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

    @FXML
    CheckBox cbxNotUsed;

    @Setter
    private CertificateEditorWindowActions editorWindowActions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CertificatesContentTable.getInstance().init(tvContent);

        BiConsumer<KeyEvent, ListView<String>> eventHandler = (event, stringListView) -> {
            if (event.getCode() == KeyCode.DELETE) {
                int index = stringListView.getSelectionModel().getSelectedIndex();
                if (index >= 0 && index < stringListView.getItems().size()) {
                    stringListView.getItems().remove(index);
                }
            }
        };

        lvNorms.setOnKeyReleased(event -> eventHandler.accept(event, lvNorms));
        lvCountries.setOnKeyReleased(event -> eventHandler.accept(event, lvCountries));

        new AutoCompleteComboBoxListener<>(cbCountrySelect, true);
        new AutoCompleteComboBoxListener<>(cbNormSelect, false);
    }

    public void selectFolder() {
        editorWindowActions.selectFile();
    }

    public void apply() {
        editorWindowActions.apply();
    }

    public void cancel() {
        editorWindowActions.cancel();
    }

    public void addCountry() {
        lvCountries.getItems().add(cbCountrySelect.getValue());
        cbCountrySelect.getEditor().clear();

//        editorWindowActions.addCountry();
    }

    public void addItem() {
        CertificateContent cc = CertificateContentActions.addItem(CertificatesContentTable.getInstance());
        cc.setWasChanged(true);
    }

    public void editItem() {
        CertificatesContentTable.getInstance().setEditModeActive(true);
        CertificatesContentTable.getInstance().setEditMode(tvContent.getSelectionModel().getSelectedIndex());
    }

    public void deleteItem() {
        int selectedContentIndex = tvContent.getSelectionModel().getSelectedIndex();

        if (selectedContentIndex > -1) {
            CertificateContentActions.deleteContent(tvContent.getItems().get(selectedContentIndex));
        }
    }

    public void addNorm() {
        lvNorms.getItems().add(cbNormSelect.getValue());
        cbNormSelect.getEditor().clear();

//        editorWindowActions.addNorm();
    }

    public void normsKey() {

    }

    public void countryKey() {

    }
}
