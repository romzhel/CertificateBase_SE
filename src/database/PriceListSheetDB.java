package database;

import files.price_to_excel.HierarchyGroup;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class PriceListSheetDB extends DbRequest {

    public PriceListSheetDB() {
        super();
        try {
            addData = connection.prepareStatement("INSERT INTO priceListSheets" +
                            "(price_list_id, name, language, init_row, content_mode, lead_time_correction, " +
                            "group_names_displaying, column_enums, content_enums, dchain_enums, discount, sort_order) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = connection.prepareStatement("UPDATE priceListSheets " +
                    "SET price_list_id = ?, name = ?, language = ?, init_row = ?, content_mode = ?, lead_time_correction = ?, " +
                    "group_names_displaying = ?, column_enums = ?, content_enums = ?, dchain_enums = ?, discount = ?," +
                    "sort_order = ? WHERE id = ?");
            deleteData = connection.prepareStatement("DELETE FROM priceListSheets " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            logAndMessage("priceListSheets prepared statements exception" + e.getMessage());
            finalActions();
        }
    }


    public ArrayList<PriceListSheet> getData() {
        ArrayList<PriceListSheet> priceListSheets = new ArrayList<>();
        try {
            ResultSet rs = connection.prepareStatement("SELECT * FROM priceListSheets").executeQuery();

            while (rs.next()) {
                priceListSheets.add(new PriceListSheet(rs));
            }

            rs.close();

        } catch (SQLException e) {
            logAndMessage("SQL exception priceListSheets" + e.getMessage());
        }

        return priceListSheets;
    }

    public boolean putData(PriceListSheet pls) {
        try {
            int index = 1;
            addData.setInt(index++, pls.getPriceListId());
            addData.setString(index++, pls.getSheetName());
            addData.setInt(index++, pls.getLanguage());
            addData.setInt(index++, pls.getInitialRow());
            addData.setInt(index++, pls.getContentMode());
            addData.setInt(index++, pls.getLeadTimeCorrection());
            addData.setBoolean(index++, pls.isGroupNameDisplaying());
            addData.setString(index++, pls.getColumnsSelector().getSelectedItemsAsString());
            addData.setString(index++, pls.getContentTable().exportToString());
            addData.setString(index++, pls.getDchainSelector().getSelectedItemsAsString());
            addData.setInt(index++, pls.getDiscount());
            addData.setInt(index++, pls.getSortOrder() == HierarchyGroup.SORT_MATERIAL ? 0 : 1);

            MainWindow.setProgress(1.0);

            if (addData.executeUpdate() > 0) {//successful
                ResultSet rs = addData.getGeneratedKeys();

                if (rs.next()) {
                    pls.setSheetId(rs.getInt(1));
//                        System.out.println("new order accebility ID = " + rs.getInt(1));
                    return true;
                }
            } else {
                logAndMessage("PriceListSheet DB answer error");
            }

        } catch (SQLException e) {
            logAndMessage("exception of PriceListSheet writing to BD" + e.getMessage());
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean updateData(PriceListSheet pls) {
        MainWindow.setProgress(1.0);

        try {
            int index = 1;
            updateData.setInt(index++, pls.getPriceListId());
            updateData.setString(index++, pls.getSheetName());
            updateData.setInt(index++, pls.getLanguage());
            updateData.setInt(index++, pls.getInitialRow());
            updateData.setInt(index++, pls.getContentMode());
            updateData.setInt(index++, pls.getLeadTimeCorrection());
            updateData.setBoolean(index++, pls.isGroupNameDisplaying());
            updateData.setString(index++, pls.getColumnsSelector().getSelectedItemsAsString());
            updateData.setString(index++, pls.getContentTable().exportToString());
            updateData.setString(index++, pls.getDchainSelector().getSelectedItemsAsString());
            updateData.setInt(index++, pls.getDiscount());
            updateData.setInt(index++, pls.getSortOrder() == HierarchyGroup.SORT_MATERIAL ? 0 : 1);

            updateData.setInt(index++, pls.getSheetId());

            if (updateData.executeUpdate() > 0) {//successful
                return true;
            } else {
                logAndMessage("PriceListSheet DB update error");
            }

        } catch (SQLException e) {
            logAndMessage("exception of PriceListSheet writing to BD" + e.getMessage());
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean deleteData(PriceListSheet pls) {
        MainWindow.setProgress(1.0);

        try {
            deleteData.setInt(1, pls.getSheetId());

            if (deleteData.executeUpdate() > 0) {//successful
                return true;
            } else {
                logAndMessage("PriceListSheet DB delete error");
            }

        } catch (SQLException e) {
            logAndMessage("exception of PriceListSheet deleting in BD" + e.getMessage());
        } finally {
            finalActions();
        }
        return false;
    }
}
