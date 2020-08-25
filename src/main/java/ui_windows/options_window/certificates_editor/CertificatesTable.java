package ui_windows.options_window.certificates_editor;

import files.Folders;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class CertificatesTable {
    private static CertificatesTable instance;
    private TableView<Certificate> tableView;

    private CertificatesTable() {
    }

    public static CertificatesTable getInstance() {
        if (instance == null) {
            instance = new CertificatesTable();
        }
        return instance;
    }

    public void init(TableView<Certificate> tableView) {
        this.tableView = tableView;
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));

        tableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("expirationDate"));
        tableView.getColumns().get(1).setStyle("-fx-alignment: CENTER");

        TableColumn<Certificate, String> tcem = new TableColumn<>("Осталось (мес.");
        tcem.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getMonthToExpiration()));
        tcem.setComparator((o1, o2) -> {
            String ob1 = o1.length() > 1 ? o1 : "0" + o1;
            String ob2 = o2.length() > 1 ? o2 : "0" + o2;
            return ob1.compareTo(ob2);
        });
        tcem.setPrefWidth(tableView.getColumns().get(2).getPrefWidth());
        tcem.setText(tableView.getColumns().get(2).getText());
        tcem.setStyle("-fx-alignment: CENTER");
        tableView.getColumns().set(2, tcem);

        TableColumn<Certificate, String> tcfn = new TableColumn<>("Имя файла");
        tcfn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        tcfn.setPrefWidth(tableView.getColumns().get(3).getPrefWidth());
        tcfn.setText(tableView.getColumns().get(3).getText());

        tcfn.setCellFactory(new Callback<TableColumn<Certificate, String>, TableCell<Certificate, String>>() {
            @Override
            public TableCell<Certificate, String> call(TableColumn<Certificate, String> param) {
                return new TableCell<Certificate, String>() {

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

        tableView.getColumns().set(3, tcfn);

        tableView.getItems().addAll(Certificates.getInstance().getCertificates());
    }

    public TableView<Certificate> getTableView() {
        return tableView;
    }

    public void addItem(Certificate certificate) {
        tableView.getItems().add(certificate);
        tableView.refresh();
    }
}
