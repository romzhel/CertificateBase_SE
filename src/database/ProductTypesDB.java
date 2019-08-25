package database;

import core.CoreModule;
import core.Dialogs;
import ui_windows.main_window.MainWindow;
import ui_windows.product.ProductType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ProductTypesDB implements Request {
    PreparedStatement addData, updateData, deleteData;

    public ProductTypesDB() {
        try {
            addData = CoreModule.getDataBase().getDbConnection().prepareStatement("INSERT INTO " +
                            "productTypes (product_type, type_ten) VALUES (?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
//            updateData = CoreModule.getDataBase().getDbConnection().prepareStatement("UPDATE productTypes " +
//                    "SET name = ?, cert_type_id = ?, expiration_date = ?, countries = ?, file_name = ? WHERE id = ?");
            deleteData = CoreModule.getDataBase().getDbConnection().prepareStatement("DELETE FROM productTypes " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            System.out.println("products prepared statements exception");
        }
    }

    @Override
    public ArrayList getData() {
        ArrayList<ProductType> productTypes = new ArrayList<>();
        try {
            ResultSet rs = CoreModule.getDataBase().getData("SELECT * FROM productTypes");

            while (rs.next()) {
                productTypes.add(new ProductType(rs));
            }

        } catch (SQLException e) {
            System.out.println("SQL exception productTypes get data");
        }

        return productTypes;
    }

    @Override
    public boolean putData(Object object) {
        if (object instanceof ProductType) {
            ProductType pt = (ProductType) object;
            try {
                addData.setString(1, pt.getType());
                addData.setString(2, pt.getTen());

                MainWindow.setProgress(1.0);

                if (addData.executeUpdate() > 0) {//successful
                    ResultSet rs = addData.getGeneratedKeys();

                    MainWindow.setProgress(0.0);

                    if (rs.next()) {
                        pt.setId(rs.getInt(1));
                        System.out.println("new product type ID = " + rs.getInt(1));
                        return true;
                    }
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка добавления типа оборудования в БД");
                }

            } catch (SQLException e) {
                System.out.println("exception of writing to BD");
            }
        }
        MainWindow.setProgress(0.0);

        return false;
    }

    @Override
    public boolean updateData(Object object) {
        return false;
    }

    @Override
    public boolean deleteData(Object object) {
        if (object instanceof ProductType) {
            ProductType pt = (ProductType) object;

            MainWindow.setProgress(1.0);

            try {

                deleteData.setInt(1, pt.getId());
                deleteData.addBatch();

                int results[] = deleteData.executeBatch();

                MainWindow.setProgress(0.0);

                for (int res : results) {
                    if (res == 0) return false;//error
                }
                return true;//successful

            } catch (SQLException e) {
                System.out.println("exception of removing product type(s) from DB");
            }

        }

        return false;
    }
}
