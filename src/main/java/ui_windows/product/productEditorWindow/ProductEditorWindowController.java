package ui_windows.product.productEditorWindow;

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
import ui.Dialogs;
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
import utils.PriceBox;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static core.SharedData.SHD_SELECTED_PRODUCTS;

public class ProductEditorWindowController implements Initializable {
    private static final Logger logger = LogManager.getLogger(ProductEditorWindowController.class);
    @FXML
    public TextField tfMaterial;
    @FXML
    public TextField tfMaterialPrint;
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
    public CheckBox cbxBlocked;
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
    public TextArea taCommentsPrice;
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
    public TextField tfPriceListIncl;
    @FXML
    public TextField tfPriceListInclCost;
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
    @FXML
    public TextField tfWarranty;
    private MultiEditor multiEditor;
    private CertificateVerificationTable certificateVerificationTable;
    private ComboBoxEqTypeSelector comboBoxEqTypeSelector;
    private PriceBox priceBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Product> editedItems = null;
        if (SHD_SELECTED_PRODUCTS.getData() instanceof ObservableList) {
            editedItems = SHD_SELECTED_PRODUCTS.getData();
        } else {
            Dialogs.showMessage("Подробные сведения", "Ошибка определения открываемых элементов");
        }

        certificateVerificationTable = new CertificateVerificationTable(this);
        certificateVerificationTable.display(editedItems, CheckParameters.getDefault());
        comboBoxEqTypeSelector = new ComboBoxEqTypeSelector(cbType, certificateVerificationTable);
        priceBox = new PriceBox(tfPriceListInclCost);

        multiEditor = new MultiEditor(editedItems, this);

        cmCertActions.getItems().get(3).setDisable(Users.getInstance().getCurrentUser().getProfile().getName().equals(Profile.COMMON_ACCESS));

        rmiTypeFilter.selectedProperty().addListener((observable, oldValue, newValue) -> {
            String prevType = cbType.getValue();
            certificateVerificationTable.display(certificateVerificationTable.getCheckParameters().setEqTypeFiltered(newValue));
            comboBoxEqTypeSelector.refresh(prevType);
        });

        cbxBlocked.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                cbxBlocked.setStyle("-fx-text-fill: red; -fx-border-color: red; -fx-outer-border: red; mark-color: red; -fx-mark-color: red;");
                cbxPrice.setDisable(true);
            } else {
                cbxBlocked.setStyle("");
                cbxPrice.setDisable(false);
            }
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
            if (Files.exists(Folders.getInstance().getManualsFolder().resolve(manualFile.getName()))) {
                Dialogs.showMessage("Добавление описания", "Описание с таким именем уже существует");
            } else {
                Path destination = Folders.getInstance().getManualsFolder().resolve(
                        Utils.getControlValue(ProductEditorWindow.getRootAnchorPane(), "tfFileName"));

                try {
                    Files.copy(manualFile.toPath(), destination);
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
        copyToClipBoardCertificatesFiles(Collections.singletonList(cv));
    }

    public void getAllCertificatesFiles() {
        logger.info("Getting all certificates");
        copyToClipBoardCertificatesFiles(tvCertVerification.getItems());
    }

    private void copyToClipBoardCertificatesFiles(List<CertificateVerificationItem> items) {
        Utils.copyFilesToClipboard(items.stream()
                .map(certificateVerificationItem -> Paths.get(certificateVerificationItem.getFile()))
                .filter(fileName -> fileName.toString().endsWith(".pdf"))
                .distinct()
                .map(fileName -> {
                    try {
                        return Folders.getInstance().getCalcCertFile(fileName).toFile();
                    } catch (Exception e) {
                        Dialogs.showMessageTS("Ошибка копирования файлов сертификатов в буфер обмена", e.getMessage());
                        return null;
                    }
                })
                .collect(Collectors.toList()));
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

    public PriceBox getPriceBox() {
        return priceBox;
    }
}
