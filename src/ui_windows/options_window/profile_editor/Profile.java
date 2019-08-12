package ui_windows.options_window.profile_editor;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

import static ui_windows.options_window.profile_editor.SimpleRight.*;

public class Profile {
    private int id;
    private StringProperty name;
    private SimpleRight products;
    private SimpleRight fileMenu;
    private SimpleRight optionsMenu;
    private SimpleRight certificates;
    private SimpleRight families;
    private SimpleRight orderAccessible;
    private SimpleRight users;
    private boolean newItem;

    public static String COMMON_ACCESS = "Общий доступ";

    public Profile() {
        id = 1;
        name = new SimpleStringProperty(COMMON_ACCESS);
        products = DISPLAY;
        fileMenu = HIDE;

        certificates = HIDE;
        families = HIDE;
        orderAccessible = HIDE;
        users = HIDE;
        optionsMenu = calcRights(certificates, families, orderAccessible, users);

        newItem = true;
    }

    public Profile(ResultSet rs) {
        try {
            id = rs.getInt("id");
            name = new SimpleStringProperty(rs.getString("name"));
            products = SimpleRight.values()[rs.getInt("products")];
            fileMenu = SimpleRight.values()[rs.getInt("file_menu")];
            certificates = SimpleRight.values()[rs.getInt("certificates")];
            families = SimpleRight.values()[rs.getInt("families")];
            orderAccessible = SimpleRight.values()[rs.getInt("orderable")];
            users = SimpleRight.values()[rs.getInt("users")];
            newItem = false;

            optionsMenu = calcRights(certificates, families, orderAccessible, users);

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
}
