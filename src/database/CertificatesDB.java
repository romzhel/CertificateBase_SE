package database;

import ui_windows.options_window.certificates_editor.Certificate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CertificatesDB extends DbRequest {

    public CertificatesDB() {
        super();
        try {
            addData = connection.prepareStatement("INSERT INTO " +
                            "certificates (name, expiration_date, countries, file_name, norms, name_match, " +
                            "material_match, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = connection.prepareStatement("UPDATE certificates " +
                    "SET name = ?, expiration_date = ?, countries = ?, file_name = ?, norms = ?, name_match = ?," +
                    "material_match = ?, user_id = ? WHERE id = ?");
            deleteData = connection.prepareStatement("DELETE FROM certificates " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            logAndMessage("prepared statements exception: " + e.getMessage());
            finalActions();
        }
    }

    public ArrayList<Certificate> getData() {
        ArrayList<Certificate> certificates = new ArrayList<>();
        try {
            ResultSet rs = connection.prepareStatement("SELECT * FROM certificates").executeQuery();

            while (rs.next()) {
                certificates.add(new Certificate(rs));
            }

            rs.close();
        } catch (SQLException e) {
            logAndMessage("SQL exception cert types: " + e.getMessage());
        }
        return certificates;
    }

    public boolean putData(Certificate cert) {
        try {
            addData.setString(1, cert.getName());
            addData.setString(2, cert.getExpirationDate());
            addData.setString(3, cert.getCountries());
            addData.setString(4, cert.getFileName());
            addData.setString(5, cert.getNorms());
            addData.setBoolean(6, cert.isFullNameMatch());
            addData.setBoolean(7, cert.isMaterialMatch());
            addData.setInt(8, cert.getUserId());

            if (addData.executeUpdate() > 0) {//successful
                ResultSet rs = addData.getGeneratedKeys();

                if (rs.next()) {
                    cert.setId(rs.getInt(1));
//                        System.out.println("new ID = " + rs.getInt(1));
                    return true;
                }
            } else {
                logAndMessage("Certificate DB insert error");
            }

        } catch (SQLException e) {
            logAndMessage("Certificate BD inserting error " + e.getMessage());
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean updateData(Certificate cert) {
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

            if (updateData.executeUpdate() > 0) {//successful
                return true;
            } else {
                logAndMessage("Certificate BD updating error ");
            }

        } catch (SQLException e) {
            logAndMessage("Certificate BD updating error " + e.getMessage());
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean deleteData(Certificate cert) {
        try {
            deleteData.setInt(1, cert.getId());

            if (deleteData.executeUpdate() > 0) {//successful
                return true;
            } else {
                logAndMessage("Certificate BD deleting error");
            }
        } catch (SQLException e) {
            logAndMessage("Certificate BD deleting error " + e.getMessage());
        } finally {
            finalActions();
        }
        return false;
    }
}
