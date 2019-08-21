package database;

import core.CoreModule;
import core.Dialogs;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ProductLgbksDB implements Request {
    PreparedStatement addData, updateData, deleteData;

    public ProductLgbksDB() {
        try {
            addData = CoreModule.getDataBase().getDbConnection().prepareStatement("INSERT INTO " +
                    "lgbk (lgbk, hierarchy, description_en, family_id, not_used, node_type, description_ru, norms_list) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = CoreModule.getDataBase().getDbConnection().prepareStatement("UPDATE lgbk " +
                    "SET lgbk = ?, hierarchy = ?, description_en = ?, family_id = ?, not_used = ?, node_type = ?," +
                    "description_ru = ?, norms_list = ? WHERE id = ?");
            deleteData = CoreModule.getDataBase().getDbConnection().prepareStatement("DELETE FROM lgbk " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            System.out.println("product lgbk prepared statements exception: " + e.getMessage());
        }
    }

    @Override
    public ArrayList getData() {
        ArrayList<ProductLgbk> productLgbks = new ArrayList<>();
        try {
            ResultSet rs = CoreModule.getDataBase().getData("SELECT * FROM lgbk");

            while (rs.next()) {
                productLgbks.add(new ProductLgbk(rs));
            }

        } catch (SQLException e) {
            System.out.println("SQL exception product families get data");
        }

        return productLgbks;
    }

    @Override
    public boolean putData(Object object) {
        if (object instanceof ProductLgbk) {
            ProductLgbk pl = (ProductLgbk) object;
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

                    MainWindow.setProgress(0.0);

                    if (rs.next()) {
                        pl.setId(rs.getInt(1));
                        System.out.println("new product lgbk ID = " + rs.getInt(1));
                        return true;
                    }
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка добавления lgbk в БД");
                }

            } catch (SQLException e) {
                System.out.println("exception of writing to BD (lgbk)");
            }
        }
        MainWindow.setProgress(0.0);

        return false;
    }

    @Override
    public boolean updateData(Object object) {
        if (object instanceof ProductLgbk) {
            ProductLgbk pl = (ProductLgbk) object;

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
                    MainWindow.setProgress(0.0);
                    return true;
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка обновления LGBK в БД (ответ = " + result + ")\n " + pl.toString());
                }

            } catch (SQLException e) {
                System.out.println("exception of updating lgbk in BD");
            }

        }
        MainWindow.setProgress(0.0);

        return false;
    }

    @Override
    public boolean deleteData(Object object) {
        if (object instanceof ProductLgbk) {
            ProductLgbk pl = (ProductLgbk) object;
            try {

                deleteData.setInt(1, pl.getId());
                deleteData.addBatch();

                int results[] = deleteData.executeBatch();

                for (int res : results) {
                    if (res == 0) return false;//error
                }
                return true;//successful

            } catch (SQLException e) {
                System.out.println("exception of removing lgbk from DB");
            }
        }
        return false;
    }
}
