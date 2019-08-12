package database;

import core.CoreModule;
import core.Dialogs;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AccessibilityDB implements Request {
    PreparedStatement addData, updateData, deleteData;

    public AccessibilityDB() {
        try {
            addData = CoreModule.getDataBase().getDbConnection().prepareStatement("INSERT INTO " +
                            "order_accessibility (status_code, ses_code, description_en, description_ru, f1, f2, " +
                            "status, orderable) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = CoreModule.getDataBase().getDbConnection().prepareStatement("UPDATE order_accessibility " +
                    "SET status_code = ?, ses_code = ?, description_en = ?, description_ru = ?, f1 = ?, f2 = ?, " +
                    "status = ?, orderable = ? WHERE id = ?");
            deleteData = CoreModule.getDataBase().getDbConnection().prepareStatement("DELETE FROM order_accessibility " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            System.out.println("order_accessibility_editor prepared statements exception" + e.getMessage());
        }
    }

    @Override
    public ArrayList getData() {
        ArrayList<OrderAccessibility> ordersAccessibility = new ArrayList<>();
        try {
            ResultSet rs = CoreModule.getDataBase().getData("SELECT * FROM order_accessibility");

            while (rs.next()) {
                ordersAccessibility.add(new OrderAccessibility(rs));
            }

        } catch (SQLException e) {
            System.out.println("SQL exception order accessibility" + e.getMessage());
        }

        return ordersAccessibility;
    }

    @Override
    public boolean putData(Object object) {
        if (object instanceof OrderAccessibility) {
            OrderAccessibility oa = (OrderAccessibility) object;
            try {
                addData.setString(1, oa.getStatusCode());
                addData.setString(2, oa.getSesCode());
                addData.setString(3, oa.getDescriptionEn());
                addData.setString(4, oa.getDescriptionRu());
                addData.setString(5, oa.getF1());
                addData.setString(6, oa.getF2());
                addData.setString(7, oa.getStatus());
                addData.setBoolean(8, oa.isOrderable());

                MainWindow.setProgress(1.0);

                if (addData.executeUpdate() > 0) {//successful
                    ResultSet rs = addData.getGeneratedKeys();

                    if (rs.next()) {
                        oa.setId(rs.getInt(1));
//                        System.out.println("new order accebility ID = " + rs.getInt(1));
                        MainWindow.setProgress(0.0);
                        return true;
                    }
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка работы с БД");
                }

            } catch (SQLException e) {
                System.out.println("exception of order accessibility writing to BD" + e.getMessage());
            }
            MainWindow.setProgress(0.0);

        }
        return false;
    }

    @Override
    public boolean updateData(Object object) {
        if (object instanceof OrderAccessibility) {
            OrderAccessibility oa = (OrderAccessibility) object;

            MainWindow.setProgress(1.0);

            try {
                updateData.setString(1, oa.getStatusCode());
                updateData.setString(2, oa.getSesCode());
                updateData.setString(3, oa.getDescriptionEn());
                updateData.setString(4, oa.getDescriptionRu());
                updateData.setString(5, oa.getF1());
                updateData.setString(6, oa.getF2());
                updateData.setString(7, oa.getStatus());
                updateData.setBoolean(8, oa.isOrderable());
                updateData.setInt(9, oa.getId());

                if (updateData.executeUpdate() > 0) {//successful
                    MainWindow.setProgress(0.0);
                    return true;
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка работы с БД");
                }

            } catch (SQLException e) {
                System.out.println("exception of order accessibility writing to BD" + e.getMessage());
            }

            MainWindow.setProgress(0.0);
        }
        return false;
    }

    @Override
    public boolean deleteData(Object object) {
        if (object instanceof OrderAccessibility) {
            OrderAccessibility oa = (OrderAccessibility) object;

            MainWindow.setProgress(1.0);

            try {
                deleteData.setInt(1, oa.getId());

                if (deleteData.executeUpdate() > 0) {//successful
                    MainWindow.setProgress(0.0);
                    return true;
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка работы с БД");
                }

            } catch (SQLException e) {
                System.out.println("exception of order accessibility deleting in BD" + e.getMessage());
            }

            MainWindow.setProgress(0.0);
        }

        return false;
    }
}
