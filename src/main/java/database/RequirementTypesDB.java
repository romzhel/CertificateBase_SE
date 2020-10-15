package database;

import ui_windows.options_window.requirements_types_editor.RequirementType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class RequirementTypesDB extends DbRequest {

    public RequirementTypesDB() {
        super();
        try {
            addData = connection.prepareStatement("INSERT INTO " +
                            "requirementTypes (req_short_name, req_full_name) VALUES (?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = connection.prepareStatement("UPDATE requirementTypes " +
                    "SET req_short_name = ?, req_full_name = ? WHERE id = ?");
            deleteData = connection.prepareStatement("DELETE FROM requirementTypes " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            logAndMessage("add data prepared statement exception, ", e);
            finalActions();
        }
    }

    public ArrayList getData() {
        ArrayList<RequirementType> requirementTypes = new ArrayList<>();
        try {
            ResultSet rs = connection.prepareStatement("SELECT * FROM requirementTypes").executeQuery();

            while (rs.next()) {
                requirementTypes.add(new RequirementType(rs));
            }

            rs.close();
        } catch (SQLException e) {
            logAndMessage("SQL exception cert types, ", e);
        }
        return requirementTypes;
    }

    public boolean putData(RequirementType ct) {
        try {
            addData.setString(1, ct.getShortName());
            addData.setString(2, ct.getFullName());

            if (addData.executeUpdate() > 0) {//successful
                ResultSet rs = addData.getGeneratedKeys();

                if (rs.next()) {
                    ct.setId(rs.getInt(1));
//                        System.out.println("new ID = " + rs.getInt(1));
                    return true;
                }
            } else {
                logAndMessage("", new RuntimeException("SQL exception inserting cert types"));
            }
        } catch (SQLException e) {
            logAndMessage("SQL exception inserting cert types", e);
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean updateData(RequirementType ct) {
        try {
            updateData.setString(1, ct.getShortName());
            updateData.setString(2, ct.getFullName());
            updateData.setInt(3, ct.getId());

            if (updateData.executeUpdate() > 0) {//successful
                return true;
            } else {
                logAndMessage("", new RuntimeException("SQL exception updating cert types"));
            }
        } catch (SQLException e) {
            logAndMessage("SQL exception updating cert types ", e);
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean deleteData(RequirementType ct) {
        try {
            deleteData.setInt(1, ct.getId());

            if (deleteData.executeUpdate() > 0) {//successful
                return true;
            } else {
                logAndMessage("", new RuntimeException("SQL exception inserting cert types"));
            }
        } catch (SQLException e) {
            logAndMessage("SQL exception deleting cert types", e);
        } finally {
            finalActions();
        }
        return false;
    }
}
