package ui_windows.options_window.requirements_types_editor;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RequirementType {
    private int id;
    private StringProperty shortName;
    private StringProperty fullName;

    public RequirementType(int id, String shortName, String fullName) {
        this.id = id;
        this.shortName = new SimpleStringProperty(shortName);
        this.fullName = new SimpleStringProperty(fullName);
    }

    public RequirementType(ResultSet rs){
        try {
            id = rs.getInt("id");
            shortName = new SimpleStringProperty(rs.getString("req_short_name"));
            fullName = new SimpleStringProperty(rs.getString("req_full_name"));
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName.get();
    }

    public StringProperty shortNameProperty() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName.set(shortName);
    }

    public String getFullName() {
        return fullName.get();
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName.set(fullName);
    }

    @Override
    public String toString() {
        return "Certificate Type: id = " + id + ", shortName = " + shortName.getValue() + ", fullName = " + fullName.getValue();
    }
}
