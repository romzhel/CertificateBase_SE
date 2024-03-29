package database;

import ui_windows.options_window.product_lgbk.ProductLgbk;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
            logAndMessage("product lgbk prepared statements exception: ", e);
            finalActions();
        }
    }

    public List<ProductLgbk> getData() {
        List<ProductLgbk> productLgbks = new ArrayList<>();
        try {
            ResultSet rs = connection.prepareStatement("SELECT * FROM lgbk").executeQuery();

            while (rs.next()) {
                productLgbks.add(new ProductLgbk(rs));
            }

            rs.close();
        } catch (SQLException e) {
            logAndMessage("SQL exception product lgbk get data", e);
        }
        return productLgbks;
    }

    public boolean putData(Collection<ProductLgbk> pls) {
        try {

            for (ProductLgbk pl : pls) {
                int index = 1;
                addData.setString(index++, pl.getLgbk());
                addData.setString(index++, pl.getHierarchy());
                addData.setString(index++, pl.getDescription_en());
                addData.setInt(index++, pl.getFamilyId());
                addData.setBoolean(index++, pl.isNotUsed());
                addData.setInt(index++, pl.getNodeType());
                addData.setString(index++, pl.getDescription_ru());
                addData.setString(index++, pl.getNormsList().getStringLine());
                addData.addBatch();
            }

            connection.setAutoCommit(false);
            int[] result = addData.executeBatch();
            connection.commit();

            ResultSet keys = addData.getGeneratedKeys();
            if (keys.next()) {
                int firstId = keys.getInt(1) - pls.size() + 1;
                int index = 0;
                for (ProductLgbk lgbk : pls) {
                    if (result[index++] == 1) {
                        lgbk.setId(firstId++);
                    } else {
                        logAndMessage("", new RuntimeException("Данные LGBK/Hierarchy не были добавлены в БД"));
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            logAndMessage("", new RuntimeException("exception of writing to BD (lgbk)"));
        } finally {
            finalActions();
        }
        return true;
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

            int result = updateData.executeUpdate();

            if (result > 0) {//successful
                return true;
            } else {
                logAndMessage("", new RuntimeException("Ошибка обновления LGBK в БД"));
            }
        } catch (SQLException e) {
            logAndMessage("exception of updating lgbk in BD ", e);
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
            logAndMessage("exception of removing lgbk from DB", e);
        } finally {
            finalActions();
        }
        return false;
    }
}
