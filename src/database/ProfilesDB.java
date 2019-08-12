package database;

import core.CoreModule;
import core.Dialogs;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.profile_editor.Profile;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ProfilesDB implements Request {
    PreparedStatement addData, updateData, deleteData;

    public ProfilesDB() {
        try {
            addData = CoreModule.getDataBase().getDbConnection().prepareStatement("INSERT INTO " +
                            "profiles (name, products, file_menu, certificates, families, orderable, users) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = CoreModule.getDataBase().getDbConnection().prepareStatement("UPDATE profiles " +
                    "SET name = ?, products = ?, file_menu = ?, certificates = ?, families = ?, orderable = ?," +
                    "users = ? WHERE id = ?");
            deleteData = CoreModule.getDataBase().getDbConnection().prepareStatement("DELETE FROM profiles " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            System.out.println("prepared statements exception" + e.getMessage());
        }
    }

    @Override
    public ArrayList getData() {
        ArrayList<Profile> products = new ArrayList<>();
        try {
            ResultSet rs = CoreModule.getDataBase().getData("SELECT * FROM profiles");

            while (rs.next()) {
                products.add(new Profile(rs));
            }

        } catch (SQLException e) {
            System.out.println("SQL exception products, " + e.getMessage());
        }

        return products;
    }

    @Override
    public boolean putData(Object object) {
        if (object instanceof Profile) {
            Profile prof = (Profile) object;
            try {
                addData.setString(1, prof.getName());
                addData.setInt(2, prof.getProducts().ordinal());
                addData.setInt(3, prof.getFileMenu().ordinal());
                addData.setInt(4, prof.getCertificates().ordinal());
                addData.setInt(5, prof.getFamilies().ordinal());
                addData.setInt(6, prof.getOrderAccessible().ordinal());
                addData.setInt(7, prof.getUsers().ordinal());

                MainWindow.setProgress(1.0);

                if (addData.executeUpdate() > 0) {//successful
                    ResultSet rs = addData.getGeneratedKeys();

                    if (rs.next()) {
                        prof.setId(rs.getInt(1));
                        MainWindow.setProgress(0.0);
                        return true;
                    }
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка работы с БД");
                }

            } catch (SQLException e) {
                System.out.println("exception of writing to BD" + e.getMessage());
            }

            MainWindow.setProgress(0.0);
        }

        return false;
    }

    @Override
    public boolean updateData(Object object) {
        if (object instanceof Profile) {
            Profile prof = (Profile) object;

            try {
                updateData.setString(1, prof.getName());
                updateData.setInt(2, prof.getProducts().ordinal());
                updateData.setInt(3, prof.getFileMenu().ordinal());
                updateData.setInt(4, prof.getCertificates().ordinal());
                updateData.setInt(5, prof.getFamilies().ordinal());
                updateData.setInt(6, prof.getOrderAccessible().ordinal());
                updateData.setInt(7, prof.getUsers().ordinal());
                updateData.setInt(8, prof.getId());

                MainWindow.setProgress(1.0);

                if (updateData.executeUpdate() > 0) {//successful
                    MainWindow.setProgress(0.0);
                    return true;
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка работы с БД");
                }

            } catch (SQLException e) {
                System.out.println("exception of updating in BD");
            }
            MainWindow.setProgress(0.0);
        }

        return false;
    }

    @Override
    public boolean deleteData(Object object) {
        if (object instanceof Profile) {
            Profile prof = (Profile) object;

            MainWindow.setProgress(1.0);

            try {

                deleteData.setInt(1, prof.getId());
                deleteData.addBatch();

                int results[] = deleteData.executeBatch();

                MainWindow.setProgress(0.0);

                for (int res : results) {
                    if (res == 0) return false;//error
                }
                return true;//successful

            } catch (SQLException e) {
                System.out.println("exception of removing product type(s) from DB");
            }
            MainWindow.setProgress(0.0);
        }

        return false;
    }
}
