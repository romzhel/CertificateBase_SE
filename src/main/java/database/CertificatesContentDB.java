package database;

import lombok.extern.log4j.Log4j2;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
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
            logAndMessage("CertificatesContentDB prepared statements exception, ", e);
            finalActions();
        }
    }

    public List<CertificateContent> getData() {
        List<CertificateContent> content = new ArrayList<>();
        try {
            ResultSet rs = connection.prepareStatement("SELECT * FROM certificatesContent").executeQuery();

            while (rs.next()) {
                content.add(new CertificateContent(rs));
            }

            rs.close();

        } catch (SQLException e) {
            logAndMessage("SQL exception of getting cert content: ", e);
        }
        return content;

    }

    public boolean putData(Collection<CertificateContent> contents) {
        log.trace("put cert content to DB: {}", contents);
        try {
            for (CertificateContent content : contents) {
                addData.setInt(1, content.getCertId());
                addData.setInt(2, content.getProductType().getId());
                addData.setString(3, content.getEquipmentName());
                addData.addBatch();
            }

            connection.setAutoCommit(false);
            int[] result = addData.executeBatch();
            connection.commit();

            for (int res : result) {
                if (res != 1) {
                    logAndMessage("", new RuntimeException("Данный контент не был добавлен в БД"));
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
                logAndMessage("", new RuntimeException("CertificateContent DB insert error"));
                return false;
            }
        } catch (SQLException e) {
            logAndMessage("exception of writing content to BD: ", e);
        } finally {
            finalActions();
        }
        return true;
    }

    public boolean updateData(Collection<CertificateContent> contents) {
        log.trace("update cert content to DB: {}", contents);
        try {
            for (CertificateContent content : contents) {
                updateData.setInt(1, content.getProductType().getId());
                updateData.setString(2, content.getEquipmentName());
                updateData.setInt(3, content.getId());
                updateData.addBatch();
            }

            connection.setAutoCommit(false);
            int[] result = updateData.executeBatch();
            connection.commit();

            for (int res : result) {
                if (res != 1) {
                    logAndMessage("", new RuntimeException("CertificateContent DB update error"));
                    return false;
                }
            }
        } catch (SQLException e) {
            logAndMessage("exception of updating content in BD: ", e);
        } finally {
            finalActions();
        }
        return true;
    }

    public boolean deleteData(Collection<CertificateContent> content) {
        log.trace("delete cert content to DB: {}", content);
        try {
            for (CertificateContent cc : content) {
                deleteData.setInt(1, cc.getId());
                deleteData.addBatch();
            }

            int results[] = deleteData.executeBatch();

            for (int res : results) {
                if (res == 0) {
                    return false;//error
                }
            }
        } catch (SQLException e) {
            logAndMessage("exception of removing content from DB: ", e);
        } finally {
            finalActions();
        }
        return true;
    }
}
