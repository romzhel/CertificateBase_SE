package ui_windows.options_window.profile_editor;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import static ui_windows.options_window.profile_editor.SimpleRight.DISPLAY;
import static ui_windows.options_window.profile_editor.SimpleRight.HIDE;

public class Profile {
    private int id;
    private StringProperty name;
    private SimpleRight products;
    private SimpleRight fileMenu;
    private SimpleRight fileMenuOpen;
    private SimpleRight fileMenuExportPrice;
    private SimpleRight optionsMenu;
    private SimpleRight certificates;
    private SimpleRight families;
    private SimpleRight orderAccessible;
    private SimpleRight users;
    private SimpleRight priceLists;
    private boolean newItem;

    public static final String COMMON_ACCESS = "Общий доступ";

    public Profile() {
        id = 1;
        name = new SimpleStringProperty(COMMON_ACCESS);
        products = DISPLAY;
        fileMenu = HIDE;
        fileMenuOpen = HIDE;
        fileMenuExportPrice = HIDE;

        certificates = HIDE;
        families = HIDE;
        orderAccessible = HIDE;
        users = HIDE;
        priceLists = HIDE;
        optionsMenu = calcRights(certificates, families, orderAccessible, users, priceLists);

        newItem = true;
    }

    public Profile(ResultSet rs) {
        try {
            id = rs.getInt("id");
            name = new SimpleStringProperty(rs.getString("name"));
            products = SimpleRight.values()[rs.getInt("products")];
//            fileMenu = SimpleRight.values()[rs.getInt("file_menu")];
            fileMenuOpen = SimpleRight.values()[rs.getInt("file_menu_open")];
            fileMenuExportPrice = SimpleRight.values()[rs.getInt("file_menu_export_price")];
            certificates = SimpleRight.values()[rs.getInt("certificates")];
            families = SimpleRight.values()[rs.getInt("families")];
            orderAccessible = SimpleRight.values()[rs.getInt("orderable")];
            users = SimpleRight.values()[rs.getInt("users")];
            priceLists = SimpleRight.values()[rs.getInt("price_lists")];
            newItem = false;

            fileMenu = calcRights(fileMenuOpen, fileMenuExportPrice);
            optionsMenu = calcRights(certificates, families, orderAccessible, users, priceLists);

        } catch (SQLException e) {
            System.out.println("profile constructor exception:" + e.getMessage());
        }
    }

    private SimpleRight calcRights(SimpleRight... rights) {
        for (SimpleRight sr : rights) {
            if (sr != HIDE) return DISPLAY;
        }
        return HIDE;
    }

    public void disableDeleteItem(ContextMenu contextMenu) {
        final String DELETE = "delete";
        for (MenuItem mi:contextMenu.getItems()             ) {
            if (mi.getId() != null && mi.getId().toLowerCase().contains(DELETE) && !name.getValue().toLowerCase().contains("полный")) {
                mi.setDisable(true);
            } else {
                mi.setDisable(false);
            }

        }
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public SimpleRight getProducts() {
        return products;
    }

    public void setProducts(SimpleRight products) {
        this.products = products;
    }

    public SimpleRight getFileMenu() {
        return fileMenu;
    }

    public void setFileMenu(SimpleRight fileMenu) {
        this.fileMenu = fileMenu;
    }

    public SimpleRight getOptionsMenu() {
        return optionsMenu;
    }

    public void setOptionsMenu(SimpleRight optionsMenu) {
        this.optionsMenu = optionsMenu;
    }

    public SimpleRight getCertificates() {
        return certificates;
    }

    public void setCertificates(SimpleRight certificates) {
        this.certificates = certificates;
    }

    public SimpleRight getFamilies() {
        return families;
    }

    public void setFamilies(SimpleRight families) {
        this.families = families;
    }

    public SimpleRight getOrderAccessible() {
        return orderAccessible;
    }

    public void setOrderAccessible(SimpleRight orderAccessible) {
        this.orderAccessible = orderAccessible;
    }

    public SimpleRight getUsers() {
        return users;
    }

    public void setUsers(SimpleRight users) {
        this.users = users;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isNewItem() {
        return newItem;
    }

    public void setNewItem(boolean newItem) {
        this.newItem = newItem;
    }

    public SimpleRight getFileMenuOpen() {
        return fileMenuOpen;
    }

    public SimpleRight getFileMenuExportPrice() {
        return fileMenuExportPrice;
    }

    public void setFileMenuOpen(SimpleRight fileMenuOpen) {
        this.fileMenuOpen = fileMenuOpen;
    }

    public void setFileMenuExportPrice(SimpleRight fileMenuExportPrice) {
        this.fileMenuExportPrice = fileMenuExportPrice;
    }

    public SimpleRight getPriceLists() {
        return priceLists;
    }

    public void setPriceLists(SimpleRight priceLists) {
        this.priceLists = priceLists;
    }
}
