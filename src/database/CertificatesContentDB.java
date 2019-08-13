package database;

import core.CoreModule;
import core.Dialogs;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import ui_windows.main_window.MainWindow;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CertificatesContentDB implements Request {
    PreparedStatement addData, updateData, deleteData;

    public CertificatesContentDB() {
        try {
            addData = CoreModule.getDataBase().getDbConnection().prepareStatement("INSERT INTO " +
                            "certificatesContent (cert_id, product_type_id, product_names) VALUES (?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = CoreModule.getDataBase().getDbConnection().prepareStatement("UPDATE certificatesContent " +
                    "SET product_type_id = ?, product_names = ? WHERE id = ?");
            deleteData = CoreModule.getDataBase().getDbConnection().prepareStatement("DELETE FROM certificatesContent " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            showErrorMessage(e.getMessage(), "CertificatesContentDB prepared statements exception, " + e.getMessage());
        }
    }

    @Override
    public ArrayList<CertificateContent> getData() {
        ArrayList<CertificateContent> content = new ArrayList<>();
        try {
            ResultSet rs = CoreModule.getDataBase().getData("SELECT * FROM certificatesContent");

            while (rs.next()) {
                content.add(new CertificateContent(rs));
            }

        } catch (SQLException e) {
            showErrorMessage(e.getMessage(), "SQL exception of getting cert content: " + e.getMessage() + "\n" + e.getStackTrace());
        }

        return content;
    }

    @Override
    public boolean putData(Object object) {
        if (object instanceof CertificateContent) {
            CertificateContent content = (CertificateContent) object;

            try {
                addData.setInt(1, content.getCertId());
                addData.setInt(2, CoreModule.getProductTypes().getID(content));
                addData.setString(3, content.getEquipmentName());

                MainWindow.setProgress(1.0);

                if (addData.executeUpdate() > 0) {//successful
                    ResultSet rs = addData.getGeneratedKeys();

                    MainWindow.setProgress(0.0);

                    if (rs.next()) {
                        content.setId(rs.getInt(1));
                        return true;
                    }
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка работы с БД");
                }

            } catch (SQLException e) {
                showErrorMessage(e.getMessage(),"exception of writing content to BD: " + e.getMessage() + "\n" + e.getStackTrace());
            }
        }

        return false;
    }

    @Override
    public boolean updateData(Object object) {
        if (object instanceof CertificateContent) {
            CertificateContent content = (CertificateContent) object;
            try {
                updateData.setInt(1, CoreModule.getProductTypes().getID(content));
                updateData.setString(2, content.getEquipmentName());
                updateData.setInt(3, content.getId());

                MainWindow.setProgress(1.0);

                if (updateData.executeUpdate() > 0) {//successful
                    MainWindow.setProgress(0.0);
                    return true;
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка работы с БД");
                }

            } catch (SQLException e) {
                showErrorMessage(e.getMessage(), "exception of updating content in BD: " + e.getMessage() + "\n" + e.getStackTrace());
            }

        }
        return false;
    }

    @Override
    public boolean deleteData(Object object) {
        if (object instanceof ArrayList) {
            ArrayList<CertificateContent> content = (ArrayList<CertificateContent>) object;

            try {
                for (CertificateContent cc : content) {
                    deleteData.setInt(1, cc.getId());
                    deleteData.addBatch();
                }

                MainWindow.setProgress(1.0);

                int results[] = deleteData.executeBatch();

                MainWindow.setProgress(0.0);

                for (int res : results) {
                    if (res == 0) return false;//error
                }
                return true;//successful

            } catch (SQLException e) {
                System.out.println();
                showErrorMessage(e.getMessage(), "exception of removing content from DB: " + e.getMessage() + "\n" + e.getStackTrace());
            }
        }
        return false;
    }

    public void showErrorMessage(String messageText, String logText){
        Dialogs.showMessage("Ошибка работы с базой данных", messageText);
        System.out.println(logText);
    }
}
