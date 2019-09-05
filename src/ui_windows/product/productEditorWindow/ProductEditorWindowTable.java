package ui_windows.product.productEditorWindow;

import core.CoreModule;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import ui_windows.options_window.certificates_editor.certificate_content_editor.certificatesChecker.CertificateVerificationItem;
import ui_windows.options_window.certificates_editor.certificate_content_editor.certificatesChecker.CertificatesChecker;
import utils.Utils;

import java.io.File;

public class ProductEditorWindowTable {

    public ProductEditorWindowTable(TableView<CertificateVerificationItem> tableView) {
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
                    File file = new File(CoreModule.getFolders().getCertFolder().getPath() + "\\" +
                            tableView.getSelectionModel().getSelectedItem().getFile());

                    Utils.openFile(file);
                }
            }
        });

        tableView.setPlaceholder(new Text("Нет данных для отображения"));
    }
}
