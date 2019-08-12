package database;

import core.CoreModule;
import core.Dialogs;
import ui_windows.options_window.certificates_editor.Certificate;
import ui_windows.main_window.MainWindow;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CertificatesDB implements Request {
    PreparedStatement addData, updateData, deleteData;

    public CertificatesDB(){
        try {
            addData = CoreModule.getDataBase().getDbConnection().prepareStatement("INSERT INTO " +
                            "certificates (name, expiration_date, countries, file_name, norms, name_match, " +
                            "material_match, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = CoreModule.getDataBase().getDbConnection().prepareStatement("UPDATE certificates " +
                    "SET name = ?, expiration_date = ?, countries = ?, file_name = ?, norms = ?, name_match = ?," +
                    "material_match = ?, user_id = ? WHERE id = ?");
            deleteData = CoreModule.getDataBase().getDbConnection().prepareStatement("DELETE FROM certificates " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            System.out.println("prepared statements exception " + e.getMessage());
        }
    }

    @Override
    public ArrayList<Certificate> getData() {
        ArrayList<Certificate> certificates = new ArrayList<>();
        try {
            ResultSet rs = CoreModule.getDataBase().getData("SELECT * FROM certificates");

            while (rs.next()) {
                certificates.add(new Certificate(rs));
            }

        } catch (SQLException e) {
            System.out.println("SQL exception cert types");
        }

        return certificates;
    }



    @Override
    public boolean putData(Object object) {
        if (object instanceof Certificate) {
            Certificate cert = (Certificate) object;
            try {
                addData.setString(1, cert.getName());
                addData.setString(2, cert.getExpirationDate());
                addData.setString(3, cert.getCountries());
                addData.setString(4, cert.getFileName());
                addData.setString(5, cert.getNorms());
                addData.setBoolean(6, cert.isFullNameMatch());
                addData.setBoolean(7, cert.isMaterialMatch());
                addData.setInt(8, cert.getUserId());

                MainWindow.setProgress(1.0);

                if (addData.executeUpdate() > 0) {//successful
                    ResultSet rs = addData.getGeneratedKeys();

                    MainWindow.setProgress(1.0);

                    if (rs.next()) {
                        cert.setId(rs.getInt(1));
                        System.out.println("new ID = " + rs.getInt(1));
                        return true;
                    }
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка работы с БД");
                }

            } catch (SQLException e) {
                System.out.println("exception of writing to BD");
            }

        }
        return false;
    }

    @Override
    public boolean updateData(Object object) {
        if (object instanceof Certificate) {
            Certificate cert = (Certificate) object;

            System.out.println("updating of " + cert.getName());
            try {
                updateData.setString(1, cert.getName());
                updateData.setString(2, cert.getExpirationDate());
                updateData.setString(3, cert.getCountries());
                updateData.setString(4, cert.getFileName());
                updateData.setString(5, cert.getNorms());
                updateData.setBoolean(6, cert.isFullNameMatch());
                updateData.setBoolean(7, cert.isMaterialMatch());
                updateData.setInt(8, cert.getUserId());
                updateData.setInt(9, cert.getId());

                MainWindow.setProgress(1.0);

                if (updateData.executeUpdate() > 0) {//successful
                    MainWindow.setProgress(0.0);
                    return true;
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка работы с БД");
                }

            } catch (SQLException e) {
                System.out.println("exception of writing to BD");
            }

            MainWindow.setProgress(0.0);

        }
        return false;
    }

    @Override
    public boolean deleteData(Object object) {
        if (object instanceof Certificate) {
            Certificate cert = (Certificate) object;

            try {
                deleteData.setInt(1, cert.getId());

                MainWindow.setProgress(1.0);

                if (deleteData.executeUpdate() > 0) {//successful
                    MainWindow.setProgress(0.0);
                    return true;
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка работы с БД");
                }

            } catch (SQLException e) {
                System.out.println("exception of writing to BD");
            }
        }
        MainWindow.setProgress(0.0);
        return false;
    }
}
