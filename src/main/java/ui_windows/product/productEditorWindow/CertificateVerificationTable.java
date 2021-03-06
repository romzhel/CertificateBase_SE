package ui_windows.product.productEditorWindow;

import files.Folders;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import ui.Dialogs;
import ui_windows.product.Product;
import ui_windows.product.certificatesChecker.CertificateVerificationItem;
import ui_windows.product.certificatesChecker.CertificatesChecker;
import ui_windows.product.certificatesChecker.CheckParameters;
import utils.Utils;

import java.util.TreeSet;

import static ui_windows.product.certificatesChecker.CertificateVerificationItem.ABSENT_TEXT;
import static ui_windows.product.certificatesChecker.CertificateVerificationItem.NOT_OK_TEXT;
import static ui_windows.product.certificatesChecker.CertificatesChecker.CERT_NO_NEEDED;

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
                                    if (cv.getCertificate() == null && !cv.getNorm().equals(CERT_NO_NEEDED)) {
                                        setTextFill(Color.RED);
                                        setStyle("-fx-font-weight: bold");
                                    } else if (cv.getNorm().equals(CERT_NO_NEEDED)) {
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

                                    try {
                                        Folders.getInstance().getCalcCertFile(item);
                                        setTextFill(Color.GREEN);
                                    } catch (Exception e) {
                                        setTextFill(Color.RED);
                                    }
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
                                    if (cv.getStatus().startsWith(NOT_OK_TEXT)) {
                                        setTextFill(Color.RED);
                                        if (cv.getStatus().equals(ABSENT_TEXT)) setStyle("-fx-font-weight: bold");
                                    } else if (cv.getNorm().equals(CERT_NO_NEEDED)) {
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
                    CertificateVerificationItem cvi = tableView.getSelectionModel().getSelectedItem();
                    if (cvi == null) {
                        return;
                    }

                    try {
                        Utils.openFile(Folders.getInstance().getCalcCertFile(cvi.getFile()).toFile());
                    } catch (Exception e) {
                        Dialogs.showMessageTS("Ошибка открытия файла сертификата", e.getMessage());
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

        if (checkParameters.isEqTypeFiltered()) {
            tableView.setStyle("-fx-border-color: orange;");
            tableView.setTooltip(new Tooltip("Фильтр по совпадению названия активен"));
        } else {
            tableView.setStyle("-fx-border-color: -fx-box-border;");
            tableView.setTooltip(null);
        }

//        CoreModule.getProducts().getTableView().refresh();
    }

    public CheckParameters getCheckParameters() {
        return checkParameters;
    }

    public void setProductsForCheckingAndDisplaying(ObservableList<Product> productsForCheckingAndDisplaying) {
        this.productsForCheckingAndDisplaying = productsForCheckingAndDisplaying;
    }

    public ObservableList<CertificateVerificationItem> getItems() {
        return tableView.getItems();
    }

    public TreeSet<String> getProductTypes() {
        return productTypes;
    }
}
