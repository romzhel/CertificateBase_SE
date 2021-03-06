package ui_windows.product;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductType {
    private int id;
    private String type;
    private String ten;
    private boolean wasChanged = false;

    public ProductType(int id, String type, String ten){
        this.id = id;
        this.type = type;
        this.ten = ten;
    }

    public ProductType(ResultSet rs){
        try {
            id = rs.getInt("id");
            type = rs.getString("product_type");
            ten = rs.getString("type_ten");
        } catch (SQLException e){
            System.out.println("exception product type from DB constructor");
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public boolean wasChanged() {
        return wasChanged;
    }

    public void setWasChanged(boolean wasChanged) {
        this.wasChanged = wasChanged;
    }
}
