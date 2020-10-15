package database;

import files.price_to_excel.HierarchyGroup;
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
                            "group_names_displaying, column_enums, content_enums, dchain_enums, discount, sort_order," +
                            "check_cert) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            updateData = connection.prepareStatement("UPDATE priceListSheets " +
                    "SET price_list_id = ?, name = ?, language = ?, init_row = ?, content_mode = ?, lead_time_correction = ?, " +
                    "group_names_displaying = ?, column_enums = ?, content_enums = ?, dchain_enums = ?, discount = ?," +
                    "sort_order = ?, check_cert = ? WHERE id = ?");
            deleteData = connection.prepareStatement("DELETE FROM priceListSheets " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            logAndMessage("priceListSheets prepared statements exception", e);
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
            logAndMessage("SQL exception priceListSheets", e);
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
            addData.setBoolean(index++, pls.isCheckCert());

            if (addData.executeUpdate() > 0) {//successful
                ResultSet rs = addData.getGeneratedKeys();

                if (rs.next()) {
                    pls.setSheetId(rs.getInt(1));
//                        System.out.println("new order accebility ID = " + rs.getInt(1));
                    return true;
                }
            } else {
                logAndMessage("PriceListSheet DB answer error", new RuntimeException());
            }

        } catch (SQLException e) {
            logAndMessage("exception of PriceListSheet writing to BD", e);
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean updateData(PriceListSheet pls) {
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
            updateData.setBoolean(index++, pls.isCheckCert());

            updateData.setInt(index++, pls.getSheetId());

            if (updateData.executeUpdate() > 0) {//successful
                return true;
            } else {
                logAndMessage("PriceListSheet DB update error", new RuntimeException());
            }

        } catch (SQLException e) {
            logAndMessage("exception of PriceListSheet writing to BD", e);
        } finally {
            finalActions();
        }
        return false;
    }

    public boolean deleteData(PriceListSheet pls) {
        try {
            deleteData.setInt(1, pls.getSheetId());

            if (deleteData.executeUpdate() > 0) {//successful
                return true;
            } else {
                logAndMessage("PriceListSheet DB delete error", new RuntimeException());
            }

        } catch (SQLException e) {
            logAndMessage("exception of PriceListSheet deleting in BD", e);
        } finally {
            finalActions();
        }
        return false;
    }
}
