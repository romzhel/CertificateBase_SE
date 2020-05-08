package ui_windows.options_window.profile_editor;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import static ui_windows.options_window.profile_editor.SimpleRight.valueOf;

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
        fileCol.setMinWidth(200);
        TableColumn<Profile, String> fileOpenCol = new TableColumn<>("Импорт");
        fileOpenCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileMenuOpen().getName()));
        fileOpenCol.setCellFactory(ComboBoxTableCell.forTableColumn("HIDE", "DISPLAY"));
        fileOpenCol.setOnEditCommit(event -> {
            event.getRowValue().setFileMenuOpen(valueOf(event.getNewValue()));
        });
        fileOpenCol.setMinWidth(100);
        TableColumn<Profile, String> filePrice = new TableColumn<>("Прайс");
        filePrice.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileMenuExportPrice().getName()));
        filePrice.setCellFactory(ComboBoxTableCell.forTableColumn("HIDE", "DISPLAY"));
        filePrice.setOnEditCommit(event -> {
            event.getRowValue().setFileMenuExportPrice(valueOf(event.getNewValue()));
        });
        filePrice.setMinWidth(100);

        fileCol.getColumns().addAll(fileOpenCol, filePrice);
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

        TableColumn<Profile, String> col5 = new TableColumn<>("Прайс-листы");
        col5.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPriceLists().getName()));
        col5.setCellFactory(ComboBoxTableCell.forTableColumn("HIDE", "DISPLAY", "FULL"));
        col5.setOnEditCommit(event -> {
            event.getRowValue().setPriceLists(valueOf(event.getNewValue()));
        });
        col5.setMinWidth(100);

        optionsCol.getColumns().addAll(col1, col2, col3, col4, col5);
        tableView.getColumns().add(optionsCol);
        tableView.getItems().addAll(Profiles.getInstance().getItems());
        tableView.setEditable(true);
    }

    public TableView<Profile> getTableView() {
        return tableView;
    }
}
