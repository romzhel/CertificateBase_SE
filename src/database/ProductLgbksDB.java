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
                    "lgbk (lgbk, hierarchy, description, family_id, not_used) VALUES (?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = CoreModule.getDataBase().getDbConnection().prepareStatement("UPDATE lgbk " +
                    "SET lgbk = ?, hierarchy = ?, description = ?, family_id = ?, not_used = ? WHERE id = ?");
            deleteData = CoreModule.getDataBase().getDbConnection().prepareStatement("DELETE FROM lgbk " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            System.out.println("product lgbk prepared statements exception");
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
            try {
                addData.setString(1, pl.getLgbk());
                addData.setString(2, pl.getHierarchy());
                addData.setString(3, pl.getDescription());
                addData.setInt(4, pl.getFamilyId());
                addData.setBoolean(5, pl.isNotUsed());

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

            try {
                updateData.setString(1, pl.getLgbk());
                updateData.setString(2, pl.getHierarchy());
                updateData.setString(3, pl.getDescription());
                updateData.setInt(4, pl.getFamilyId());
                updateData.setBoolean(5, pl.isNotUsed());
                updateData.setInt(6, pl.getId());

                MainWindow.setProgress(1.0);

                if (updateData.executeUpdate() > 0) {//successful
                    MainWindow.setProgress(0.0);
                    return true;
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка работы с БД");
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
