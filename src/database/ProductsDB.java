package database;

import core.CoreModule;
import core.Dialogs;
import javafx.application.Platform;
import ui_windows.main_window.MainWindow;
import ui_windows.main_window.Product;

import java.sql.*;
import java.util.ArrayList;

public class ProductsDB implements Request {
    PreparedStatement addData, updateData, deleteData;

    public ProductsDB() {
        try {
            addData = CoreModule.getDataBase().getDbConnection().prepareStatement("INSERT INTO " +
                            "products (material, article, hierarchy, lgbk, family, end_of_service, dangerous, " +
                            "country, dchain, description_ru, description_en, price, archive, need_action, not_used, history, " +
                            "last_change_date, file_name, comments, replacement, type_id, change_codes, product_print," +
                            "last_import_codes) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = CoreModule.getDataBase().getDbConnection().prepareStatement("UPDATE products " +
                    "SET article = ?, hierarchy = ?, lgbk = ?, family = ?, end_of_service = ?, dangerous = ?, country = ?, " +
                    "dchain = ?, description_ru = ?, description_en = ?, price = ?, archive = ?, need_action = ?, not_used = ?, history = ?," +
                    "last_change_date = ?, file_name = ?, comments = ?, replacement = ?, type_id = ?, change_codes = ?, " +
                    "product_print = ?, last_import_codes = ? WHERE material = ?");
            deleteData = CoreModule.getDataBase().getDbConnection().prepareStatement("DELETE FROM products " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            System.out.println("products prepared statements exception: " + e.getMessage());
        }
    }

    @Override
    public ArrayList getData() {
        ArrayList<Product> products = new ArrayList<>();
        try {
            ResultSet rs = CoreModule.getDataBase().getData("SELECT * FROM products");

            while (rs.next()) {
                products.add(new Product(rs));
            }

        } catch (SQLException e) {
            System.out.println("SQL exception products, " + e.getMessage());
        }

        return products;
    }

    @Override
    public boolean updateData(Object object) {
        if (object instanceof ArrayList) {
            ArrayList<Product> alpr = (ArrayList<Product>) object;

            MainWindow.setProgress(0.01);
            try {
                int count = 0;
                for (int i = 0; i < alpr.size(); i = i + 500) {

                    int j;
                    for (j = i; j < (i + 500) && (j < alpr.size()); j++) {
                        count = 0;
                        updateData.setString(++count, alpr.get(j).getArticle());
                        updateData.setString(++count, alpr.get(j).getHierarchy());
                        updateData.setString(++count, alpr.get(j).getLgbk());
                        updateData.setInt(++count, alpr.get(j).getFamily());
                        updateData.setString(++count, alpr.get(j).getEndofservice());
                        updateData.setString(++count, alpr.get(j).getDangerous());
                        updateData.setString(++count, alpr.get(j).getCountry());
                        updateData.setString(++count, alpr.get(j).getDchain());
                        updateData.setString(++count, alpr.get(j).getDescriptionru());
                        updateData.setString(++count, alpr.get(j).getDescriptionen());
                        updateData.setBoolean(++count, alpr.get(j).isPrice());
                        updateData.setBoolean(++count, alpr.get(j).isArchive());
                        updateData.setBoolean(++count, alpr.get(j).isNeedaction());
                        updateData.setBoolean(++count, alpr.get(i).isNotused());
                        updateData.setString(++count, alpr.get(j).getHistory());
                        updateData.setString(++count, alpr.get(j).getLastChangeDate());
                        updateData.setString(++count, alpr.get(j).getFileName());
                        updateData.setString(++count, alpr.get(j).getComments());
                        updateData.setString(++count, alpr.get(j).getReplacement());
                        updateData.setInt(++count, alpr.get(j).getType_id());
                        updateData.setString(++count, alpr.get(j).getChangecodes());
                        updateData.setString(++count, alpr.get(j).getProductForPrint());
                        updateData.setString(++count, alpr.get(j).getLastImportcodes());
                        updateData.setString(++count, alpr.get(j).getMaterial());

                        updateData.addBatch();
                    }
                    MainWindow.setProgress((double) j / (double) alpr.size());
                    int[] result = updateData.executeBatch();

                    for (int res : result) {
                        if (res != 1) {
                            Platform.runLater(() -> Dialogs.showMessage("Запись данных в БД", "Данные не были обновлены в БД, " +
                                    "ответ: " + res));
                            return false;
                        }
                    }
                }

                MainWindow.setProgress(0.0);
                return true;

            } catch (SQLException e) {
                System.out.println("exception of updating to product BD, " + e.getMessage());
            }

        }
        return false;
    }

    @Override
    public boolean putData(Object object) {
        if (object instanceof ArrayList) {
            ArrayList<Product> alpr = (ArrayList<Product>) object;

            MainWindow.setProgress(0.01);

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
                        addData.setInt(++count, alpr.get(j).getFamily());
                        addData.setString(++count, alpr.get(j).getEndofservice());
                        addData.setString(++count, alpr.get(j).getDangerous());
                        addData.setString(++count, alpr.get(j).getCountry());
                        addData.setString(++count, alpr.get(j).getDchain());
                        addData.setString(++count, alpr.get(j).getDescriptionru());
                        addData.setString(++count, alpr.get(j).getDescriptionen());
                        addData.setBoolean(++count, alpr.get(j).isPrice());
                        addData.setBoolean(++count, alpr.get(j).isArchive());
                        addData.setBoolean(++count, alpr.get(j).isNeedaction());
                        addData.setBoolean(++count, alpr.get(j).isNotused());
                        addData.setString(++count, alpr.get(j).getHistory());
                        addData.setString(++count, alpr.get(j).getLastChangeDate());
                        addData.setString(++count, alpr.get(j).getFileName());
                        addData.setString(++count, alpr.get(j).getComments());
                        addData.setString(++count, alpr.get(j).getReplacement());
                        addData.setInt(++count, alpr.get(j).getType_id());
                        addData.setString(++count, alpr.get(j).getChangecodes());
                        addData.setString(++count, alpr.get(j).getProductForPrint());
                        addData.setString(++count, alpr.get(j).getLastImportcodes());
                        addData.addBatch();
                    }

                    MainWindow.setProgress((double) j / (double) alpr.size());

                    int[] result = addData.executeBatch();
                    for (int res : result) {
                        if (res != 1) {
                            Dialogs.showMessage("Запись данных в БД", "Данные не были добавлены в БД");
                            return false;
                        }
                    }
                }

                MainWindow.setProgress(0.0);
                return true;

            } catch (SQLException e) {
                System.out.println("exception of adding to product BD, " + e.getMessage() + ", " + alpr.get(--j).toString());
            }

        }
        return false;
    }

    @Override
    public boolean deleteData(Object object) {
        return false;
    }
}
