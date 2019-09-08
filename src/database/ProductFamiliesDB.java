package database;

import ui_windows.main_window.MainWindow;
import ui_windows.options_window.families_editor.ProductFamily;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ProductFamiliesDB extends DbRequest {

    public ProductFamiliesDB() {
        super();
        try {
            addData = connection.prepareStatement("INSERT INTO " +
                    "productFamilies (name, responsible) VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS);
            updateData = connection.prepareStatement("UPDATE productFamilies " +
                    "SET name = ?, responsible = ? WHERE id = ?");
            deleteData = connection.prepareStatement("DELETE FROM productFamilies " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            logAndMessage("product families prepared statements exception");
            finalActions();
        }
    }

    public ArrayList getData() {
        ArrayList<ProductFamily> productFamilies = new ArrayList<>();
        try {
            ResultSet rs = connection.prepareStatement("SELECT * FROM productFamilies").executeQuery();

            while (rs.next()) {
                productFamilies.add(new ProductFamily(rs));
            }
        } catch (SQLException e) {
            logAndMessage("SQL exception product families get data");
        }
        return productFamilies;
    }

    public boolean putData(ProductFamily pf) {
        try {
            addData.setString(1, pf.getName());
            addData.setString(2, pf.getResponsible());

            MainWindow.setProgress(1.0);

            if (addData.executeUpdate() > 0) {//successful
                ResultSet rs = addData.getGeneratedKeys();

                if (rs.next()) {
                    pf.setId(rs.getInt(1));
                    return true;
                }
            } else {
                logAndMessage("exception of inserting to BD (family)");
            }
        } catch (SQLException e) {
            logAndMessage("exception of inserting to BD (family)");
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean updateData(ProductFamily pf) {
        try {
            updateData.setString(1, pf.getName());
            updateData.setString(2, pf.getResponsible());
            updateData.setInt(3, pf.getId());

            MainWindow.setProgress(1.0);

            if (updateData.executeUpdate() > 0) {//successful
                return true;
            } else {
                logAndMessage("exception of updating family in BD");
            }
        } catch (SQLException e) {
            logAndMessage("exception of updating family in BD");
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean deleteData(ProductFamily pf) {
        try {
            deleteData.setInt(1, pf.getId());
            deleteData.addBatch();

            MainWindow.setProgress(1.0);

            int results[] = deleteData.executeBatch();

            for (int res : results) {
                if (res > 0) {
                    return true;//
                }
            }
        } catch (SQLException e) {
            logAndMessage("exception of removing family from DB " + e.getMessage());
        } finally {
            finalActions();
        }
        return false;
    }
}
