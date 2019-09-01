package ui_windows.options_window.price_lists_editor;

import core.CoreModule;
import javafx.scene.layout.AnchorPane;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

public class PriceList {
    private int id;
    private String name;
    private String fileName;
    private ArrayList<String> lgbks;

    public PriceList(String type, ArrayList<String> lgbks) {
        this.name = type;
        this.lgbks = lgbks;
    }

    public PriceList(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        name = rs.getString("name");
        fileName = rs.getString("file_name");

        String valueFromDB = rs.getString("lgbks");
        String[] values;
        if (valueFromDB != null && !valueFromDB.isEmpty()) {
            values = valueFromDB.split("\\,");
            lgbks = new ArrayList<String>(Arrays.asList(values));
        } else {
            lgbks = new ArrayList<>();
        }
    }

    public PriceList(AnchorPane root) {
        id = 0;
        name = Utils.getControlValue(root, "tfName");

//        ArrayList<String> lgbkNames = new ArrayList<>();
        ArrayList<String> lgbkDescriptions = Utils.getALControlValueFromLV(root, "lvSelected");
        lgbks = new ArrayList<>(CoreModule.getProductLgbks().getLgbkNameALbyDescsAL(lgbkDescriptions));
        fileName = Utils.getControlValue(root, "tfFileName");
    }

    public void showInEditorWindow(AnchorPane root) {
        Utils.setControlValue(root, "tfName", name);
        Utils.setControlValue(root, "tfFileName", fileName);
    }

    public String getLgbksAsString() {
        String result = "";
        for (String plgbk : lgbks) {
            result += plgbk.concat(",");
        }
        result.replaceAll("\\,$", "");

        return result;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getLgbks() {
        return lgbks;
    }

    public void setLgbks(ArrayList<String> lgbks) {
        this.lgbks = lgbks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
