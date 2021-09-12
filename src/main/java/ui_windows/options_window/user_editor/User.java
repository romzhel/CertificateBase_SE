package ui_windows.options_window.user_editor;

import javafx.scene.layout.AnchorPane;
import lombok.Data;
import ui_windows.options_window.families_editor.ProductFamilies;
import ui_windows.options_window.profile_editor.Profile;
import ui_windows.options_window.profile_editor.Profiles;
import utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

@Data
public class User {
    private int id;
    private String name;
    private String surname;
    private String productFamilies;
    private String password;
    private String pcNames;
    private Profile profile;

    public User() {
        name = "Общий";
        surname = "доступ";
        productFamilies = "";
        password = "";
        pcNames = "";
        profile = new Profile();
    }

    public User(ResultSet rs) {
        try {
            id = rs.getInt("id");
            name = rs.getString("name");
            surname = rs.getString("surname");
            productFamilies = rs.getString("product_families");
            password = rs.getString("password");
            pcNames = rs.getString("pc_names");
            profile = Profiles.getInstance().getProfileById(rs.getInt("profile_id"));
        } catch (SQLException e) {
            System.out.println("user constructor ecxeption" + e.getMessage());
        }
    }

    public User(AnchorPane root) {
        name = Utils.getControlValue(root, "tfName");
        surname = Utils.getControlValue(root, "tfSurname");
        productFamilies = Utils.getControlValue(root, "lvSelectedFamilies");
        profile = Profiles.getInstance().getProfileByName(Utils.getControlValue(root, "cbProfile"));
        password = Utils.getControlValue(root, "tfPassword");
    }

    public void displayInEditorWindow(AnchorPane root) {
        Utils.setControlValue(root, "tfName", name);
        Utils.setControlValue(root, "tfSurname", surname);

        TreeSet<String> selFamilies = new TreeSet<>();
        selFamilies.addAll(Arrays.asList(getProductFamilies().split("\\,")));
        Utils.setControlValue(root, "lvSelectedFamilies", new ArrayList<>(selFamilies));

        List<String> allFamilies = ProductFamilies.getInstance().getFamiliesNames();
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

    @Override
    public String toString() {
        return String.format("%s %s (id = %d)", name, surname, id);
    }
}
