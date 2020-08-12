package ui_windows.product.productEditorWindow;

import core.Dialogs;
import database.ProductsDB;
import files.Folders;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.main_window.filter_window_se.Filter_SE;
import ui_windows.options_window.certificates_editor.CertificateEditorWindow;
import ui_windows.options_window.families_editor.ProductFamilies;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.profile_editor.Profile;
import ui_windows.options_window.user_editor.Users;
import ui_windows.product.MultiEditor;
import ui_windows.product.Product;
import ui_windows.product.certificatesChecker.CertificateVerificationItem;
import ui_windows.product.certificatesChecker.CheckParameters;
import ui_windows.product.productEditorWindow.configNormsWindow.ConfigNormsWindow;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static core.SharedData.SHD_SELECTED_PRODUCTS;

public class ProductEditorWindowController implements Initializable {
    private static final Logger logger = LogManager.getLogger(ProductEditorWindowController.class);
    @FXML
    public TextField tfMaterial;
    @FXML
    public TextField tfArticle;
    @FXML
    public TextField tfDangerous;
    @FXML
    public ComboBox<String> cbType;
    @FXML
    public TextField tfAccessibility;
    @FXML
    public CheckBox cbxNotUsed;
    @FXML
    public CheckBox cbxPrice;
    @FXML
    public CheckBox cbxArchive;
    @FXML
    public TextField tfLgbk;
    @FXML
    public TextField tfHierarchy;
    @FXML
    public TextField tfEndOfService;
    @FXML
    public TextField tfCountry;
    @FXML
    public TextArea taComments;
    @FXML
    public AnchorPane apRoot;
    @FXML
    public TextArea taDescription;
    @FXML
    public TextArea taDescriptionEn;
    @FXML
    public RadioMenuItem rmiTypeFilter;
    @FXML
    public ComboBox<String> cbFamily;
    @FXML
    public TextField tfPm;
    @FXML
    public ListView<String> lHistory;
    @FXML
    public TextField tfManHier;
    @FXML
    public TextField tfFileName;
    @FXML
    public TextField tfReplacement;
    @FXML
    public TextField tfMinOrder;
    @FXML
    public TextField tfPacketSize;
    @FXML
    public TextField tfLeadTime;
    @FXML
    public TextField tfWeight;
    @FXML
    public TextField tfLocalPrice;
    @FXML
    TableView<CertificateVerificationItem> tvCertVerification;
    @FXML
    Button btnApply;
    @FXML
    CheckBox cbxOrderable;
    @FXML
    ContextMenu cmCertActions;
    private MultiEditor multiEditor;
    private CertificateVerificationTable certificateVerificationTable;
    private ComboBoxEqTypeSelector comboBoxEqTypeSelector;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Product> editedItems = null;
        if (SHD_SELECTED_PRODUCTS.getData() instanceof ObservableList) {
            editedItems = SHD_SELECTED_PRODUCTS.getData();
        } else {
            Dialogs.showMessage("Подробные сведения", "Ошибка определения открываемых элементов");
        }

        certificateVerificationTable = new CertificateVerificationTable(this);
        certificateVerificationTable.display(editedItems, new CheckParameters());
        comboBoxEqTypeSelector = new ComboBoxEqTypeSelector(cbType, certificateVerificationTable);

        multiEditor = new MultiEditor(editedItems, this);

        cmCertActions.getItems().get(3).setDisable(Users.getInstance().getCurrentUser().getProfile().getName().equals(Profile.COMMON_ACCESS));

        rmiTypeFilter.selectedProperty().addListener((observable, oldValue, newValue) -> {
            String prevType = cbType.getValue();
            certificateVerificationTable.display(certificateVerificationTable.getCheckParameters().setEqTypeFiltered(newValue));
            comboBoxEqTypeSelector.refresh(prevType);
        });

        initFamilySelector();
    }

    private void initFamilySelector() {
        cbFamily.setOnAction(event -> {
            ProductFamily pf = ProductFamilies.getInstance().getFamilyByName(cbFamily.getValue());

            if (pf != null) tfPm.setText(pf.getResponsible());
            else tfPm.setText("");
        });
    }

    public void apply() {
        if (multiEditor.checkAndSaveChanges()) {
            if (new ProductsDB().updateData(multiEditor.getEditedItems())) {
                Filter_SE.getInstance().apply();
                ((Stage) tvCertVerification.getScene().getWindow()).close();
            }
        } else {
            ((Stage) tvCertVerification.getScene().getWindow()).close();
        }
    }

    public void cancel() {
        ((Stage) tvCertVerification.getScene().getWindow()).close();
    }

    public void actionSelectManualFile() {
        File manualFile = Dialogs.selectFile(CertificateEditorWindow.getStage());
        if (manualFile != null) {
            if (new File(Folders.getInstance().getManualsFolder().getPath() + "\\" + manualFile.getName()).exists()) {
                Dialogs.showMessage("Добавление описания", "Описание с таким именем уже существует");
            } else {
                File destination = new File(Folders.getInstance().getManualsFolder().getPath() + "\\" +
                        Utils.getControlValue(ProductEditorWindow.getRootAnchorPane(), "tfFileName"));

                try {
                    Files.copy(manualFile.toPath(), destination.toPath());
                    Utils.setColor(ProductEditorWindow.getRootAnchorPane(), "tfFileName", Color.GREEN);
                } catch (IOException e) {
                    Utils.setColor(ProductEditorWindow.getRootAnchorPane(), "tfFileName", Color.RED);
                    System.out.println("file copying error " + e.getMessage());
                }
            }
        }
    }

    public void getCertificateFile() {
        logger.info("Getting selected certificates");
        CertificateVerificationItem cv = tvCertVerification.getSelectionModel().getSelectedItem();
        if (cv == null) return;

        File file = new File(Folders.getInstance().getCertFolder().getPath() + "\\" + cv.getFile());
        ArrayList<File> files = new ArrayList<>();
        if (file.exists() && file.getPath().endsWith(".pdf")) files.add(file);
        Utils.copyFilesToClipboard(files);
    }

    public void getAllCertificatesFiles() {
        logger.info("Getting all certificates");
        ArrayList<File> files = new ArrayList<>();
        for (CertificateVerificationItem cv : tvCertVerification.getItems()) {
            if (cv.getFile() != null && !cv.getFile().isEmpty()) {
                File file = new File(Folders.getInstance().getCertFolder().getPath() + "\\" + cv.getFile());
                if (file.exists() && file.getPath().endsWith(".pdf")) files.add(file);
            }
        }
        Utils.copyFilesToClipboard(files);
    }

    private void getCertificateFiles(List<CertificateVerificationItem> selectedItems) {
        ArrayList<File> files = new ArrayList<>();
        for (CertificateVerificationItem cv : selectedItems) {
            if (cv.getFile() != null && !cv.getFile().isEmpty()) {
                File file = null;
                try {
                    file = Utils.getFileFromMultiLocation(cv.getFile(),
                            Folders.getInstance().getCashedCertFolder(),
                            Folders.getInstance().getCertFolder().toPath())
                            .toFile();
                } catch (Exception e) {
                    logger.error("Ошибка {} получения файла {}", e.getMessage(), cv.getFile(), e);
                }
                if (file.exists() && file.getPath().endsWith(".pdf")) {
                    files.add(file);
                }
            }
        }
        Utils.copyFilesToClipboard(files);
    }

    public void configNorms() {
        logger.info("Opening norms window editor");
        ConfigNormsWindow cnw = new ConfigNormsWindow(ProductEditorWindow.getStage(), multiEditor, certificateVerificationTable);
    }

    public MultiEditor getMultiEditor() {
        return multiEditor;
    }

    public void setMultiEditor(MultiEditor multiEditor) {
        this.multiEditor = multiEditor;
    }

    public CertificateVerificationTable getCertificateVerificationTable() {
        return certificateVerificationTable;
    }
}
