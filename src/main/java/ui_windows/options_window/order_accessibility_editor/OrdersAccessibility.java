package ui_windows.options_window.order_accessibility_editor;

import core.Initializable;
import database.AccessibilityDB;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import ui_windows.product.Product;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Log4j2
public class OrdersAccessibility implements Initializable {
    private static OrdersAccessibility instance;
    private final OrderAccessibility EMPTY = new OrderAccessibility("", "", "Unknown", "Неизвестно");
    private Map<String, OrderAccessibility> ordersAccessibilityMap;
    private Map<String, OrderAccessibility> alternativeOrdersAccessibilityMap;
    private OrdersAccessibilityTable table;

    private OrdersAccessibility() {
    }

    public static OrdersAccessibility getInstance() {
        if (instance == null) {
            instance = new OrdersAccessibility();
        }
        return instance;
    }

    @Override
    public void init() {
        List<OrderAccessibility> items = new AccessibilityDB().getData();
        ordersAccessibilityMap = items.stream()
                .filter(oa -> !oa.getStatusCode().trim().isEmpty())
                .collect(Collectors.toMap(
                        OrderAccessibility::getStatusCode,
                        (oa) -> oa));

        alternativeOrdersAccessibilityMap = items.stream()
                .filter(oa -> oa.getStatusCode().trim().isEmpty())
                .collect(Collectors.toMap(
                        OrderAccessibility::getAlternativeStatusCode,
                        (oa) -> oa));
    }

    public void addItem(OrderAccessibility oa) {
        if (new AccessibilityDB().putData(oa)) {
            ordersAccessibilityMap.put(oa.getStatusCode(), oa);
            table.getTableView().getItems().add(oa);
        }
    }

    public void updateItem(OrderAccessibility oa) {
        new AccessibilityDB().updateData(oa);
    }

    public void removeItem(OrderAccessibility oa) {
        if (new AccessibilityDB().deleteData(oa)) {
            ordersAccessibilityMap.remove(oa.getStatusCode());
            table.getTableView().getItems().remove(oa);
        }
    }

    public OrderAccessibility getOrderAccessibilityByStatusCode(String statusCode) {
        OrderAccessibility result = ordersAccessibilityMap.getOrDefault(statusCode, alternativeOrdersAccessibilityMap.get(statusCode));
        return result != null ? result : EMPTY;
    }

    public OrderAccessibility getOrderAccessibility(Product product) {
        String dchain = product.getDchain();
        if (dchain != null && !dchain.trim().isEmpty()) {
            return ordersAccessibilityMap.get(dchain);
        }

        Set<String> spPositions = new HashSet<>(Arrays.asList("VIN", "VAC", "VVI"));
        Set<String> setsPositions = new HashSet<>(Arrays.asList("RU5:SETS"));

        if (spPositions.contains(product.getHierarchy())) {
            return alternativeOrdersAccessibilityMap.get("sp");
        } else if (setsPositions.contains(product.getLgbk())) {
            return alternativeOrdersAccessibilityMap.get("sets");
        } else {
            return EMPTY;
        }
    }
}
