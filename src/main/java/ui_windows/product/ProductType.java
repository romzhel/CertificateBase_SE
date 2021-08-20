package ui_windows.product;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@Data
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

    public ProductType(ResultSet rs) {
        try {
            id = rs.getInt("id");
            type = rs.getString("product_type");
            ten = rs.getString("type_ten");
        } catch (SQLException e) {
            System.out.println("exception product type from DB constructor");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductType that = (ProductType) o;
        return type.equals(that.type) && ten.equals(that.ten);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, ten);
    }
}
