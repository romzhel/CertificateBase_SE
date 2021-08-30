package ui_windows.options_window.certificates_editor;

import database.CertificatesContentDB;
import database.CertificatesDB;
import files.Folders;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import lombok.extern.log4j.Log4j2;
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
import java.util.List;
import java.util.stream.Collectors;

import static ui_windows.Mode.ADD;
import static ui_windows.Mode.EDIT;
import static ui_windows.options_window.profile_editor.SimpleRight.FULL;

@Log4j2
public class CertificateEditorWindowActions {
    public static final String DELETED_MARK = "%%%_";
    private List<CertificateContent> backupOfContent;
    private Certificate editedCertificate;

    public CertificateEditorWindowActions() {
        log.trace("constructor without params");
    }

    public CertificateEditorWindowActions(Certificate editedCertificate) {
        this.editedCertificate = editedCertificate;
        backupOfContent = editedCertificate.getContent().stream()
                .map(cc -> new CertificateContent(cc.getId(), cc.getCertId(), cc.getProductType(), cc.getEquipmentName()))
                .collect(Collectors.toList());
        log.trace("constructor with params: cert {}", editedCertificate);
        log.trace("constructor with params: calced backup content {}", backupOfContent);
    }

    public void apply() {
        AnchorPane root = CertificateEditorWindow.getRootAnchorPane();
        CertificateEditorWindowController controller = CertificateEditorWindow.getLoader().getController();

        if (CertificateEditorWindow.getMode() == ADD) {//adding new record
            log.trace("apply new cert saving");

            if (Utils.hasEmptyControls(root, "cbCountrySelect", "cbNormSelect") || hasEmptyCellInTable()) {//empty fields
                Dialogs.showMessage("Пустые поля", "Не все поля заполнены");
            } else {//save to DB

                editedCertificate = new Certificate(CertificateEditorWindow.getStage());
                editedCertificate.setUserId(Users.getInstance().getCurrentUser().getId());

                treatDeletedMark(controller, editedCertificate);

                log.trace("new cert: {}", editedCertificate);

                if (!Certificates.getInstance().hasDoubles(editedCertificate)) {//check duplicates
                    if (new CertificatesDB().putData(editedCertificate)) {//write to DB, get cert id

                        Certificates.getInstance().addItem(editedCertificate);//add cert to global list
                        CertificatesTable.getInstance().addItem(editedCertificate);//display record in table

                        CertificateContentActions.saveContent(editedCertificate);

                        closeCertificateEditorWindow();
                    }
                }
            }

        } else if (CertificateEditorWindow.getMode() == EDIT) {//editing existing record
            if (editedCertificate != null && !hasEmptyCellInTable()) {
                log.trace("saving changed cert: {}", editedCertificate);

                editedCertificate.getContent().clear();

                getCertFromEditorWindow(editedCertificate);//get data from form
                if (Users.getInstance().getCurrentUser().getProfile().getCertificates() != FULL) {
                    editedCertificate.setUserId(Users.getInstance().getCurrentUser().getId());
                }

                treatDeletedMark(controller, editedCertificate);

                log.trace("cert from UI: {}", editedCertificate);

                if (!Certificates.getInstance().hasDoubles(editedCertificate)) {//check duplicates
                    if (new CertificatesDB().updateData(editedCertificate)) {//write to DB
                        CertificateContentActions.saveContent(editedCertificate);

                        Platform.runLater(() -> CertificatesTable.getInstance().getTableView().refresh());//refresh table

                        closeCertificateEditorWindow();
                    }

                }
            } else {
                log.trace("empty fields during edited cert saving");
                Dialogs.showMessage("Пустые поля", "Не все поля заполнены");
            }
        }
    }

    public void cancel() {
        log.trace("cancel cert editing");
        editedCertificate.setContent(backupOfContent);
        log.trace("cert with backup content: {}", editedCertificate);
        closeCertificateEditorWindow();
    }

    public void closeCertificateEditorWindow() {
        CertificateEditorWindow.close();
    }

    public void treatDeletedMark(CertificateEditorWindowController controller, Certificate cert) {
        if (controller.cbxNotUsed.isSelected()) {
            if (!cert.getName().startsWith(DELETED_MARK)) {
                cert.setName(DELETED_MARK + cert.getName());
            }
        } else {
            cert.setName(cert.getName().replaceAll("^" + DELETED_MARK, ""));
        }
    }

    public void getCertFromEditorWindow(Certificate cert) {
        log.trace("applying values to edited cert from UI");
        AnchorPane root = (AnchorPane) CertificateEditorWindow.getStage().getScene().getRoot();

        cert.setName(Utils.getControlValue(root, "tfCertName"));
        cert.setExpirationDate(Utils.getControlValue(root, "dpDatePicker"));
        cert.setCountries(Countries.getShortNames(Utils.getALControlValueFromLV(root, "lvCountries")));
        cert.setNorms(RequirementTypes.getInstance().getReqIdsLineFromShortNamesAL(Utils.getALControlValueFromLV(root, "lvNorms")));
        cert.setFileName(Utils.getControlValue(root, "tfFileName"));
        cert.setFullNameMatch(Utils.getControlValue(root, "ckbNameMatch") == "true");
        cert.setMaterialMatch(Utils.getControlValue(root, "ckbMaterialMatch") == "true");
    }

    public void displayData() {
        CertificateEditorWindowController controller = CertificateEditorWindow.getLoader().getController();

        init();
        AnchorPane root = (AnchorPane) CertificateEditorWindow.getStage().getScene().getRoot();

        controller.cbxNotUsed.setSelected(editedCertificate.getName().startsWith(DELETED_MARK));

        Utils.setControlValue(root, "tfCertName", editedCertificate.getName().replaceAll("^" + DELETED_MARK, ""));
        Utils.setControlValue(root, "dpDatePicker", editedCertificate.getExpirationDate());
        Utils.setControlValueLVfromAL(root, "lvCountries", Countries.getCombinedNames(editedCertificate.getCountries()));
        Utils.setControlValueLVfromAL(root, "lvNorms", RequirementTypes.getInstance().getRequirementsList(editedCertificate.getNorms()));
        Utils.setControlValue(root, "tfFileName", editedCertificate.getFileName());

        try {
            Folders.getInstance().getCalcCertFile(editedCertificate.getFileName());
            Utils.setColor(CertificateEditorWindow.getRootAnchorPane(), "tfFileName", Color.GREEN);
        } catch (Exception e) {
            Utils.setColor(CertificateEditorWindow.getRootAnchorPane(), "tfFileName", Color.RED);
        }

        Utils.setControlValue(root, "ckbNameMatch", editedCertificate.isFullNameMatch());
        Utils.setControlValue(root, "ckbMaterialMatch", editedCertificate.isMaterialMatch());

        CertificatesContentTable.getInstance().setContent(editedCertificate.getContent());
    }

    public void delete(Certificate cert) {
        if (new CertificatesDB().deleteData(cert)) {//delete cert from DB
            new CertificatesContentDB().deleteData(cert.getContent());//delete cert content from db
            CertificatesContent.getInstance().delete(cert.getContent());//delete content from class
            Certificates.getInstance().remove(cert);//delete cert from class
        }
    }

    /*public Certificate getItem() {
        int index = CertificatesTable.getInstance().getTableView().getSelectionModel().getSelectedIndex();

        if (index > -1 && index < CertificatesTable.getInstance().getTableView().getItems().size()) {
            return CertificatesTable.getInstance().getTableView().getItems().get(index);
        } else return null;
    }*/

    public void init() {
        AnchorPane root = (AnchorPane) CertificateEditorWindow.getStage().getScene().getRoot();

        Utils.setControlValue(root, "cbCountrySelect", Countries.getItems());//add countries
        Utils.setControlValue(root, "cbNormSelect", RequirementTypes.getInstance().getAllRequirementTypesShortNames());
//        Utils.setControlValue(root, "cbCountrySelect", Countries.getItems().get(0));//display first country
    }

    public void addCountry() {
        AnchorPane root = (AnchorPane) CertificateEditorWindow.getStage().getScene().getRoot();
        String newCountry = Utils.getControlValue(root, "cbCountrySelect");
        if (newCountry.trim().length() > 0) {
            Utils.addControlValueLV(root, "lvCountries", newCountry);
            CertificateEditorWindowController controller = CertificateEditorWindow.getLoader().getController();
            controller.cbCountrySelect.getEditor().clear();
        }
    }

    public void addNorm() {
        AnchorPane root = (AnchorPane) CertificateEditorWindow.getStage().getScene().getRoot();
        String newNorm = Utils.getControlValue(root, "cbNormSelect");
        if (newNorm.trim().length() > 0) {
            Utils.addControlValueLV(root, "lvNorms", newNorm);
            CertificateEditorWindowController controller = CertificateEditorWindow.getLoader().getController();
            controller.cbNormSelect.getEditor().clear();
        }
    }

    public void selectFile() {
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

    public boolean hasEmptyCellInTable() {
        for (CertificateContent cc : CertificatesContentTable.getInstance().getTableView().getItems()) {
            boolean eqTypeEmpty = cc.getProductType().getType() == null || cc.getProductType().getType().trim().isEmpty();
            boolean tnvedEmpty = cc.getProductType().getTen() == null || cc.getProductType().getTen().trim().isEmpty();
            boolean eqNamesEmpty = cc.getEquipmentName() == null || cc.getEquipmentName().trim().isEmpty();
            if (eqTypeEmpty || eqNamesEmpty || tnvedEmpty) return true;
        }
        return false;
    }
}
