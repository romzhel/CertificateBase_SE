package ui_windows.options_window.certificates_editor;

import core.CoreModule;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import ui_windows.options_window.requirements_types_editor.RequirementType;
import utils.Utils;

import java.io.File;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CertificatesTable {
    private TableView<Certificate> tableView;

    public CertificatesTable(TableView<Certificate> tableView){
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

                                File file = new File(CoreModule.getFolders().getCertFolder().getPath() + "\\" + item);
                                if (file.exists()) setTextFill(Color.GREEN);
                                else setTextFill(Color.RED);
                            }
                        }
                    };
                }
            });

        tableView.getColumns().set(3, tcfn);

        tableView.getItems().addAll(CoreModule.getCertificates().getCertificates());
    }

    public TableView<Certificate> getTableView() {
        return tableView;
    }

    public void addItem(Certificate certificate){
        tableView.getItems().add(certificate);
        tableView.refresh();
    }
}
