package ui_windows.options_window.user_editor;

import database.UsersDB;
import utils.Utils;

import java.util.ArrayList;

public class Users {
    private static Users instance;
    private ArrayList<User> users;
    private UsersTable table;
    private User currentUser;

    private Users() {
        users = new ArrayList<>();
    }

    public static Users getInstance() {
        if (instance == null) {
            instance = new Users();
        }
        return instance;
    }

    public Users getFromDB() {
        users = new UsersDB().getData();
        return this;
    }

    public User checkCurrentUser(String value) {
        if (value != null && !value.isEmpty()) {
            for (User user : users) {
                if (user != null) {
                    if (((user.getPassword() != null && user.getPassword().equals(value))) ||
                            ((user.getPcNames() != null && Utils.stringToList(user.getPcNames()).indexOf(value) >= 0))) {
                        currentUser = user;
                        return user;
                    }
                }
            }
        }
        currentUser = new User();
        return new User();
    }

    public void addItem(User user) {
        if (new UsersDB().putData(user)) {
            users.add(user);
            table.getTableView().getItems().add(user);
            table.getTableView().refresh();
        }
    }

    public void editItem(User user) {
        if (new UsersDB().updateData(user)) {
            table.getTableView().refresh();
        }
    }

    public void removeItem(User user) {
        if (new UsersDB().deleteData(user)) {
            users.remove(user);
            table.getTableView().getItems().remove(user);
            table.getTableView().refresh();
        }
    }

    public boolean isPcNameUsed(String pcName) {
        for (User user : users) {
            if (Utils.stringToList(user.getPcNames()).indexOf(pcName) >= 0) return true;
        }
        return false;
    }

    public boolean isPasswordUsed(String password) {
        for (User user : users) {
            if (user.getPassword().equals(password)) return true;
        }
        return false;
    }

    public boolean userExistsById(int id) {
        for (User user : users) {
            if (user.getId() == id) return true;
        }
        return false;
    }

    public ArrayList<User> getItems() {
        return users;
    }

    public UsersTable getTable() {
        return table;
    }

    public void setTable(UsersTable table) {
        this.table = table;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
