package database;

import core.CoreModule;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public boolean putData(List<CertificateContent> contents) {
        try {
            for (CertificateContent content : contents) {
                addData.setInt(1, content.getCertId());
                addData.setInt(2, CoreModule.getProductTypes().getID(content));
                addData.setString(3, content.getEquipmentName());
                addData.addBatch();
                System.out.printf("content DB batch %s", Utils.printTime());
            }

            connection.setAutoCommit(false);
            int[] result = addData.executeBatch();
            connection.commit();

            MainWindow.setProgress(1.0);
            for (int res : result) {
                if (res != 1) {
                    logAndMessage("Данный контент не был добавлен в БД");
                    return false;
                }
            }

//            if (addData.executeUpdate() > 0) {//successful
            if (result.length == contents.size()) {//successful
                ResultSet rs = addData.getGeneratedKeys();

                if (rs.next()) {
                    int index = rs.getInt(1) - contents.size();
                    for (CertificateContent cc : contents) {
                        cc.setId(++index);
                    }
                }

                rs.close();
            } else {
                logAndMessage("CertificateContent DB insert error ");
                return false;
            }
        } catch (SQLException e) {
            logAndMessage("exception of writing content to BD: " + e.getMessage() + "\n" + e.getStackTrace());
        } finally {
            finalActions();
        }
        return true;
    }

    public boolean updateData(List<CertificateContent> contents) {
        try {
            for (CertificateContent content : contents) {
                updateData.setInt(1, CoreModule.getProductTypes().getID(content));
                updateData.setString(2, content.getEquipmentName());
                updateData.setInt(3, content.getId());
                updateData.addBatch();
            }

            connection.setAutoCommit(false);
            int[] result = updateData.executeBatch();
            connection.commit();

            MainWindow.setProgress(1.0);

            for (int res : result) {
                if (res != 1) {
                    logAndMessage("CertificateContent DB update error");
                    return false;
                }
            }
        } catch (SQLException e) {
            logAndMessage("exception of updating content in BD: " + e.getMessage());
        } finally {
            finalActions();
        }
        return true;
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
