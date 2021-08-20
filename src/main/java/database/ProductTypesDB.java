package database;

import lombok.extern.log4j.Log4j2;
import ui_windows.product.ProductType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ProductTypesDB extends DbRequest {

    public ProductTypesDB() {
        super();
        try {
            addData = connection.prepareStatement("INSERT INTO " +
                            "productTypes (product_type, type_ten) VALUES (?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = connection.prepareStatement("UPDATE productTypes " +
                    "SET product_type = ?, type_ten = ? WHERE id = ?");
            deleteData = connection.prepareStatement("DELETE FROM productTypes " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            logAndMessage("product types prepared statements exception", e);
            finalActions();
        }
    }

    public List getData() {
        ArrayList<ProductType> productTypes = new ArrayList<>();
        try {
            ResultSet rs = connection.prepareStatement("SELECT * FROM productTypes").executeQuery();

            while (rs.next()) {
                productTypes.add(new ProductType(rs));
            }

            rs.close();
        } catch (SQLException e) {
            logAndMessage("SQL exception productTypes get data", e);
        }
        return productTypes;
    }

    public boolean putData(ProductType pt) {
        log.trace("put product type to DB: {}", pt);
        try {
            addData.setString(1, pt.getType());
            addData.setString(2, pt.getTen());

            if (addData.executeUpdate() > 0) {
                ResultSet rs = addData.getGeneratedKeys();

                if (rs.next()) {
                    pt.setId(rs.getInt(1));
                }

                return true;
            } else {
                logAndMessage("", new RuntimeException("Ошибка добавления типа оборудования в БД"));
            }
        } catch (SQLException e) {
            logAndMessage("exception of writing product type to B", e);
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean updateData(ProductType pt) {
        log.trace("update product type to DB: {}", pt);
        try {
            updateData.setString(1, pt.getType());
            updateData.setString(2, pt.getTen());

            updateData.setInt(3, pt.getId());

            if (updateData.executeUpdate() > 0) {//successful
                return true;
            } else {
                logAndMessage("", new RuntimeException("Ошибка обновления типа оборудования в БД"));
            }
        } catch (SQLException e) {
            logAndMessage("exception of updating product type to BD", e);
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean deleteData(ProductType pt) {
        log.trace("delete product type to DB: {}", pt);
        try {
            deleteData.setInt(1, pt.getId());
            deleteData.addBatch();

            int results[] = deleteData.executeBatch();

            for (int res : results) {
                if (res > 0) {
                    return true;//
                }
            }
        } catch (SQLException e) {
            logAndMessage("exception of removing product type(s) from DB", e);
        } finally {
            finalActions();
        }
        return false;
    }
}
