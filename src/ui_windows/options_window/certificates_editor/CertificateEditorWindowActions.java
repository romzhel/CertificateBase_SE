package ui_windows.options_window.certificates_editor;

import core.CoreModule;
import core.Dialogs;
import database.CertificatesContentDB;
import database.CertificatesDB;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContentActions;
import utils.Countries;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static ui_windows.Mode.ADD;
import static ui_windows.Mode.EDIT;
import static ui_windows.options_window.profile_editor.SimpleRight.FULL;

public class CertificateEditorWindowActions {

    public static void apply() {
        AnchorPane root = CertificateEditorWindow.getRootAnchorPane();

        if (CertificateEditorWindow.getMode() == ADD) {//adding new record

            if (Utils.hasEmptyControls(root, "cbCountrySelect", "cbNormSelect") || hasEmptyCellInTable()) {//empty fields
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
            if (cert != null && !hasEmptyCellInTable()) {

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
            } else {
                Dialogs.showMessage("Пустые поля", "Не все поля заполнены");
            }
        }
    }

    public static void getCertFromEditorWindow(Certificate cert) {
        AnchorPane root = (AnchorPane) CertificateEditorWindow.getStage().getScene().getRoot();

        cert.setName(Utils.getControlValue(root, "tfCertName"));
        cert.setExpirationDate(Utils.getControlValue(root, "dpDatePicker"));
        cert.setCountries(Countries.getShortNames(Utils.getALControlValueFromLV(root, "lvCountries")));
        cert.setNorms(CoreModule.getRequirementTypes().getReqIdsLineFromShortNamesAL(Utils.getALControlValueFromLV(root, "lvNorms")));
        cert.setFileName(Utils.getControlValue(root, "tfFileName"));
        cert.setFullNameMatch(Utils.getControlValue(root, "ckbNameMatch") == "true");
        cert.setMaterialMatch(Utils.getControlValue(root, "ckbMaterialMatch") == "true");
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
            Utils.setControlValueLVfromAL(root, "lvCountries", Countries.getCombinedNames(cert.getCountries()));
            Utils.setControlValueLVfromAL(root, "lvNorms", CoreModule.getRequirementTypes().getRequirementsList(cert.getNorms()));
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
            if (Dialogs.confirm("Удаление записи", "Действительно желаете удалить запись без возможности восстановления?")) {
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
        Utils.setControlValue(root, "cbNormSelect", CoreModule.getRequirementTypes().getAllRequirementTypesShortNames());
//        Utils.setControlValue(root, "cbCountrySelect", Countries.getItems().get(0));//display first country
    }

    public static void addCountry() {
        AnchorPane root = (AnchorPane) CertificateEditorWindow.getStage().getScene().getRoot();
        String newCountry = Utils.getControlValue(root, "cbCountrySelect");
        if (newCountry.trim().length() > 0) {
            Utils.addControlValueLV(root, "lvCountries", newCountry);
        }
    }

    public static void addNorm() {
        AnchorPane root = (AnchorPane) CertificateEditorWindow.getStage().getScene().getRoot();
        String newNorm = Utils.getControlValue(root, "cbNormSelect");
        if (newNorm.trim().length() > 0) {
            Utils.addControlValueLV(root, "lvNorms", newNorm);
        }
    }

    public static void selectFile() {
        File certFile = Dialogs.selectFile(CertificateEditorWindow.getStage());
        if (certFile != null) {
            if (new File(CoreModule.getFolders().getCertFolder().getPath() + "\\" + certFile.getName()).exists()) {
                Dialogs.showMessage("Добавление сертификата", "Файл сертификата с таким именем уже существует.\n" +
                        "Проверьте, возможно, сертификат уже есть в базе");
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

    public static boolean hasEmptyCellInTable() {
        for (CertificateContent cc : CoreModule.getCertificatesContentTable().getTableView().getItems()) {
            boolean eqTypeEmpty = cc.getProductType().getType() == null || cc.getProductType().getType().trim().isEmpty();
            boolean tnvedEmpty = cc.getProductType().getTen() == null || cc.getProductType().getTen().trim().isEmpty();
            boolean eqNamesEmpty = cc.getEquipmentName() == null || cc.getEquipmentName().trim().isEmpty();
            if (eqTypeEmpty || eqNamesEmpty || tnvedEmpty) return true;
        }
        return false;
    }


}
