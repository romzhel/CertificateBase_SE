package ui_windows.productEditorWindow;

import core.CoreModule;
import core.Dialogs;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import ui_windows.options_window.certificates_editor.Certificate;
import ui_windows.options_window.certificates_editor.CertificateEditorWindow;
import ui_windows.options_window.certificates_editor.CertificateVerification;
import ui_windows.main_window.Product;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.user_editor.User;
import utils.AutoCompleteComboBoxListener;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static ui_windows.options_window.profile_editor.SimpleRight.*;

public class ProductEditorWindowController implements Initializable {

    @FXML
    TableView<CertificateVerification> tvCertVerification;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new AutoCompleteComboBoxListener<>(cbType, false);
//        cbType.getItems().addAll(CoreModule.getProductTypes().getPreparedTypes());

        String[] colNames = new String[]{"norm", "matchedPart", "prodType", "file", "status"};
        String[] titles = new String[]{"Регламент", "Соответствие", "Тип продукции", "Файл сертификата", "Актуальность"};

        for (int i = 0; i < colNames.length; i++) {
            TableColumn<CertificateVerification, String> col = new TableColumn<>(titles[i]);
            col.setCellValueFactory(new PropertyValueFactory<>(colNames[i]));
            col.setPrefWidth(200);

            if (colNames[i].equals("file")) {
                col.setCellFactory(new Callback<TableColumn<CertificateVerification, String>, TableCell<CertificateVerification, String>>() {
                    @Override
                    public TableCell<CertificateVerification, String> call(TableColumn<CertificateVerification, String> param) {
                        return new TableCell<CertificateVerification, String>() {

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

            tvCertVerification.getColumns().add(col);
        }

        Product pr = ProductEditorWindowActions.getItem();
        tvCertVerification.getItems().addAll(CoreModule.getCertificates().checkCertificates(pr));

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
            OrderAccessibility oa = CoreModule.getOrdersAccessibility().getOrderAccessibilityByStatusCode(pr.getDchain());

            boolean isOrderable = oa == null ? false : oa.isOrderable();
            cbxOrderable.setSelected(isOrderable);
            cbxOrderable.setIndeterminate(!isOrderable);
        });

    }

    public void apply() {
        ProductEditorWindowActions.apply();
    }

    public void cancel() {
        ProductEditorWindow.getStage().close();
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
        CertificateVerification cv = tvCertVerification.getSelectionModel().getSelectedItem();
        if (cv == null) return;

        File file = new File(CoreModule.getFolders().getCertFolder().getPath() + "\\" + cv.getFile());
        ArrayList<File> files = new ArrayList<>();
        if (file.exists()) files.add(file);
        Utils.copyFilsToClipboard(files);
    }

    public void getAllCertificatesFiles() {
        ArrayList<File> files = new ArrayList<>();
        for (CertificateVerification cv : tvCertVerification.getItems()) {
            File file = new File(CoreModule.getFolders().getCertFolder().getPath() + "\\" + cv.getFile());
            if (file.exists()) files.add(file);
        }
        Utils.copyFilsToClipboard(files);
    }
}
