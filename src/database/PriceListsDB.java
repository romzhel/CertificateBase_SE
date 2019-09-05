package database;

import ui_windows.main_window.MainWindow;
import ui_windows.options_window.price_lists_editor.PriceList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class PriceListsDB extends DbRequest {

    public PriceListsDB() {
        super();
        try {
            addData = connection.prepareStatement("INSERT INTO " +
                    "priceLists (name, file_name, lgbks) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            updateData = connection.prepareStatement("UPDATE priceLists " +
                    "SET name = ?, file_name = ?, lgbks = ? WHERE id = ?");
            deleteData = connection.prepareStatement("DELETE FROM priceLists " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            logAndMessage("priceLists prepared statements exception " + e.getMessage());
            finalActions();
        }
    }

    public ArrayList getData() {
        ArrayList<PriceList> priceLists = new ArrayList<>();
        try {
            ResultSet rs = connection.prepareStatement("SELECT * FROM priceLists").executeQuery();

            while (rs.next()) {
                priceLists.add(new PriceList(rs));
            }

        } catch (SQLException e) {
            logAndMessage("SQL exception product families get data");
        }
        return priceLists;
    }

    public boolean putData(PriceList pl) {
        try {
            addData.setString(1, pl.getName());
            addData.setString(2, pl.getFileName());
            addData.setString(3, pl.getLgbksAsString());

            MainWindow.setProgress(1.0);

            if (addData.executeUpdate() > 0) {//successful
                ResultSet rs = addData.getGeneratedKeys();

                if (rs.next()) {
                    pl.setId(rs.getInt(1));
//                        System.out.println("new ID = " + rs.getInt(1));
                    finalActions();
                    return true;
                }
            } else {
                logAndMessage("PriceList DB insert error");
            }
        } catch (SQLException e) {
            logAndMessage("exception of adding to priceList BD, " + e.getMessage());
        }
        finalActions();
        return false;
    }

    public boolean updateData(PriceList pl) {
        try {
            MainWindow.setProgress(1.0);

            updateData.setString(1, pl.getName());
            updateData.setString(2, pl.getFileName());
            updateData.setString(3, pl.getLgbksAsString());

            updateData.setInt(4, pl.getId());

            if (updateData.executeUpdate() > 0) {//successful
                finalActions();
                return true;
            } else {
                logAndMessage("PriceList BD updating error ");
            }
        } catch (SQLException e) {
            logAndMessage("exception of updating to priceList BD, " + e.getMessage());
        }
        finalActions();
        return false;
    }

    public boolean deleteData(PriceList pl) {
        try {
            deleteData.setInt(1, pl.getId());

            MainWindow.setProgress(1.0);

            if (deleteData.executeUpdate() > 0) {//successful
                finalActions();
                return true;
            } else {
                logAndMessage("PriceList BD delete error ");
            }
        } catch (SQLException e) {
            logAndMessage("exception of deleting from BD: " + e.getMessage());
        }
        finalActions();
        return false;
    }
}
