package ui_windows.options_window.certificates_editor;

import database.CertificatesContentDB;
import database.CertificatesDB;
import files.Folders;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import ui.Dialogs;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContentActions;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificatesContent;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificatesContentTable;
import ui_windows.options_window.requirements_types_editor.RequirementTypes;
import ui_windows.options_window.user_editor.Users;
import utils.Countries;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static ui_windows.Mode.ADD;
import static ui_windows.Mode.EDIT;
import static ui_windows.options_window.profile_editor.SimpleRight.FULL;

public class CertificateEditorWindowActions {
    public static final String DELETED_MARK = "%%%_";

    public static void apply() {
        AnchorPane root = CertificateEditorWindow.getRootAnchorPane();
        CertificateEditorWindowController controller = CertificateEditorWindow.getLoader().getController();

        if (CertificateEditorWindow.getMode() == ADD) {//adding new record

            if (Utils.hasEmptyControls(root, "cbCountrySelect", "cbNormSelect") || hasEmptyCellInTable()) {//empty fields
                Dialogs.showMessage("Пустые поля", "Не все поля заполнены");
            } else {//save to DB

                Certificate cert = new Certificate(CertificateEditorWindow.getStage());
                cert.setUserId(Users.getInstance().getCurrentUser().getId());

                treatDeletedMark(controller, cert);

                if (!Certificates.getInstance().hasDoubles(cert)) {//check duplicates
                    if (new CertificatesDB().putData(cert)) {//write to DB, get cert id

                        Certificates.getInstance().addItem(cert);//add cert to global list
                        CertificatesTable.getInstance().addItem(cert);//display record in table

                        CertificateContentActions.saveContent(cert);

                        close();
                    }
                }
            }

        } else if (CertificateEditorWindow.getMode() == EDIT) {//editing existing record
            Certificate cert = getItem();
            if (cert != null && !hasEmptyCellInTable()) {

                getCertFromEditorWindow(cert);//get data from form
                if (Users.getInstance().getCurrentUser().getProfile().getCertificates() != FULL)
                    cert.setUserId(Users.getInstance().getCurrentUser().getId());

                treatDeletedMark(controller, cert);

                if (!Certificates.getInstance().hasDoubles(cert)) {//check duplicates
                    if (new CertificatesDB().updateData(cert)) {//write to DB
                        CertificateContentActions.saveContent(cert);

                        Platform.runLater(() -> CertificatesTable.getInstance().getTableView().refresh());//refresh table

                        close();
                    }

                }
            } else {
                Dialogs.showMessage("Пустые поля", "Не все поля заполнены");
            }
        }
    }

    public static void treatDeletedMark(CertificateEditorWindowController controller, Certificate cert) {
        if (controller.cbxNotUsed.isSelected()) {
            cert.setName(cert.getName().replaceAll("^" + DELETED_MARK, ""));
            cert.setName(DELETED_MARK + cert.getName());
        } else {
            cert.setName(cert.getName().replaceAll("^" + DELETED_MARK, ""));
        }
    }

    public static void getCertFromEditorWindow(Certificate cert) {
        AnchorPane root = (AnchorPane) CertificateEditorWindow.getStage().getScene().getRoot();

        cert.setName(Utils.getControlValue(root, "tfCertName"));
        cert.setExpirationDate(Utils.getControlValue(root, "dpDatePicker"));
        cert.setCountries(Countries.getShortNames(Utils.getALControlValueFromLV(root, "lvCountries")));
        cert.setNorms(RequirementTypes.getInstance().getReqIdsLineFromShortNamesAL(Utils.getALControlValueFromLV(root, "lvNorms")));
        cert.setFileName(Utils.getControlValue(root, "tfFileName"));
        cert.setFullNameMatch(Utils.getControlValue(root, "ckbNameMatch") == "true");
        cert.setMaterialMatch(Utils.getControlValue(root, "ckbMaterialMatch") == "true");
    }

    public static void close() {
        CertificateEditorWindow.close();
    }

    public static void displayData() {
        CertificateEditorWindowController controller = CertificateEditorWindow.getLoader().getController();
        Certificate cert = getItem();
        if (cert != null) {
            init();
            AnchorPane root = (AnchorPane) CertificateEditorWindow.getStage().getScene().getRoot();

            controller.cbxNotUsed.setSelected(cert.getName().startsWith(DELETED_MARK));

            Utils.setControlValue(root, "tfCertName", cert.getName().replaceAll("^" + DELETED_MARK, ""));
            Utils.setControlValue(root, "dpDatePicker", cert.getExpirationDate());
            Utils.setControlValueLVfromAL(root, "lvCountries", Countries.getCombinedNames(cert.getCountries()));
            Utils.setControlValueLVfromAL(root, "lvNorms", RequirementTypes.getInstance().getRequirementsList(cert.getNorms()));
            Utils.setControlValue(root, "tfFileName", cert.getFileName());

            try {
                Folders.getInstance().getCalcCertFile(cert.getFileName());
                Utils.setColor(CertificateEditorWindow.getRootAnchorPane(), "tfFileName", Color.GREEN);
            } catch (Exception e) {
                Utils.setColor(CertificateEditorWindow.getRootAnchorPane(), "tfFileName", Color.RED);
            }

            Utils.setControlValue(root, "ckbNameMatch", cert.isFullNameMatch());
            Utils.setControlValue(root, "ckbMaterialMatch", cert.isMaterialMatch());

            CertificatesContentTable.getInstance().setContent(cert.getContent());
        }
    }

    public static void deleteData() {
        Certificate cert = getItem();
        if (cert != null)
            if (Dialogs.confirm("Удаление записи", "Действительно желаете удалить запись без возможности восстановления?")) {
                if (new CertificatesDB().deleteData(cert)) {//delete cert from DB
                    new CertificatesContentDB().deleteData(cert.getContent());//delete cert content from db
                    CertificatesContent.getInstance().delete(cert.getContent());//delete content from class
                    Certificates.getInstance().remove(cert);//delete cert from class
                }
            }
    }

    public static Certificate getItem() {
        int index = CertificatesTable.getInstance().getTableView().getSelectionModel().getSelectedIndex();

        if (index > -1 && index < CertificatesTable.getInstance().getTableView().getItems().size()) {
            return CertificatesTable.getInstance().getTableView().getItems().get(index);
        } else return null;
    }

    public static void init() {
        AnchorPane root = (AnchorPane) CertificateEditorWindow.getStage().getScene().getRoot();

        Utils.setControlValue(root, "cbCountrySelect", Countries.getItems());//add countries
        Utils.setControlValue(root, "cbNormSelect", RequirementTypes.getInstance().getAllRequirementTypesShortNames());
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
            if (Files.exists(Folders.getInstance().getCertFolder().resolve(certFile.getName()))) {
                Dialogs.showMessage("Добавление сертификата", "Файл сертификата с таким именем уже существует.\n" +
                        "Проверьте, возможно, сертификат уже есть в базе");
            } else {
                Path destination = Folders.getInstance().getCertFolder().resolve(certFile.getName());

                try {
                    Files.copy(certFile.toPath(), destination);
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
        for (CertificateContent cc : CertificatesContentTable.getInstance().getTableView().getItems()) {
            boolean eqTypeEmpty = cc.getProductType().getType() == null || cc.getProductType().getType().trim().isEmpty();
            boolean tnvedEmpty = cc.getProductType().getTen() == null || cc.getProductType().getTen().trim().isEmpty();
            boolean eqNamesEmpty = cc.getEquipmentName() == null || cc.getEquipmentName().trim().isEmpty();
            if (eqTypeEmpty || eqNamesEmpty || tnvedEmpty) return true;
        }
        return false;
    }


}
