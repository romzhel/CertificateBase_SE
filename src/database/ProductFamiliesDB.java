package database;

import core.CoreModule;
import core.Dialogs;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.families_editor.ProductFamily;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ProductFamiliesDB implements Request {
    PreparedStatement addData, updateData, deleteData;

    public ProductFamiliesDB(){
        try {
            addData = CoreModule.getDataBase().getDbConnection().prepareStatement("INSERT INTO " +
                            "productFamilies (name, responsible) VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS);
            updateData = CoreModule.getDataBase().getDbConnection().prepareStatement("UPDATE productFamilies " +
                    "SET name = ?, responsible = ? WHERE id = ?");
            deleteData = CoreModule.getDataBase().getDbConnection().prepareStatement("DELETE FROM productFamilies " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            System.out.println("product families prepared statements exception");
        }
    }

    @Override
    public ArrayList getData() {
        ArrayList<ProductFamily> productFamilies = new ArrayList<>();
        try {
            ResultSet rs = CoreModule.getDataBase().getData("SELECT * FROM productFamilies");

            while (rs.next()) {
                productFamilies.add(new ProductFamily(rs));
            }

        } catch (SQLException e) {
            System.out.println("SQL exception product families get data");
        }

        return productFamilies;
    }

    @Override
    public boolean putData(Object object) {
        if (object instanceof ProductFamily) {
            ProductFamily pf = (ProductFamily) object;
            try {
                addData.setString(1, pf.getName());
                addData.setString(2, pf.getResponsible());

                MainWindow.setProgress(1.0);

                if (addData.executeUpdate() > 0) {//successful
                    ResultSet rs = addData.getGeneratedKeys();

                    MainWindow.setProgress(0.0);

                    if (rs.next()) {
                        pf.setId(rs.getInt(1));
                        return true;
                    }
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка добавления направления в БД");
                }

            } catch (SQLException e) {
                System.out.println("exception of writing to BD (family)");
            }
        }

        MainWindow.setProgress(0.0);

        return false;
    }

    @Override
    public boolean updateData(Object object) {
        if (object instanceof ProductFamily) {
            ProductFamily pf = (ProductFamily) object;

            try {
                updateData.setString(1, pf.getName());
                updateData.setString(2, pf.getResponsible());
                updateData.setInt(3, pf.getId());

                MainWindow.setProgress(1.0);

                if (updateData.executeUpdate() > 0) {//successful
                    MainWindow.setProgress(0.0);
                    return true;
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка работы с БД");
                }

            } catch (SQLException e) {
                System.out.println("exception of updating in BD");
            }

        }
        MainWindow.setProgress(0.0);

        return false;
    }

    @Override
    public boolean deleteData(Object object) {
        if (object instanceof ProductFamily) {
            ProductFamily pf = (ProductFamily) object;
            try {

                deleteData.setInt(1, pf.getId());
                deleteData.addBatch();

                MainWindow.setProgress(1.0);

                int results[] = deleteData.executeBatch();

                MainWindow.setProgress(0.0);

                for (int res : results) {
                    if (res == 0) return false;//error
                }
                return true;//successful

            } catch (SQLException e) {
                System.out.println("exception of removing family from DB");
            }
        }
        return false;
    }
}
