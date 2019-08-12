package ui_windows.options_window.certificates_editor;

import core.CoreModule;
import core.Dialogs;
import database.CertificatesContentDB;
import database.CertificatesDB;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import ui_windows.options_window.OptionsWindow;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContentActions;
import ui_windows.options_window.profile_editor.Profile;
import utils.Countries;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static ui_windows.Mode.ADD;
import static ui_windows.Mode.EDIT;
import static ui_windows.options_window.profile_editor.SimpleRight.*;

public class CertificateEditorWindowActions {

    public static void apply() {
        AnchorPane root = CertificateEditorWindow.getRootAnchorPane();

        if (CertificateEditorWindow.getMode() == ADD) {//adding new record

            if (Utils.hasEmptyControls(root, "cbCountrySelect", "cbNormSelect")) {//empty fields
                Dialogs.showMessage("Пустые поля", "Не все поля заполнены");
            } else {//save to DB

                Certificate cert = new Certificate(CertificateEditorWindow.getStage());
                cert.setUserId(CoreModule.getUsers().getCurrentUser().getId());

                if (!CoreModule.getCertificates().hasDoubles(cert)) {//check duplicates
                    if (new CertificatesDB().putData(cert)) {//write to DB, get cert id

                        CoreModule.getCertificates().addItem(cert);//add cert to global list
                        CoreModule.getCertificatesTable().addItem(cert);//display record in table

                        CertificateContentActions.saveContent(cert);

                        close();
                    }
                }
            }

        } else if (CertificateEditorWindow.getMode() == EDIT) {//editing existing record
            Certificate cert = getItem();
            if (cert != null) {

                getCertFromEditorWindow(cert);//get data from form
                if (CoreModule.getUsers().getCurrentUser().getProfile().getCertificates() != FULL)
                    cert.setUserId(CoreModule.getUsers().getCurrentUser().getId());

                if (!CoreModule.getCertificates().hasDoubles(cert)) {//check duplicates
                    if (new CertificatesDB().updateData(cert)) {//write to DB
                        CertificateContentActions.saveContent(cert);

                        Platform.runLater(() -> CoreModule.getCertificatesTable().getTableView().refresh());//refresh table

                        System.out.println(cert.toString());

                        close();
                    }

                }
            }
        }
    }

    public static void getCertFromEditorWindow(Certificate cert) {
        AnchorPane root = (AnchorPane) CertificateEditorWindow.getStage().getScene().getRoot();

        cert.setName(Utils.getControlValue(root, "tfCertName"));
        cert.setExpirationDate(Utils.getControlValue(root, "dpDatePicker"));
        cert.setCountries(Countries.getShortNames(Utils.getControlValueFromLV(root, "lvCountries")));
        cert.setNorms(CoreModule.getRequirementTypes().getRequirementIdsLine(Utils.getControlValueFromLV(root, "lvNorms")));
        cert.setFileName(Utils.getControlValue(root, "tfFileName"));
        cert.setFullNameMatch(Utils.getControlValue(root, "ckbNameMatch") == "true" ? true : false);
        cert.setMaterialMatch(Utils.getControlValue(root, "ckbMaterialMatch") == "true" ? true : false);
    }

    public static void close() {
        CertificateEditorWindow.close();
    }

    public static void displayData() {
        Certificate cert = getItem();
        if (cert != null) {
            init();
            AnchorPane root = (AnchorPane) CertificateEditorWindow.getStage().getScene().getRoot();

            Utils.setControlValue(root, "tfCertName", cert.getName());
            Utils.setControlValue(root, "dpDatePicker", cert.getExpirationDate());
            Utils.setControlValueLV(root, "lvCountries", Countries.getCombinedNames(cert.getCountries()));
            Utils.setControlValueLV(root, "lvNorms", CoreModule.getRequirementTypes().getRequirementsList(cert.getNorms()));
            Utils.setControlValue(root, "tfFileName", cert.getFileName());

            File certFile = new File(CoreModule.getFolders().getCertFolder() + "\\" + cert.getFileName());
            if (certFile.exists()) {
                Utils.setColor(CertificateEditorWindow.getRootAnchorPane(), "tfFileName", Color.GREEN);
            } else {
                Utils.setColor(CertificateEditorWindow.getRootAnchorPane(), "tfFileName", Color.RED);
            }

            Utils.setControlValue(root, "ckbNameMatch", cert.isFullNameMatch());
            Utils.setControlValue(root, "ckbMaterialMatch", cert.isMaterialMatch());

            CoreModule.getCertificatesContentTable().setContent(cert.getContent());
        }
    }

    public static void deleteData() {
        Certificate cert = getItem();
        if (cert != null)
            if (Dialogs.confirm("Удаление записи", "Действительно желаете удалить запись?")) {
                if (new CertificatesDB().deleteData(cert)) {//delete cert from DB
                    new CertificatesContentDB().deleteData(cert.getContent());//delete cert content from db
                    CoreModule.getCertificatesContent().delete(cert.getContent());//delete content from class
                    CoreModule.getCertificates().remove(cert);//delete cert from class
                }
            }
    }

    public static Certificate getItem() {
        int index = CoreModule.getCertificatesTable().getTableView().getSelectionModel().getSelectedIndex();

        if (index > -1 && index < CoreModule.getCertificatesTable().getTableView().getItems().size()) {
            return CoreModule.getCertificatesTable().getTableView().getItems().get(index);
        } else return null;
    }

    public static void init() {
        AnchorPane root = (AnchorPane) CertificateEditorWindow.getStage().getScene().getRoot();

        Utils.setControlValue(root, "cbCountrySelect", Countries.getItems());//add countries
        Utils.setControlValue(root, "cbNormSelect", CoreModule.getRequirementTypes().getAllRequirementTypes());
//        Utils.setControlValue(root, "cbCountrySelect", Countries.getItems().get(0));//display first country
    }

    public static void addCountry() {
        AnchorPane root = (AnchorPane) CertificateEditorWindow.getStage().getScene().getRoot();
        String newCountry = Utils.getControlValue(root, "cbCountrySelect");
        if (newCountry.trim().length() > 0) Utils.addControlValueLV(root, "lvCountries", newCountry);
    }

    public static void addNorm() {
        AnchorPane root = (AnchorPane) CertificateEditorWindow.getStage().getScene().getRoot();
        String newNorm = Utils.getControlValue(root, "cbNormSelect");
        if (newNorm.trim().length() > 0) Utils.addControlValueLV(root, "lvNorms", newNorm);
    }

    public static void selectFile() {
        File certFile = Dialogs.selectFile(CertificateEditorWindow.getStage());
        if (certFile != null) {
            if (new File(CoreModule.getFolders().getCertFolder().getPath() + "\\" + certFile.getName()).exists()) {
                Dialogs.showMessage("Добавление сертификата", "Сертификат с таким именем уже существует");
            } else {
                File destination = new File(CoreModule.getFolders().getCertFolder().getPath() + "\\" + certFile.getName());

                try {
                    Files.copy(certFile.toPath(), destination.toPath());
                    Utils.setControlValue((AnchorPane) CertificateEditorWindow.getStage().getScene().getRoot(),
                            "tfFileName", certFile.getName());
                    Utils.setColor(CertificateEditorWindow.getRootAnchorPane(), "tfFileName", Color.GREEN);
                } catch (IOException e) {
                    Utils.setColor(CertificateEditorWindow.getRootAnchorPane(), "tfFileName", Color.RED);
                    System.out.println("file copying error " + e.getMessage());
                }
            }
        }
    }


}
