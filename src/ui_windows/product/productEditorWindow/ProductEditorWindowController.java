package ui_windows.product.productEditorWindow;

import core.CoreModule;
import core.Dialogs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ui_windows.options_window.certificates_editor.CertificateEditorWindow;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;
import ui_windows.options_window.product_lgbk.LgbkAndParent;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.profile_editor.Profile;
import ui_windows.product.MultiEditor;
import ui_windows.product.Product;
import ui_windows.product.certificatesChecker.CertificateVerificationItem;
import ui_windows.product.certificatesChecker.CheckParameters;
import ui_windows.product.productEditorWindow.configNormsWindow.ConfigNormsWindow;
import utils.Utils;

import javax.xml.bind.annotation.XmlAnyAttribute;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ProductEditorWindowController implements Initializable {
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
    TableView<CertificateVerificationItem> tvCertVerification;
    @FXML
    public ComboBox<String> cbFamily;
    @FXML
    public TextField tfPm;
    @FXML
    Button btnApply;
    @FXML
    CheckBox cbxOrderable;
    @FXML
    ContextMenu cmCertActions;
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
    private MultiEditor multiEditor = null;
    private CertificateVerificationTable certificateVerificationTable;
    private ComboBoxEqTypeSelector comboBoxEqTypeSelector;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        certificateVerificationTable = new CertificateVerificationTable(this);
        certificateVerificationTable.display(CoreModule.getProducts().getTableView().getSelectionModel().getSelectedItems(),
                new CheckParameters());
        comboBoxEqTypeSelector = new ComboBoxEqTypeSelector(cbType, certificateVerificationTable);

        cmCertActions.getItems().get(3).setDisable(CoreModule.getUsers().getCurrentUser().getProfile().getName().equals(Profile.COMMON_ACCESS));

        rmiTypeFilter.selectedProperty().addListener((observable, oldValue, newValue) -> {
            String prevType = cbType.getValue();
            certificateVerificationTable.display(certificateVerificationTable.getCheckParameters().setEqTypeFiltered(newValue));
            comboBoxEqTypeSelector.refresh(prevType);
        });

        initFamilySelector();
        initBlockSelector();
    }

    private void initBlockSelector() {
        cbxOrderable.setOnMouseClicked(event -> {
            Product pr = ProductEditorWindowActions.getEditedItem();
            OrderAccessibility oa = CoreModule.getOrdersAccessibility().getOrderAccessibilityByStatusCode(pr.getDchain());

            boolean isOrderable = oa == null ? false : oa.isOrderable();
            cbxOrderable.setSelected(isOrderable);
            cbxOrderable.setIndeterminate(!isOrderable);
        });
    }

    private void initFamilySelector() {
        cbFamily.setOnAction(event -> {
            ProductFamily pf = CoreModule.getProductFamilies().getFamilyByName(cbFamily.getValue());

            if (pf != null) tfPm.setText(pf.getResponsible());
            else tfPm.setText("");
        });
    }

    public void apply() {
        ProductEditorWindowActions.apply(this, multiEditor);
        ProductEditorWindowActions.setMultiEditor(null);
        multiEditor = null;
        ((Stage) tvCertVerification.getScene().getWindow()).close();
    }

    public void cancel() {
        ProductEditorWindowActions.setMultiEditor(null);
        multiEditor = null;
        ((Stage) tvCertVerification.getScene().getWindow()).close();
    }

    public void actionSelectManualFile() {
        File manualFile = Dialogs.selectFile(CertificateEditorWindow.getStage());
        if (manualFile != null) {
            if (new File(CoreModule.getFolders().getManualsFolder().getPath() + "\\" + manualFile.getName()).exists()) {
                Dialogs.showMessage("Добавление описания", "Описание с таким именем уже существует");
            } else {
                File destination = new File(CoreModule.getFolders().getManualsFolder().getPath() + "\\" +
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
        CertificateVerificationItem cv = tvCertVerification.getSelectionModel().getSelectedItem();
        if (cv == null) return;

        File file = new File(CoreModule.getFolders().getCertFolder().getPath() + "\\" + cv.getFile());
        ArrayList<File> files = new ArrayList<>();
        if (file.exists() && file.getPath().endsWith(".pdf")) files.add(file);
        Utils.copyFilesToClipboard(files);
    }

    public void getAllCertificatesFiles() {
        ArrayList<File> files = new ArrayList<>();
        for (CertificateVerificationItem cv : tvCertVerification.getItems()) {
            if (cv.getFile() != null && !cv.getFile().isEmpty()) {
                File file = new File(CoreModule.getFolders().getCertFolder().getPath() + "\\" + cv.getFile());
                if (file.exists() && file.getPath().endsWith(".pdf")) files.add(file);
            }
        }
        Utils.copyFilesToClipboard(files);
    }

    public void configNorms() {
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
