package ui_windows.options_window.user_editor;

import core.CoreModule;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.AnchorPane;
import ui_windows.options_window.profile_editor.Profile;
import utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

public class User {
    private int id;
    private String name;
    private String surname;
    private StringProperty productFamilies;
    private String password;
    private String pcNames;
    private Profile profile;

    public User() {
        name = "Общий";
        surname = "доступ";
        productFamilies = new SimpleStringProperty("");
        password = "";
        pcNames = "";
        profile = new Profile();
    }

    public User(ResultSet rs) {
        try {
            id = rs.getInt("id");
            name = rs.getString("name");
            surname = rs.getString("surname");
            productFamilies = new SimpleStringProperty(rs.getString("product_families"));
            password = rs.getString("password");
            pcNames = rs.getString("pc_names");
            profile = CoreModule.getProfiles().getProfileById(rs.getInt("profile_id"));
        } catch (SQLException e) {
            System.out.println("user constructor ecxeption" + e.getMessage());
        }
    }

    public User(AnchorPane root) {
        name = Utils.getControlValue(root, "tfName");
        surname = Utils.getControlValue(root, "tfSurname");
        productFamilies = new SimpleStringProperty(Utils.getControlValue(root, "lvSelectedFamilies"));
        profile = CoreModule.getProfiles().getProfileByName(Utils.getControlValue(root, "cbProfile"));
        password = Utils.getControlValue(root, "tfPassword");
    }

    public void displayInEditorWindow(AnchorPane root) {
        Utils.setControlValue(root, "tfName", name);
        Utils.setControlValue(root, "tfSurname", surname);

        TreeSet<String> selFamilies = new TreeSet<>();
        selFamilies.addAll(Arrays.asList(getProductFamilies().split("\\,")));
        Utils.setControlValue(root, "lvSelectedFamilies", new ArrayList<>(selFamilies));

        ArrayList<String> allFamilies = CoreModule.getProductFamilies().getFamiliesNames();
        for (String s : new ArrayList<>(selFamilies)) {
            allFamilies.remove(s);
        }
        Utils.setControlValue(UserEditorWindow.getRootAnchorPane(), "lvFamilies", allFamilies);

        Utils.setControlValue(root, "cbProfile", getProfile().getName());

//        Utils.setControlValue(root, "tfPassword", "******");

        if (pcNames != null) {
            TreeSet<String> pcs = new TreeSet<>();
            String pcsa[] = getPcNames().split("\\,");
            pcs.addAll(Arrays.asList(pcsa));

            Utils.setControlValue(root, "lvPcNames", new ArrayList<>(pcs));
        }
    }

    public void addPcname(String pcName) {
        if (pcNames == null || pcNames.length() == 0) pcNames = pcName;
        else pcNames += "," + pcName;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductFamilies() {
        return productFamilies.get();
    }

    public void setProductFamilies(String productFamilies) {
        this.productFamilies.set(productFamilies);
    }

    public StringProperty productFamiliesProperty() {
        return productFamilies;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPcNames() {
        return pcNames;
    }

    public void setPcNames(String pcNames) {
        this.pcNames = pcNames;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
