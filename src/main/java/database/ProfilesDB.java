package database;

import ui_windows.options_window.profile_editor.Profile;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ProfilesDB extends DbRequest {

    public ProfilesDB() {
        super();
        try {
            addData = connection.prepareStatement("INSERT INTO " +
                            "profiles (name, products, file_menu, file_menu_open, file_menu_export_price, certificates, " +
                            "families, orderable, users, price_lists) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = connection.prepareStatement("UPDATE profiles " +
                    "SET name = ?, products = ?, file_menu = ?, file_menu_open = ?, file_menu_export_price = ?, " +
                    "certificates = ?, families = ?, orderable = ?, users = ?, price_lists = ? WHERE id = ?");
            deleteData = connection.prepareStatement("DELETE FROM profiles WHERE id = ?");
        } catch (SQLException e) {
            logAndMessage("prepared statements exception", e);
            finalActions();
        }
    }

    public ArrayList getData() {
        ArrayList<Profile> profiles = new ArrayList<>();
        try {
            ResultSet rs = connection.prepareStatement("SELECT * FROM profiles").executeQuery();

            while (rs.next()) {
                profiles.add(new Profile(rs));
            }

            rs.close();
        } catch (SQLException e) {
            logAndMessage("profiles getting from DB error ", e);
        }
        return profiles;
    }

    public boolean putData(Profile prof) {
        try {
            addData.setString(1, prof.getName());
            addData.setInt(2, prof.getProducts().ordinal());
            addData.setInt(3, prof.getFileMenu().ordinal());
            addData.setInt(4, prof.getFileMenuOpen().ordinal());
            addData.setInt(5, prof.getFileMenuExportPrice().ordinal());
            addData.setInt(6, prof.getCertificates().ordinal());
            addData.setInt(7, prof.getFamilies().ordinal());
            addData.setInt(8, prof.getOrderAccessible().ordinal());
            addData.setInt(9, prof.getUsers().ordinal());
            addData.setInt(10, prof.getPriceLists().ordinal());

            if (addData.executeUpdate() > 0) {//successful
                ResultSet rs = addData.getGeneratedKeys();

                if (rs.next()) {
                    prof.setId(rs.getInt(1));
                    return true;
                }
            } else {
                logAndMessage("", new RuntimeException("profile inserting into DB error"));
            }

        } catch (SQLException e) {
            logAndMessage("profile inserting into DB error", e);
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean updateData(Profile prof) {
        try {
            updateData.setString(1, prof.getName());
            updateData.setInt(2, prof.getProducts().ordinal());
            updateData.setInt(3, prof.getFileMenu().ordinal());
            updateData.setInt(4, prof.getFileMenuOpen().ordinal());
            updateData.setInt(5, prof.getFileMenuExportPrice().ordinal());
            updateData.setInt(6, prof.getCertificates().ordinal());
            updateData.setInt(7, prof.getFamilies().ordinal());
            updateData.setInt(8, prof.getOrderAccessible().ordinal());
            updateData.setInt(9, prof.getUsers().ordinal());
            updateData.setInt(10, prof.getPriceLists().ordinal());
            updateData.setInt(11, prof.getId());

            if (updateData.executeUpdate() > 0) {//successful
                return true;
            } else {
                logAndMessage("", new RuntimeException("profile updating in DB error"));
            }
        } catch (SQLException e) {
            logAndMessage("profile updating in DB error", e);
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean deleteData(Profile prof) {
        try {
            deleteData.setInt(1, prof.getId());
            deleteData.addBatch();

            int results[] = deleteData.executeBatch();

            for (int res : results) {
                if (res > 0) {
                    return true;//success
                }
            }
        } catch (SQLException e) {
            logAndMessage("exception of removing product type(s) from DB", e);
        } finally {
            finalActions();
        }
        return false;
    }
}
