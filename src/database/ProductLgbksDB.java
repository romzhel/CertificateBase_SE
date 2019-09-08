package database;

import ui_windows.main_window.MainWindow;
import ui_windows.options_window.product_lgbk.ProductLgbk;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ProductLgbksDB extends DbRequest {

    public ProductLgbksDB() {
        super();
        try {
            addData = connection.prepareStatement("INSERT INTO " +
                            "lgbk (lgbk, hierarchy, description_en, family_id, not_used, node_type, description_ru, norms_list) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = connection.prepareStatement("UPDATE lgbk " +
                    "SET lgbk = ?, hierarchy = ?, description_en = ?, family_id = ?, not_used = ?, node_type = ?," +
                    "description_ru = ?, norms_list = ? WHERE id = ?");
            deleteData = connection.prepareStatement("DELETE FROM lgbk " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            logAndMessage("product lgbk prepared statements exception: " + e.getMessage());
            finalActions();
        }
    }

    public ArrayList getData() {
        ArrayList<ProductLgbk> productLgbks = new ArrayList<>();
        try {
            ResultSet rs = connection.prepareStatement("SELECT * FROM lgbk").executeQuery();

            while (rs.next()) {
                productLgbks.add(new ProductLgbk(rs));
            }
        } catch (SQLException e) {
            logAndMessage("SQL exception product lgbk get data");
        }
        return productLgbks;
    }

    public boolean putData(ProductLgbk pl) {
        int index = 1;
        try {
            addData.setString(index++, pl.getLgbk());
            addData.setString(index++, pl.getHierarchy());
            addData.setString(index++, pl.getDescription_en());
            addData.setInt(index++, pl.getFamilyId());
            addData.setBoolean(index++, pl.isNotUsed());
            addData.setInt(index++, pl.getNodeType());
            addData.setString(index++, pl.getDescription_ru());
            addData.setString(index++, pl.getNormsList().getStringLine());

            MainWindow.setProgress(1.0);

            if (addData.executeUpdate() > 0) {//successful
                ResultSet rs = addData.getGeneratedKeys();

                if (rs.next()) {
                    pl.setId(rs.getInt(1));
//                        System.out.println("new product lgbk ID = " + rs.getInt(1));
                    return true;
                }
            } else {
                logAndMessage("Ошибка добавления lgbk в БД");
            }
        } catch (SQLException e) {
            logAndMessage("exception of writing to BD (lgbk)");
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean updateData(ProductLgbk pl) {
        int index = 1;
        try {
            updateData.setString(index++, pl.getLgbk());
            updateData.setString(index++, pl.getHierarchy());
            updateData.setString(index++, pl.getDescription_en());
            updateData.setInt(index++, pl.getFamilyId());
            updateData.setBoolean(index++, pl.isNotUsed());
            updateData.setInt(index++, pl.getNodeType());
            updateData.setString(index++, pl.getDescription_ru());
            updateData.setString(index++, pl.getNormsList().getStringLine());

            updateData.setInt(index++, pl.getId());

            MainWindow.setProgress(1.0);

            int result = updateData.executeUpdate();

            if (result > 0) {//successful
                return true;
            } else {
                logAndMessage("Ошибка обновления LGBK в БД");
            }
        } catch (SQLException e) {
            logAndMessage("exception of updating lgbk in BD " + e.getMessage());
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean deleteData(ProductLgbk pl) {
        try {
            deleteData.setInt(1, pl.getId());
            deleteData.addBatch();

            int results[] = deleteData.executeBatch();

            for (int res : results) {
                if (res > 0) {
                    return true;//
                }
            }
        } catch (SQLException e) {
            logAndMessage("exception of removing lgbk from DB");
        } finally {
            finalActions();
        }
        return false;
    }
}
