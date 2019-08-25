package ui_windows.product.productEditorWindow;

import core.CoreModule;
import core.Dialogs;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import ui_windows.options_window.profile_editor.Profile;
import ui_windows.product.Product;
import ui_windows.options_window.certificates_editor.CertificateEditorWindow;
import ui_windows.options_window.certificates_editor.certificate_content_editor.certificatesChecker.CertificateVerificationItem;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;
import ui_windows.options_window.product_lgbk.LgbkAndParent;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.productEditorWindow.configNormsWindow.ConfigNormsWindow;
import utils.AutoCompleteComboBoxListener;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ProductEditorWindowController implements Initializable {

    @FXML
    TableView<CertificateVerificationItem> tvCertVerification;

    @FXML
    ComboBox<String> cbType;

    @FXML
    ComboBox<String> cbFamily;

    @FXML
    TextField tfPm;

    @FXML
    Button btnApply;

    @FXML
    CheckBox cbxOrderable;

    @FXML
    TextField tfAccessibility;

    @FXML
    CheckBox cbxNotUsed;

    @FXML
    ContextMenu cmCertActions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ProductEditorWindowActions.setTableView(tvCertVerification);
        tvCertVerification.setPlaceholder(new Text("Нет данных для отображения"));
        cmCertActions.getItems().get(3).setDisable(CoreModule.getUsers().getCurrentUser().getProfile().getName().equals(Profile.COMMON_ACCESS));

        tvCertVerification.itemsProperty().get().addListener((ListChangeListener<CertificateVerificationItem>) c -> {
            tfAccessibility.getStyleClass().removeAll("itemStrikethroughRed", "itemStrikethroughBrown",
                    "itemStrikethroughGreen", "itemStrikethroughBlack");
            tfAccessibility.getStyleClass().add(CoreModule.getCertificates().getCertificatesChecker().getCheckStatusResultStyle());
        });

        ProductEditorWindowActions.fillCertificateVerificationTable();

        new AutoCompleteComboBoxListener<>(cbType, false);
//        cbType.getItems().addAll(CoreModule.getProductTypes().getPreparedTypes());

        Product editedProduct = CoreModule.getProducts().getTableView().getSelectionModel().getSelectedItem();
        LgbkAndParent lgbkAndParent = CoreModule.getProductLgbkGroups().getLgbkAndParent(
                new ProductLgbk(editedProduct.getLgbk(), editedProduct.getHierarchy()));
        boolean globalNotUsed = lgbkAndParent.getLgbkItem().isNotUsed() || lgbkAndParent.getLgbkParent().isNotUsed();

        if (editedProduct.isNotused()) {
//            cbxNotUsed.getStyleClass().removeAll("check-box");
            cbxNotUsed.setSelected(true);
        } else if (globalNotUsed) {
//            cbxNotUsed.getStyleClass().removeAll("check-box");
//            cbxNotUsed.getStyleClass().add("check-box");
            cbxNotUsed.setSelected(true);
        } else {
//            cbxNotUsed.getStyleClass().removeAll("check-box");
            cbxNotUsed.setSelected(false);
        }

        String[] colNames = new String[]{"norm", "matchedPart", "prodType", "file", "status"};
        String[] titles = new String[]{"Регламент", "Соответствие", "Тип продукции", "Файл сертификата", "Актуальность"};

        for (int i = 0; i < colNames.length; i++) {
            TableColumn<CertificateVerificationItem, String> col = new TableColumn<>(titles[i]);
            col.setCellValueFactory(new PropertyValueFactory<>(colNames[i]));
            col.setPrefWidth(200);

            if (colNames[i].equals("norm")) {
                col.setCellFactory(new Callback<TableColumn<CertificateVerificationItem, String>, TableCell<CertificateVerificationItem, String>>() {
                    @Override
                    public TableCell<CertificateVerificationItem, String> call(TableColumn<CertificateVerificationItem, String> param) {
                        return new TableCell<CertificateVerificationItem, String>() {

                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);

                                if (!isEmpty()) {
                                    setText(item);

                                    CertificateVerificationItem cv = param.getTableView().getItems().get(getIndex());
                                    if (cv.getCertificate() == null) {
                                        setTextFill(Color.RED);
                                        setStyle("-fx-font-weight: bold");
                                    } else {
                                        setTextFill(Color.BLACK);
                                    }
                                }
                            }
                        };
                    }
                });
            }

            if (colNames[i].equals("file")) {
                col.setCellFactory(new Callback<TableColumn<CertificateVerificationItem, String>, TableCell<CertificateVerificationItem, String>>() {
                    @Override
                    public TableCell<CertificateVerificationItem, String> call(TableColumn<CertificateVerificationItem, String> param) {
                        return new TableCell<CertificateVerificationItem, String>() {

                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);

                                if (!isEmpty()) {
                                    setText(item);

                                    File file = new File(CoreModule.getFolders().getCertFolder().getPath() + "\\" + item);
                                    if (file.exists()) setTextFill(Color.GREEN);
                                    else setTextFill(Color.RED);
                                }
                            }
                        };
                    }
                });
            }

            if (colNames[i].equals("status")) {
                col.setCellFactory(new Callback<TableColumn<CertificateVerificationItem, String>, TableCell<CertificateVerificationItem, String>>() {
                    @Override
                    public TableCell<CertificateVerificationItem, String> call(TableColumn<CertificateVerificationItem, String> param) {
                        return new TableCell<CertificateVerificationItem, String>() {

                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);

                                if (!isEmpty()) {
                                    setText(item);

                                    CertificateVerificationItem cv = param.getTableView().getItems().get(getIndex());
                                    if (cv.getStatus().equals(CertificateVerificationItem.ABSENT_TEXT)) {
                                        setTextFill(Color.RED);
                                        setStyle("-fx-font-weight: bold");
                                    } else {
                                        setTextFill(Color.BLACK);
                                    }
                                }
                            }
                        };
                    }
                });
            }

            tvCertVerification.getColumns().add(col);
        }

        tvCertVerification.setOnMouseClicked(event -> {//double click on product
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    File file = new File(CoreModule.getFolders().getCertFolder().getPath() + "\\" +
                            tvCertVerification.getSelectionModel().getSelectedItem().getFile());

                    Utils.openFile(file);
                }
            }
        });

        cbFamily.setOnAction(event -> {
            ProductFamily pf = CoreModule.getProductFamilies().getFamilyByName(cbFamily.getValue());

            if (pf != null) tfPm.setText(pf.getResponsible());
            else tfPm.setText("");
        });

        cbxOrderable.setOnMouseClicked(event -> {
            Product pr = ProductEditorWindowActions.getEditedItem();
            OrderAccessibility oa = CoreModule.getOrdersAccessibility().getOrderAccessibilityByStatusCode(pr.getDchain());

            boolean isOrderable = oa == null ? false : oa.isOrderable();
            cbxOrderable.setSelected(isOrderable);
            cbxOrderable.setIndeterminate(!isOrderable);
        });

    }

    public void apply() {
        ProductEditorWindowActions.apply(((Stage) tvCertVerification.getScene().getWindow()));
    }

    public void cancel() {
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
        if (file.exists()) files.add(file);
        Utils.copyFilsToClipboard(files);
    }

    public void getAllCertificatesFiles() {
        ArrayList<File> files = new ArrayList<>();
        for (CertificateVerificationItem cv : tvCertVerification.getItems()) {
            File file = new File(CoreModule.getFolders().getCertFolder().getPath() + "\\" + cv.getFile());
            if (file.exists()) files.add(file);
        }
        Utils.copyFilsToClipboard(files);
    }

    public void configNorms() {
        new ConfigNormsWindow(ProductEditorWindow.getStage());
    }
}
