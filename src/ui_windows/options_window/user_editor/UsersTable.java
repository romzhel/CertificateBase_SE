package ui_windows.options_window.user_editor;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class UsersTable {
    private TableView<User> tableView;

    public UsersTable(TableView<User> tabView){
        tableView = tabView;

        String fields[] = new String[]{"name", "productFamilies", "profile"};
        String titles[] = new String[]{"Пользователь", "Линейки оборудования", "Профиль"};
        int widths[] = new int[]{250, 400, 200};

        TableColumn<User, String> col;
        for (int i = 0; i < fields.length; i++) {

            col = new TableColumn<>(titles[i]);
            col.setMinWidth(widths[i]);

            if (fields[i].equals("name")) {
                col.setCellValueFactory(param -> new SimpleStringProperty( param.getValue().getName()+
                        " " + param.getValue().getSurname()));
            } else if (fields[i].equals("profile")){
                col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getProfile().getName()));
            } else col.setCellValueFactory(new PropertyValueFactory<>(fields[i]));

            tableView.getColumns().add(col);
        }

        tableView.getItems().addAll(Users.getInstance().getItems());
    }

    public User getSelectedItem(){
        return tableView.getItems().get(tableView.getSelectionModel().getSelectedIndex());
    }

    public TableView<User> getTableView(){
        return tableView;
    }
}
