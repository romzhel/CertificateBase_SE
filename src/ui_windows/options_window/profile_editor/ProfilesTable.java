package ui_windows.options_window.profile_editor;

import core.CoreModule;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import static ui_windows.options_window.profile_editor.SimpleRight.*;

public class ProfilesTable {
    private TableView<Profile> tableView;

    public ProfilesTable(TableView<Profile> table) {
        tableView = table;

        TableColumn<Profile, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setMinWidth(200);
        tableView.getColumns().add(nameCol);

        TableColumn<Profile, String> prodCol = new TableColumn<>("Позиции");
        prodCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getProducts().getName()));
        prodCol.setCellFactory(ComboBoxTableCell.forTableColumn("DISPLAY", "OWN", "FULL"));
        prodCol.setOnEditCommit(event -> {
            event.getRowValue().setProducts(valueOf(event.getNewValue()));
        });
        prodCol.setMinWidth(100);
        tableView.getColumns().add(prodCol);

        TableColumn<Profile, String> fileCol = new TableColumn<>("Меню Файл");
        fileCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileMenu().getName()));
        fileCol.setCellFactory(ComboBoxTableCell.forTableColumn("HIDE", "DISPLAY"));
        fileCol.setOnEditCommit(event -> {
            event.getRowValue().setFileMenu(valueOf(event.getNewValue()));
        });
        fileCol.setMinWidth(100);
        tableView.getColumns().add(fileCol);


        TableColumn<Profile, String> optionsCol = new TableColumn<>("Настройки");
//
        TableColumn<Profile, String> col1 = new TableColumn<>("Сертификаты");
        col1.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCertificates().getName()));
        col1.setCellFactory(ComboBoxTableCell.forTableColumn("HIDE", "DISPLAY", "OWN", "FULL"));
        col1.setOnEditCommit(event -> {
            event.getRowValue().setCertificates(valueOf(event.getNewValue()));
            System.out.println(valueOf(event.getNewValue()).getName());
        });
        col1.setMinWidth(100);

        TableColumn<Profile, String> col2 = new TableColumn<>("Направления");
        col2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFamilies().getName()));
        col2.setCellFactory(ComboBoxTableCell.forTableColumn("HIDE", "DISPLAY", "FULL"));
        col2.setOnEditCommit(event -> {
            event.getRowValue().setFamilies(valueOf(event.getNewValue()));
        });
        col2.setMinWidth(100);

        TableColumn<Profile, String> col3 = new TableColumn<>("Доступность");
        col3.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getOrderAccessible().getName()));
        col3.setCellFactory(ComboBoxTableCell.forTableColumn("HIDE", "DISPLAY", "FULL"));
        col3.setOnEditCommit(event -> {
            event.getRowValue().setOrderAccessible(valueOf(event.getNewValue()));
        });
        col3.setMinWidth(100);

        TableColumn<Profile, String> col4 = new TableColumn<>("Пользователи");
        col4.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getUsers().getName()));
        col4.setCellFactory(ComboBoxTableCell.forTableColumn("HIDE", "DISPLAY", "FULL"));
        col4.setOnEditCommit(event -> {
            event.getRowValue().setUsers(valueOf(event.getNewValue()));
        });
        col4.setMinWidth(100);


        optionsCol.getColumns().addAll(col1, col2, col3, col4);
        tableView.getColumns().add(optionsCol);
        tableView.getItems().addAll(CoreModule.getProfiles().getItems());
        tableView.setEditable(true);
    }

    public TableView<Profile> getTableView() {
        return tableView;
    }
}
