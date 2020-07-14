package ui_windows.options_window.order_accessibility_editor;

import core.Initializable;
import database.AccessibilityDB;

import java.util.ArrayList;

public class OrdersAccessibility implements Initializable {
    private static OrdersAccessibility instance;
    private ArrayList<OrderAccessibility> ordersAccessibility;
    private OrdersAccessibilityTable table;

    private OrdersAccessibility() {
        ordersAccessibility = new ArrayList<>();
    }

    public static OrdersAccessibility getInstance() {
        if (instance == null) {
            instance = new OrdersAccessibility();
        }
        return instance;
    }

    @Override
    public void init() {
        ordersAccessibility = new AccessibilityDB().getData();
    }

    public void addItem(OrderAccessibility oa) {
        if (new AccessibilityDB().putData(oa)) {
            ordersAccessibility.add(oa);
            table.getTableView().getItems().add(oa);
        }
    }

    public void updateItem(OrderAccessibility oa) {
        new AccessibilityDB().updateData(oa);
    }

    public void removeItem(OrderAccessibility oa) {
        if (new AccessibilityDB().deleteData(oa)) {
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

    public String getCombineOrderAccessibility(String dchain) {
        String result = "(" + dchain + ")";
        for (OrderAccessibility oa : ordersAccessibility) {
            if (oa.getStatusCode().equals(dchain)) {
                return result.concat(" ").concat(oa.getDescription());
            }
        }
        return result;
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
