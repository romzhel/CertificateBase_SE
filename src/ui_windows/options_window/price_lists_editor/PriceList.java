package ui_windows.options_window.price_lists_editor;

import core.CoreModule;
import javafx.scene.layout.AnchorPane;
import ui_windows.options_window.price_lists_editor.se.PriceListEditorWindowControllerv2;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import utils.Utils;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class PriceList {
    private int id = -1;
    private String name = "";
    private String fileName = "";
    private File template;
    private File destination;
    private ArrayList<PriceListSheet> sheets = new ArrayList<>();
    private ArrayList<String> lgbks;

    public PriceList() {
    }

    public PriceList(PriceListEditorWindowControllerv2 controller) {
        name = controller.tfPriceName.getText();
        fileName = controller.tfPriceFileName.getText();
    }

    public PriceList(String type, ArrayList<String> lgbks) {
        this.name = type;
        this.lgbks = lgbks;
    }

    public PriceList(PriceList anotherInstance) {
        id = anotherInstance.id;
        name = anotherInstance.name;
        fileName = anotherInstance.fileName;
        template = anotherInstance.template;
        destination = anotherInstance.destination;
        for (PriceListSheet sheet : anotherInstance.sheets) {
            sheets.add(new PriceListSheet(sheet));
        }
    }

    public PriceList(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        name = rs.getString("name");
        fileName = rs.getString("file_name");
        template = new File(CoreModule.getFolders().getTemplatesFolder() + "\\" + rs.getString("template_name"));

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

    public void showInEditorWindow(PriceListEditorWindowControllerv2 controller) {
        controller.tfPriceName.setText(name);
        controller.tfPriceFileName.setText(fileName);
        if (template != null) {
            controller.tfTemplateName.setText(template.getName().replaceAll("null", ""));
        }

        int sheetIndex = 1;
        for (PriceListSheet sheet : sheets) {
            sheet.setText("Лист " + String.valueOf(sheetIndex++));

            controller.mainTabPane.getTabs().add(sheet);
            controller.mainTabPane.getSelectionModel().clearSelection(sheetIndex - 1);
        }
    }

    public String getLgbksAsString() {
        String result = "";
        if (lgbks != null) {
            for (String plgbk : lgbks) {
                result += plgbk.concat(",");
            }
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

    public File getTemplate() {
        return template;
    }

    public void setTemplate(File template) {
        this.template = template;
    }

    public ArrayList<PriceListSheet> getSheets() {
        return sheets;
    }

    public void setSheets(ArrayList<PriceListSheet> sheets) {
        this.sheets = sheets;
    }

    public File getDestination() {
        return destination;
    }

    public void setDestination(File destination) {
        this.destination = destination;
    }
}
