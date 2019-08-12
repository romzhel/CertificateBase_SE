package database;

import core.CoreModule;
import core.Dialogs;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.requirements_types_editor.RequirementType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class RequirementTypesDB implements Request {
    PreparedStatement addData, updateData, deleteData;

    public RequirementTypesDB() {
        try {
            addData = CoreModule.getDataBase().getDbConnection().prepareStatement("INSERT INTO " +
                            "requirementTypes (req_short_name, req_full_name) VALUES (?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = CoreModule.getDataBase().getDbConnection().prepareStatement("UPDATE requirementTypes " +
                    "SET req_short_name = ?, req_full_name = ? WHERE id = ?");
            deleteData = CoreModule.getDataBase().getDbConnection().prepareStatement("DELETE FROM requirementTypes " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            System.out.println("add data prepared statement exception, " + e.getMessage());
        }
    }

    @Override
    public ArrayList getData() {
        ArrayList<RequirementType> requirementTypes = new ArrayList<>();
        try {
            ResultSet rs = CoreModule.getDataBase().getData("SELECT * FROM requirementTypes");

            while (rs.next()) {
                requirementTypes.add(new RequirementType(rs));
            }

        } catch (SQLException e) {
            System.out.println("SQL exception cert types, " + e.getMessage());
        }

        return requirementTypes;
    }

    @Override
    public boolean putData(Object object) {
        if (object instanceof RequirementType) {
            RequirementType ct = (RequirementType) object;
            try {
                addData.setString(1, ct.getShortName());
                addData.setString(2, ct.getFullName());

                MainWindow.setProgress(1.0);

                if (addData.executeUpdate() > 0) {//successful
                    ResultSet rs = addData.getGeneratedKeys();

                    MainWindow.setProgress(0.0);

                    if (rs.next()) {
                        ct.setId(rs.getInt(1));
                        System.out.println("new ID = " + rs.getInt(1));
                        return true;
                    }
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка работы с БД");
                }

            } catch (SQLException e) {
                System.out.println("exception of writing to BD");
            }

        }
        MainWindow.setProgress(0.0);

        return false;
    }

    @Override
    public boolean updateData(Object object) {
        if (object instanceof RequirementType) {
            RequirementType ct = (RequirementType) object;

            System.out.println("updating of " + ct.getFullName());
            try {
                updateData.setString(1, ct.getShortName());
                updateData.setString(2, ct.getFullName());
                updateData.setInt(3, ct.getId());

                MainWindow.setProgress(1.0);

                if (updateData.executeUpdate() > 0) {//successful
                    MainWindow.setProgress(0.0);
                    return true;
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка работы с БД");
                }

            } catch (SQLException e) {
                System.out.println("exception of writing to BD");
            }

        }
        MainWindow.setProgress(0.0);

        return false;
    }

    @Override
    public boolean deleteData(Object object) {
        if (object instanceof RequirementType) {
            RequirementType ct = (RequirementType) object;

            try {
                deleteData.setInt(1, ct.getId());

                MainWindow.setProgress(1.0);

                if (deleteData.executeUpdate() > 0) {//successful
                    MainWindow.setProgress(0.0);
                    return true;
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка работы с БД");
                }

            } catch (SQLException e) {
                System.out.println("exception of writing to BD");
            }
        }
        MainWindow.setProgress(0.0);

        return false;
    }
}
