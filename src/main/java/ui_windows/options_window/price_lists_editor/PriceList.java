package ui_windows.options_window.price_lists_editor;

import files.Folders;
import files.price_to_excel.PriceStructure;
import javafx.scene.layout.AnchorPane;
import ui_windows.options_window.price_lists_editor.se.PriceListEditorWindowControllerv2;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.options_window.product_lgbk.ProductLgbks;
import ui_windows.product.Product;
import utils.Utils;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PriceList {
    private int id = -1;
    private String name = "";
    private String fileName = "";
    private File template;
    private File destination;
    private ArrayList<PriceListSheet> sheets = new ArrayList<>();
    private ArrayList<String> lgbks;
    private ArrayList<PriceStructure> priceStructures;
    private ArrayList<Product> problemItems;

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
        template = new File(Folders.getInstance().getTemplatesFolder() + "\\" + rs.getString("template_name"));
        destination = new File(rs.getString("destination_folder"));
    }

    public PriceList(AnchorPane root) {
        id = 0;
        name = Utils.getControlValue(root, "tfName");

//        ArrayList<String> lgbkNames = new ArrayList<>();
        ArrayList<String> lgbkDescriptions = Utils.getALControlValueFromLV(root, "lvSelected");
        lgbks = new ArrayList<>(ProductLgbks.getInstance().getLgbkNameALbyDescsAL(lgbkDescriptions));
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
        if (destination != null) {
            controller.tfDestinationFolder.setText(destination.getPath());
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

    /*public PriceListSheet getPriceListSheet(Product product) {
        for (PriceListSheet priceListSheet : sheets) {
            if (priceListSheet.isInPrice(product)) {
                return priceListSheet;
            }
        }
        return null;
    }

    public double getCostFromPrice(Product product) {
        PriceListSheet pls = getPriceListSheet(product);
        double cost = product.getLocalPrice();

        return pls == null || cost == 0.0 ? 0.0 : cost * (1.0 - (double) pls.getDiscount() / 100);
    }*/

    public ArrayList<PriceStructure> generate() {
        priceStructures = new ArrayList<>(10);
        problemItems = new ArrayList<>();

        for (PriceListSheet priceListSheet : sheets) {
            PriceStructure priceStructure = new PriceStructure(priceListSheet);
            priceStructure.analysePriceItems();
            problemItems.addAll(priceStructure.getProblemItems());
            priceStructures.add(priceStructure);
        }

        return priceStructures;
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

    public ArrayList<PriceStructure> getPriceStructures() {
        return priceStructures;
    }

    public ArrayList<Product> getProblemItems() {
        return problemItems;
    }
}
