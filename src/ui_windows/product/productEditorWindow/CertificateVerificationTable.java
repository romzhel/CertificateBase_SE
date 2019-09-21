package ui_windows.product.productEditorWindow;

import core.CoreModule;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import ui_windows.product.Product;
import ui_windows.product.ProductTypes;
import ui_windows.product.certificatesChecker.CertificateVerificationItem;
import ui_windows.product.certificatesChecker.CertificatesChecker;
import ui_windows.product.certificatesChecker.CheckParameters;
import utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class CertificateVerificationTable {
    private TableView<CertificateVerificationItem> tableView;
    private CheckParameters checkParameters;
    private ProductEditorWindowController pewc;
    private ObservableList<Product> productsForCheckingAndDisplaying;
    private TreeSet<String> productTypes;

    public CertificateVerificationTable(ProductEditorWindowController pewc) {
        this.pewc = pewc;
        this.tableView = pewc.tvCertVerification;
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
                                    if (cv.getCertificate() == null && !cv.getNorm().equals(CertificatesChecker.CERT_NO_NEEDED)) {
                                        setTextFill(Color.RED);
                                        setStyle("-fx-font-weight: bold");
                                    } else if (cv.getNorm().equals(CertificatesChecker.CERT_NO_NEEDED)) {
                                        setTextFill(Color.GREEN);
                                        setStyle("-fx-font-weight: bold");
                                    } else {
                                        setTextFill(Color.BLACK);
                                        setStyle("-fx-font-weight: normal");
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
                                    } else if (cv.getNorm().equals(CertificatesChecker.CERT_NO_NEEDED)) {
                                        setTextFill(Color.GREEN);
                                        setStyle("-fx-font-weight: bold");
                                    } else {
                                        setTextFill(Color.BLACK);
                                        setStyle("-fx-font-weight: normal");
                                    }
                                }
                            }
                        };
                    }
                });
            }

            tableView.getColumns().add(col);
        }

        tableView.setOnMouseClicked(event -> {//double click on product
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    String fileName = tableView.getSelectionModel().getSelectedItem().getFile();

                    if (fileName != null && !fileName.isEmpty()) {
                        File certFile = new File(CoreModule.getFolders().getCertFolder().getPath() + "\\" + fileName);

                        if (certFile != null && certFile.exists()) Utils.openFile(certFile);
                    }
                }
            }
        });

        tableView.setPlaceholder(new Text("Нет данных для отображения"));
    }

    public void display(ObservableList<Product> productsForCheckingAndDisplaying, CheckParameters checkParameters) {
        this.productsForCheckingAndDisplaying = productsForCheckingAndDisplaying;
        display(checkParameters);
    }

    public void display(CheckParameters checkParameters) {
        this.checkParameters = checkParameters;
        tableView.getItems().clear();
        CertificatesChecker certificatesChecker = new CertificatesChecker(productsForCheckingAndDisplaying, checkParameters);
        pewc.tfAccessibility.getStyleClass().add(certificatesChecker.getCheckStatusResultStyle(pewc.tfAccessibility.getStyleClass()));
        tableView.getItems().addAll(certificatesChecker.getResultTableItems());
        tableView.refresh();

        this.productTypes = certificatesChecker.getProductTypes();

//        CoreModule.getProducts().getTableView().refresh();
    }

    public CheckParameters getCheckParameters() {
        return checkParameters;
    }

    public void setProductsForCheckingAndDisplaying(ObservableList<Product> productsForCheckingAndDisplaying) {
        this.productsForCheckingAndDisplaying = productsForCheckingAndDisplaying;
    }

    public ObservableList<CertificateVerificationItem> getItems(){
        return tableView.getItems();
    }

    public TreeSet<String> getProductTypes() {
        return productTypes;
    }
}
