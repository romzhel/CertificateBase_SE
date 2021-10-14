package database;

import database.mappers.DbToProductMapper;
import ui_windows.ExecutionIndicator;
import ui_windows.product.Product;
import utils.property_change_protect.ChangeProtectService;

import java.sql.PreparedStatement;
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
                            "country, dchain, description_ru, description_en, price, not_used, archive, history, " +
                            "last_change_date, file_name, comments, replacement, type_id, change_codes, product_print," +
                            "last_import_codes, norms_list, norms_mode, min_order, packet_size, lead_time, weight, " +
                            "local_price, warranty, comments_price, protected_fields, vendor) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = connection.prepareStatement("UPDATE products " +
                    "SET article = ?, hierarchy = ?, lgbk = ?, family = ?, end_of_service = ?, dangerous = ?, country = ?, " +
                    "dchain = ?, description_ru = ?, description_en = ?, price = ?, not_used = ?, archive = ?, history = ?," +
                    "last_change_date = ?, file_name = ?, comments = ?, replacement = ?, type_id = ?, change_codes = ?, " +
                    "product_print = ?, last_import_codes = ?, norms_list = ?, norms_mode = ?, min_order = ?, packet_size = ?, " +
                    "lead_time = ?, weight = ?, local_price = ?, warranty = ?, comments_price = ?, protected_fields = ? " +
                    "WHERE material = ? AND vendor = ?");
            deleteData = connection.prepareStatement("DELETE FROM products " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            logAndMessage("products prepared statements exception: ", e);
            finalActions();
        }
    }

    public List<Product> getData() throws Exception {
        List<Product> products = new ArrayList<>();

        ResultSet rs = connection.prepareStatement("SELECT * FROM products").executeQuery();
        DbToProductMapper mapper = new DbToProductMapper();

        while (rs.next()) {
            products.add(mapper.mapToProduct(rs));
        }

        rs.close();

        return products;
    }

    public boolean updateData(List<Product> alpr) {
        try {
            int count = 0;
            for (int i = 0; i < alpr.size(); i = i + 500) {

                int j;
                for (j = i; j < (i + 500) && (j < alpr.size()); j++) {
                    count = 0;
                    count = setData(alpr, count, j, updateData);

                    updateData.setString(++count, alpr.get(j).getMaterial());
                    updateData.setInt(++count, alpr.get(j).getVendor().getId());
                    updateData.addBatch();
                }
                ExecutionIndicator.getInstance().setProgress((double) j / (double) alpr.size());

                connection.setAutoCommit(false);
                int[] result = updateData.executeBatch();
                connection.commit();

                int index = 0;
                for (int res : result) {
                    if (res != 1) {
                        String message = "Product update error";
                        logAndMessage(String.format("%s '%s' vendor '%s',\nchanged records: %d",
                                message, alpr.get(index), alpr.get(index).getVendor().toString(), res), new RuntimeException(message));
                        return false;
                    }
                    index++;
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
                    count = setData(alpr, count, j, addData);
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

    public int setData(List<Product> alpr, int count, int j, PreparedStatement prepStat) throws SQLException {
        prepStat.setString(++count, alpr.get(j).getArticle());
        prepStat.setString(++count, alpr.get(j).getHierarchy());
        prepStat.setString(++count, alpr.get(j).getLgbk());
        prepStat.setInt(++count, alpr.get(j).getFamily_id());
        prepStat.setString(++count, alpr.get(j).getEndofservice());
        prepStat.setString(++count, alpr.get(j).getDangerous());
        prepStat.setString(++count, alpr.get(j).getCountry());
        prepStat.setString(++count, alpr.get(j).getDchain());
        prepStat.setString(++count, alpr.get(j).getDescriptionru());
        prepStat.setString(++count, alpr.get(j).getDescriptionen());
        prepStat.setBoolean(++count, alpr.get(j).getPrice());
        prepStat.setBoolean(++count, alpr.get(j).getBlocked());
        prepStat.setBoolean(++count, alpr.get(j).getPriceHidden());
        prepStat.setString(++count, alpr.get(j).getHistory());
        prepStat.setString(++count, alpr.get(j).getLastChangeDate());
        prepStat.setString(++count, alpr.get(j).getFileName());
        prepStat.setString(++count, alpr.get(j).getComments());
        prepStat.setString(++count, alpr.get(j).getReplacement());
        prepStat.setInt(++count, alpr.get(j).getType_id());
        prepStat.setString(++count, alpr.get(j).getChangecodes());
        prepStat.setString(++count, alpr.get(j).getProductForPrint());
        prepStat.setString(++count, alpr.get(j).getLastImportcodes());
        prepStat.setString(++count, alpr.get(j).getNormsList().getStringLine());
        prepStat.setInt(++count, alpr.get(j).getNormsMode());
        prepStat.setInt(++count, alpr.get(j).getMinOrder());
        prepStat.setInt(++count, alpr.get(j).getPacketSize());
        prepStat.setInt(++count, alpr.get(j).getLeadTime());
        prepStat.setDouble(++count, alpr.get(j).getWeight());
        prepStat.setDouble(++count, alpr.get(j).getLocalPrice());
        prepStat.setInt(++count, alpr.get(j).getWarranty());
        prepStat.setString(++count, alpr.get(j).getCommentsPrice().isEmpty() ? null : alpr.get(j).getCommentsPrice());
        ChangeProtectService protectService = new ChangeProtectService();
        prepStat.setString(++count, protectService.mapSetToString(alpr.get(j).getProtectedData()));

        return count;
    }

    public boolean deleteData(Object object) {
        return false;
    }
}
