package database;

import core.CoreModule;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CertificatesContentDB extends DbRequest {

    public CertificatesContentDB() {
        super();
        try {
            addData = connection.prepareStatement("INSERT INTO " +
                            "certificatesContent (cert_id, product_type_id, product_names) VALUES (?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = connection.prepareStatement("UPDATE certificatesContent " +
                    "SET product_type_id = ?, product_names = ? WHERE id = ?");
            deleteData = connection.prepareStatement("DELETE FROM certificatesContent " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            logAndMessage("CertificatesContentDB prepared statements exception, " + e.getMessage());
            finalActions();
        }
    }

    public ArrayList<CertificateContent> getData() {
        ArrayList<CertificateContent> content = new ArrayList<>();
        try {
            ResultSet rs = connection.prepareStatement("SELECT * FROM certificatesContent").executeQuery();

            while (rs.next()) {
                content.add(new CertificateContent(rs));
            }

            rs.close();

        } catch (SQLException e) {
            logAndMessage("SQL exception of getting cert content: " + e.getMessage());
        }
        return content;

    }

    public boolean putData(CertificateContent content) {
        try {
            addData.setInt(1, content.getCertId());
            addData.setInt(2, CoreModule.getProductTypes().getID(content));
            addData.setString(3, content.getEquipmentName());

            MainWindow.setProgress(1.0);

            if (addData.executeUpdate() > 0) {//successful
                ResultSet rs = addData.getGeneratedKeys();

                if (rs.next()) {
                    content.setId(rs.getInt(1));
                    return true;
                }
            } else {
                logAndMessage("CertificateContent DB insert error ");
            }

        } catch (SQLException e) {
            logAndMessage("exception of writing content to BD: " + e.getMessage() + "\n" +
                    "Additional info: certID = " + content.getCertId() + ", prodTypeId = " + CoreModule.getProductTypes().getID(content) +
                    ", equipEnums = " + content.getEquipmentName() + "\n" + e.getSQLState() + "\n" + e.getStackTrace());
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean updateData(CertificateContent content) {
        try {
            updateData.setInt(1, CoreModule.getProductTypes().getID(content));
            updateData.setString(2, content.getEquipmentName());
            updateData.setInt(3, content.getId());

            MainWindow.setProgress(1.0);

            if (updateData.executeUpdate() > 0) {//successful
                return true;
            } else {
                logAndMessage("CertificateContent DB update error");
            }
        } catch (SQLException e) {
            logAndMessage("exception of updating content in BD: " + e.getMessage());
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean deleteData(ArrayList<CertificateContent> content) {
        try {
            for (CertificateContent cc : content) {
                deleteData.setInt(1, cc.getId());
                deleteData.addBatch();
            }

            MainWindow.setProgress(1.0);

            int results[] = deleteData.executeBatch();

            for (int res : results) {
                if (res == 0) {
                    return false;//error
                }
            }
        } catch (SQLException e) {
            logAndMessage("exception of removing content from DB: " + e.getMessage());
        } finally {
            finalActions();
        }
        return true;
    }
}
