package ui_windows.options_window.order_accessibility_editor;

import database.AccessibilityDB;
import database.Request;

import java.util.ArrayList;

public class OrdersAccessibility {
    private Request db;
    private ArrayList<OrderAccessibility> ordersAccessibility;
    private OrdersAccessibilityTable table;

    public OrdersAccessibility() {
        db = new AccessibilityDB();
        ordersAccessibility = new ArrayList<>();
    }

    public OrdersAccessibility getFromDB() {
        ordersAccessibility = db.getData();
        return this;
    }

    public void addItem(OrderAccessibility oa) {
        if (db.putData(oa)) {
            ordersAccessibility.add(oa);
            table.getTableView().getItems().add(oa);
        }
    }

    public void updateItem(OrderAccessibility oa) {
        db.updateData(oa);
    }

    public void removeItem(OrderAccessibility oa) {
        if (db.deleteData(oa)) {
            ordersAccessibility.remove(oa);
            table.getTableView().getItems().remove(oa);
        }
    }

    public OrderAccessibility getOrderAccessibilityByStatusCode(String statusCode) {
        for (OrderAccessibility oa : ordersAccessibility) {
            if (oa.getStatusCode().equals(statusCode)) return oa;
        }
        return null;
    }

    public OrdersAccessibilityTable getTable() {
        return table;
    }

    public void setTable(OrdersAccessibilityTable table) {
        this.table = table;
    }

    public ArrayList<OrderAccessibility> getItems() {
        return ordersAccessibility;
    }
}
