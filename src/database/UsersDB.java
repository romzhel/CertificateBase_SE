package database;

import core.CoreModule;
import core.Dialogs;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;
import ui_windows.options_window.user_editor.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class UsersDB implements Request {
    PreparedStatement addData, updateData, deleteData;

    public UsersDB() {
        try {
            addData = CoreModule.getDataBase().getDbConnection().prepareStatement("INSERT INTO " +
                            "users (name, surname, product_families, password, pc_names, profile_id) " +
                            "VALUES (?, ?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = CoreModule.getDataBase().getDbConnection().prepareStatement("UPDATE users " +
                    "SET name = ?, surname = ?, product_families = ?, password = ?, pc_names = ?, profile_id = ? " +
                    "WHERE id = ?");
            deleteData = CoreModule.getDataBase().getDbConnection().prepareStatement("DELETE FROM users " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            System.out.println("users prepared statements exception" + e.getMessage());
        }
    }

    @Override
    public ArrayList getData() {
        ArrayList<User> users = new ArrayList<>();
        try {
            ResultSet rs = CoreModule.getDataBase().getData("SELECT * FROM users");

            while (rs.next()) {
                users.add(new User(rs));
            }

        } catch (SQLException e) {
            System.out.println("SQL exception users" + e.getMessage());
        }

        return users;
    }

    @Override
    public boolean putData(Object object) {
        if (object instanceof User) {
            User user = (User) object;

            MainWindow.setProgress(1.0);

            try {
                addData.setString(1, user.getName());
                addData.setString(2, user.getSurname());
                addData.setString(3, user.getProductFamilies());
                addData.setString(4, user.getPassword());
                addData.setString(5, user.getPcNames());
                addData.setInt(6, user.getProfile().getId());

                if (addData.executeUpdate() > 0) {//successful
                    ResultSet rs = addData.getGeneratedKeys();

                    if (rs.next()) {
                        user.setId(rs.getInt(1));
//                        System.out.println("new order accebility ID = " + rs.getInt(1));
                        MainWindow.setProgress(0.0);
                        return true;
                    }
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка работы с БД");
                }

            } catch (SQLException e) {
                System.out.println("exception of user writing to BD" + e.getMessage());
            }
            MainWindow.setProgress(0.0);

        }
        return false;
    }

    @Override
    public boolean updateData(Object object) {
        if (object instanceof User) {
            User user = (User) object;

            MainWindow.setProgress(1.0);

            try {
                updateData.setString(1, user.getName());
                updateData.setString(2, user.getSurname());
                updateData.setString(3, user.getProductFamilies());
                updateData.setString(4, user.getPassword());
                updateData.setString(5, user.getPcNames());
                updateData.setInt(6, user.getProfile().getId());
                updateData.setInt(7, user.getId());

                if (updateData.executeUpdate() > 0) {//successful
                    MainWindow.setProgress(0.0);
                    return true;
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка работы с БД");
                }

            } catch (SQLException e) {
                System.out.println("exception of user writing to BD" + e.getMessage());
            }

            MainWindow.setProgress(0.0);
        }
        return false;
    }

    @Override
    public boolean deleteData(Object object) {
        if (object instanceof User) {
            User user = (User) object;

            MainWindow.setProgress(1.0);

            try {
                deleteData.setInt(1, user.getId());

                if (deleteData.executeUpdate() > 0) {//successful
                    MainWindow.setProgress(0.0);
                    return true;
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка работы с БД");
                }

            } catch (SQLException e) {
                System.out.println("exception of user deleting in BD" + e.getMessage());
            }

            MainWindow.setProgress(0.0);
        }

        return false;
    }
}
