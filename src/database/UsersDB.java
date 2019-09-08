package database;

import ui_windows.main_window.MainWindow;
import ui_windows.options_window.user_editor.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class UsersDB extends DbRequest {

    public UsersDB() {
        super();
        try {
            addData = connection.prepareStatement("INSERT INTO " +
                            "users (name, surname, product_families, password, pc_names, profile_id) " +
                            "VALUES (?, ?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = connection.prepareStatement("UPDATE users " +
                    "SET name = ?, surname = ?, product_families = ?, password = ?, pc_names = ?, profile_id = ? " +
                    "WHERE id = ?");
            deleteData = connection.prepareStatement("DELETE FROM users " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            logAndMessage("users prepared statements exception" + e.getMessage());
            finalActions();
        }
    }

    public ArrayList getData() {
        ArrayList<User> users = new ArrayList<>();
        try {
            ResultSet rs = connection.prepareStatement("SELECT * FROM users").executeQuery();

            while (rs.next()) {
                users.add(new User(rs));
            }
        } catch (SQLException e) {
            logAndMessage("SQL exception users getting" + e.getMessage());
        }
        return users;

    }

    public boolean putData(User user) {
        try {
            MainWindow.setProgress(1.0);

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
                    return true;
                }
            } else {
                logAndMessage("SQL exception users inserting");
            }
        } catch (SQLException e) {
            logAndMessage("SQL exception users inserting " + e.getMessage());
        } finally {
            finalActions();
        }

        return false;
    }

    public boolean updateData(User user) {
        try {
            MainWindow.setProgress(1.0);

            updateData.setString(1, user.getName());
            updateData.setString(2, user.getSurname());
            updateData.setString(3, user.getProductFamilies());
            updateData.setString(4, user.getPassword());
            updateData.setString(5, user.getPcNames());
            updateData.setInt(6, user.getProfile().getId());
            updateData.setInt(7, user.getId());

            if (updateData.executeUpdate() > 0) {//successful
                return true;
            } else {
                logAndMessage("SQL exception users updating ");
            }
        } catch (SQLException e) {
            logAndMessage("SQL exception users updating " + e.getMessage());
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean deleteData(User user) {
        try {
            MainWindow.setProgress(1.0);

            deleteData.setInt(1, user.getId());

            if (deleteData.executeUpdate() > 0) {//successful
                return true;
            } else {
                logAndMessage("SQL exception users deleting ");
            }
        } catch (SQLException e) {
            logAndMessage("SQL exception users updating " + e.getMessage());
        } finally {
            finalActions();
        }
        return false;
    }
}
