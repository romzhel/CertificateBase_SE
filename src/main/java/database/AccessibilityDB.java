package database;

import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AccessibilityDB extends DbRequest {

    public AccessibilityDB() {
        super();
        try {
            addData = connection.prepareStatement("INSERT INTO " +
                            "order_accessibility (status_code, ses_code, description_en, description_ru, f1, f2, " +
                            "status, orderable) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = connection.prepareStatement("UPDATE order_accessibility " +
                    "SET status_code = ?, ses_code = ?, description_en = ?, description_ru = ?, f1 = ?, f2 = ?, " +
                    "status = ?, orderable = ? WHERE id = ?");
            deleteData = connection.prepareStatement("DELETE FROM order_accessibility " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            logAndMessage("order_accessibility_editor prepared statements exception", e);
            finalActions();
        }
    }

    public ArrayList getData() {
        ArrayList<OrderAccessibility> ordersAccessibility = new ArrayList<>();
        try {
            ResultSet rs = connection.prepareStatement("SELECT * FROM order_accessibility").executeQuery();

            while (rs.next()) {
                ordersAccessibility.add(new OrderAccessibility(rs));
            }

            rs.close();

        } catch (SQLException e) {
            logAndMessage("SQL exception order accessibility", e);
        }

        return ordersAccessibility;
    }

    public boolean putData(OrderAccessibility oa) {
        try {
            addData.setString(1, oa.getStatusCode());
            addData.setString(2, oa.getSesCode());
            addData.setString(3, oa.getDescriptionEn());
            addData.setString(4, oa.getDescriptionRu());
            addData.setString(5, oa.getF1());
            addData.setString(6, oa.getF2());
            addData.setString(7, oa.getStatus());
            addData.setBoolean(8, oa.isOrderable());

            if (addData.executeUpdate() > 0) {//successful
                ResultSet rs = addData.getGeneratedKeys();

                if (rs.next()) {
                    oa.setId(rs.getInt(1));
//                        System.out.println("new order accebility ID = " + rs.getInt(1));
                    return true;
                }
            } else {
                logAndMessage("OrderAccessibility DB answer error", new RuntimeException(""));
            }

        } catch (SQLException e) {
            logAndMessage("exception of order accessibility writing to BD", e);
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean updateData(OrderAccessibility oa) {
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
                return true;
            } else {
                logAndMessage("OrderAccessibility DB update error", new RuntimeException());
            }

        } catch (SQLException e) {
            logAndMessage("exception of order accessibility writing to BD", e);
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean deleteData(OrderAccessibility oa) {
        try {
            deleteData.setInt(1, oa.getId());

            if (deleteData.executeUpdate() > 0) {//successful
                return true;
            } else {
                logAndMessage("", new RuntimeException("OrderAccessibility DB delete error"));
            }

        } catch (SQLException e) {
            logAndMessage("exception of order accessibility deleting in BD", e);
        } finally {
            finalActions();
        }
        return false;
    }
}
