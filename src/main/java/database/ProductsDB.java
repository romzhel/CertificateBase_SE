package database;

import ui_windows.ExecutionIndicator;
import ui_windows.product.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductsDB extends DbRequest {

    public ProductsDB() {
        super();
        try {
            addData = connection.prepareStatement("INSERT INTO " +
                            "products (material, article, hierarchy, lgbk, family, end_of_service, dangerous, " +
                            "country, dchain, description_ru, description_en, price, history, " +
                            "last_change_date, file_name, comments, replacement, type_id, change_codes, product_print," +
                            "last_import_codes, norms_list, norms_mode, min_order, packet_size, lead_time, weight, local_price) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = connection.prepareStatement("UPDATE products " +
                    "SET article = ?, hierarchy = ?, lgbk = ?, family = ?, end_of_service = ?, dangerous = ?, country = ?, " +
                    "dchain = ?, description_ru = ?, description_en = ?, price = ?, history = ?," +
                    "last_change_date = ?, file_name = ?, comments = ?, replacement = ?, type_id = ?, change_codes = ?, " +
                    "product_print = ?, last_import_codes = ?, norms_list = ?, norms_mode = ?, min_order = ?, packet_size = ?, " +
                    "lead_time = ?, weight = ?, local_price = ? WHERE material = ?");
            deleteData = connection.prepareStatement("DELETE FROM products " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            logAndMessage("products prepared statements exception: ", e);
            finalActions();
        }
    }

    public ArrayList getData() {
        ArrayList<Product> products = new ArrayList<>();
        try {
            ResultSet rs = connection.prepareStatement("SELECT * FROM products").executeQuery();

            while (rs.next()) {
                products.add(new Product(rs));
            }

            rs.close();
        } catch (SQLException e) {
            logAndMessage("SQL exception products, ", e);
        }
        return products;
    }

    public boolean updateData(List<Product> alpr) {
        try {
            int count = 0;
            for (int i = 0; i < alpr.size(); i = i + 500) {

                int j;
                for (j = i; j < (i + 500) && (j < alpr.size()); j++) {
                    count = 0;
                    updateData.setString(++count, alpr.get(j).getArticle());
                    updateData.setString(++count, alpr.get(j).getHierarchy());
                    updateData.setString(++count, alpr.get(j).getLgbk());
                    updateData.setInt(++count, alpr.get(j).getFamily_id());
                    updateData.setString(++count, alpr.get(j).getEndofservice());
                    updateData.setString(++count, alpr.get(j).getDangerous());
                    updateData.setString(++count, alpr.get(j).getCountry());
                    updateData.setString(++count, alpr.get(j).getDchain());
                    updateData.setString(++count, alpr.get(j).getDescriptionru());
                    updateData.setString(++count, alpr.get(j).getDescriptionen());
                    updateData.setBoolean(++count, alpr.get(j).isPrice());
                    updateData.setString(++count, alpr.get(j).getHistory());
                    updateData.setString(++count, alpr.get(j).getLastChangeDate());
                    updateData.setString(++count, alpr.get(j).getFileName());
                    updateData.setString(++count, alpr.get(j).getComments());
                    updateData.setString(++count, alpr.get(j).getReplacement());
                    updateData.setInt(++count, alpr.get(j).getType_id());
                    updateData.setString(++count, alpr.get(j).getChangecodes());
                    updateData.setString(++count, alpr.get(j).getProductForPrint());
                    updateData.setString(++count, alpr.get(j).getLastImportcodes());
                    updateData.setString(++count, alpr.get(j).getNormsList().getStringLine());
                    updateData.setInt(++count, alpr.get(j).getNormsMode());
                    updateData.setInt(++count, alpr.get(j).getMinOrder());
                    updateData.setInt(++count, alpr.get(j).getPacketSize());
                    updateData.setInt(++count, alpr.get(j).getLeadTime());
                    updateData.setDouble(++count, alpr.get(j).getWeight());
                    updateData.setDouble(++count, alpr.get(j).getLocalPrice());

                    updateData.setString(++count, alpr.get(j).getMaterial());
                    updateData.addBatch();
                }
                ExecutionIndicator.getInstance().setProgress((double) j / (double) alpr.size());

                connection.setAutoCommit(false);
                int[] result = updateData.executeBatch();
                connection.commit();

                for (int res : result) {
                    if (res != 1) {
                        logAndMessage("", new RuntimeException("Данные продукты не были обновлены в БД"));
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            logAndMessage("", new RuntimeException("Ошибка обновления продуктов в БД"));
            try {
                connection.rollback();
            } catch (SQLException ex) {
                logAndMessage("Ошибка отката неудачного обновления продуктов в БД", e);
            }
            return false;
        } finally {
            finalActions();
        }

        return true;
    }

    public boolean putData(List<Product> alpr) {
        int j = 0;
        try {
            int count = 0;
            for (int i = 0; i < alpr.size(); i = i + 500) {

                for (j = i; j < (i + 500) && (j < alpr.size()); j++) {
                    count = 0;
                    addData.setString(++count, alpr.get(j).getMaterial());
                    addData.setString(++count, alpr.get(j).getArticle());
                    addData.setString(++count, alpr.get(j).getHierarchy());
                    addData.setString(++count, alpr.get(j).getLgbk());
                    addData.setInt(++count, alpr.get(j).getFamily_id());
                    addData.setString(++count, alpr.get(j).getEndofservice());
                    addData.setString(++count, alpr.get(j).getDangerous());
                    addData.setString(++count, alpr.get(j).getCountry());
                    addData.setString(++count, alpr.get(j).getDchain());
                    addData.setString(++count, alpr.get(j).getDescriptionru());
                    addData.setString(++count, alpr.get(j).getDescriptionen());
                    addData.setBoolean(++count, alpr.get(j).isPrice());
                    addData.setString(++count, alpr.get(j).getHistory());
                    addData.setString(++count, alpr.get(j).getLastChangeDate());
                    addData.setString(++count, alpr.get(j).getFileName());
                    addData.setString(++count, alpr.get(j).getComments());
                    addData.setString(++count, alpr.get(j).getReplacement());
                    addData.setInt(++count, alpr.get(j).getType_id());
                    addData.setString(++count, alpr.get(j).getChangecodes());
                    addData.setString(++count, alpr.get(j).getProductForPrint());
                    addData.setString(++count, alpr.get(j).getLastImportcodes());
                    addData.setString(++count, alpr.get(j).getNormsList().getStringLine());
                    addData.setInt(++count, alpr.get(j).getNormsMode());
                    addData.setInt(++count, alpr.get(j).getMinOrder());
                    addData.setInt(++count, alpr.get(j).getPacketSize());
                    addData.setInt(++count, alpr.get(j).getLeadTime());
                    addData.setDouble(++count, alpr.get(j).getWeight());
                    addData.setDouble(++count, alpr.get(j).getLocalPrice());
                    addData.addBatch();
                }

                ExecutionIndicator.getInstance().setProgress((double) j / (double) alpr.size());
                connection.setAutoCommit(false);
                int[] result = addData.executeBatch();
                connection.commit();

                for (int res : result) {
                    if (res != 1) {
                        logAndMessage("", new RuntimeException("Данные продукты не были добавлены в БД"));
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            logAndMessage("exception of putting to product BD, ", e);
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println(e.getMessage());
            }
            return false;
        } finally {
            finalActions();
        }
        return true;
    }

    public boolean deleteData(Object object) {
        return false;
    }
}
