package ui_windows.options_window.families_editor;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.AnchorPane;
import ui_windows.options_window.price_lists_editor.se.PriceListContentItem;
import ui_windows.options_window.price_lists_editor.se.PriceListContentTableItem;
import utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductFamily implements PriceListContentItem {
    private int id;
    private StringProperty name;
    private StringProperty responsible;

    public ProductFamily(){
        name = new SimpleStringProperty("введите название");
        responsible = new SimpleStringProperty("введите ответственных");
    }

    public ProductFamily(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public ProductFamily(AnchorPane root){
        name = new SimpleStringProperty(Utils.getControlValue(root, "tfFamily"));
        responsible = new SimpleStringProperty(Utils.getControlValue(root, "taResponsible"));
    }

    public ProductFamily(ResultSet rs){
        try {
            id = rs.getInt("id");
            name = new SimpleStringProperty(rs.getString("name"));
            responsible = new SimpleStringProperty(rs.getString("responsible"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void showInEditorWindow(AnchorPane root){
        Utils.setControlValue(root, "tfFamily", getName());
        Utils.setControlValue(root, "taResponsible", getResponsible());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getResponsible() {
        return responsible.get();
    }

    public StringProperty responsibleProperty() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible.set(responsible);
    }

    @Override
    public PriceListContentTableItem getTableItem() {
        return new PriceListContentTableItem(this);
    }

    @Override
    public String toString() {
        return getName();
    }
}
