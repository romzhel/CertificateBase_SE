package ui_windows.options_window.certificates_editor;

import core.CoreModule;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContentActions;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificatesContentTable;
import utils.AutoCompleteComboBoxListener;
import utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;

import static ui_windows.options_window.profile_editor.SimpleRight.FULL;

public class CertificateEditorWindowController implements Initializable {

    @FXML
    TableView<CertificateContent> tvContent;

    @FXML
    TableColumn<CertificateContent, String> tcType;

    @FXML
    TableColumn<CertificateContent, String> tcNames;

    @FXML
    TableColumn<CertificateContent, String> tсTnVed;

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
        CoreModule.setCertificatesContentTable(new CertificatesContentTable(tvContent));
        tvContent.setPlaceholder(new Label("Нет данных для отображения"));

        tcType.setCellValueFactory(new PropertyValueFactory<>("equipmentType"));
        tcType.setCellFactory(TextFieldTableCell.forTableColumn());
        tcType.setEditable(true);

        tсTnVed.setCellValueFactory(new PropertyValueFactory<>("tnved"));
        tсTnVed.setCellFactory(TextFieldTableCell.forTableColumn());
        tсTnVed.setEditable(true);

        tcNames.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));
        tcNames.setCellFactory(TextFieldTableCell.forTableColumn());

        tcType.setOnEditCommit(event -> {//product type was changed
            CertificateContent cc = event.getRowValue();

            if (!event.getNewValue().matches(event.getOldValue())) {//changes were made
                cc.setEquipmentType(event.getNewValue());
                cc.setWasChanged(true);
            }
        });

        tсTnVed.setOnEditCommit(event -> {
            CertificateContent cc = event.getRowValue();

            if (!event.getNewValue().matches(event.getOldValue())) {//changes were made
                cc.setTnved(event.getNewValue());
                cc.setWasChanged(true);
            }
        });

        tcNames.setOnEditCommit(event -> {
            CertificateContent cc = event.getRowValue();

            if (!event.getNewValue().matches(event.getOldValue())) {//changes were made
                cc.setEquipmentName(event.getNewValue());
                cc.setWasChanged(true);
            }
        });

        lvNorms.setOnKeyReleased(event -> {
            if (event.getCode()== KeyCode.DELETE) {
                int index = lvNorms.getSelectionModel().getSelectedIndex();
                if (index >= 0 && index < lvNorms.getItems().size()){
                    lvNorms.getItems().remove(index);
                }
            }
        });

        lvCountries.setOnKeyReleased(event -> {
            if (event.getCode()== KeyCode.DELETE) {
                int index = lvCountries.getSelectionModel().getSelectedIndex();
                if (index >= 0 && index < lvCountries.getItems().size()){
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
        CertificateEditorWindowActions.close();
    }

    public void addCountry() {
        CertificateEditorWindowActions.addCountry();
    }

    public void addItem() {
        tvContent.setEditable(true);
        CertificateContentActions.addItem();
    }

    public void editItem() {
        CoreModule.getCertificatesContentTable().getTableView().setEditable(true);
    }

    public void deleteItem() {
        int selectedContentIndex = tvContent.getSelectionModel().getSelectedIndex();

        if (selectedContentIndex > -1) {
            CertificateContentActions.deleteContent(tvContent.getItems().get(selectedContentIndex));
        }
    }

    public void addNorm(){
        CertificateEditorWindowActions.addNorm();
    }

    public void normsKey(){

    }

    public void countryKey(){

    }


}
