package database;

import core.CoreModule;
import core.Dialogs;
import javafx.application.Platform;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.certificates_editor.Certificate;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.product.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class PriceListsDB implements Request {
    PreparedStatement addData, updateData, deleteData;

    public PriceListsDB() {
        try {
            addData = CoreModule.getDataBase().getDbConnection().prepareStatement("INSERT INTO " +
                    "priceLists (name, lgbks) VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS);
            updateData = CoreModule.getDataBase().getDbConnection().prepareStatement("UPDATE priceLists " +
                    "SET name = ?, lgbks = ? WHERE id = ?");
            deleteData = CoreModule.getDataBase().getDbConnection().prepareStatement("DELETE FROM priceLists " +
                    "WHERE id = ?");
        } catch (SQLException e) {
            System.out.println("priceLists prepared statements exception " + e.getMessage());
        }
    }

    @Override
    public ArrayList getData() {
        ArrayList<PriceList> priceLists = new ArrayList<>();
        try {
            ResultSet rs = CoreModule.getDataBase().getData("SELECT * FROM priceLists");

            while (rs.next()) {
                priceLists.add(new PriceList(rs));
            }

        } catch (SQLException e) {
            System.out.println("SQL exception product families get data");
        }

        return priceLists;
    }

    @Override
    public boolean putData(Object object) {
        if (object instanceof ArrayList) {
            ArrayList<PriceList> alpl = (ArrayList<PriceList>) object;

            MainWindow.setProgress(0.01);

            int j = 0;
            try {
                int count = 0;
                for (int i = 0; i < alpl.size(); i = i + 500) {

                    for (j = i; j < (i + 500) && (j < alpl.size()); j++) {
                        count = 0;
                        addData.setString(++count, alpl.get(j).getName());
                        addData.setString(++count, alpl.get(j).getLgbksAsString());
                        addData.addBatch();
                    }

                    MainWindow.setProgress((double) j / (double) alpl.size());

                    int[] result = addData.executeBatch();
                    for (int res : result) {
                        if (res != 1) {
                            Dialogs.showMessage("Запись данных в БД", "Данные не были добавлены в БД");
                            return false;
                        }
                    }
                }

                MainWindow.setProgress(0.0);
                return true;

            } catch (SQLException e) {
                System.out.println("exception of adding to priceList BD, " + e.getMessage() + ", " + alpl.get(--j).toString());
            }

        }
        return false;
    }

    @Override
    public boolean updateData(Object object) {
        if (object instanceof ArrayList) {
            ArrayList<PriceList> alpl = (ArrayList<PriceList>) object;

            MainWindow.setProgress(0.01);
            try {
                int count = 0;
                for (int i = 0; i < alpl.size(); i = i + 500) {

                    int j;
                    for (j = i; j < (i + 500) && (j < alpl.size()); j++) {
                        count = 0;
                        updateData.setString(++count, alpl.get(j).getName());
                        updateData.setString(++count, alpl.get(j).getLgbksAsString());

                        updateData.setInt(++count, alpl.get(j).getId());
                        updateData.addBatch();
                    }
                    MainWindow.setProgress((double) j / (double) alpl.size());
                    int[] result = updateData.executeBatch();

                    for (int res : result) {
                        if (res != 1) {
                            Platform.runLater(() -> Dialogs.showMessage("Запись данных в БД", "Данные не были обновлены в БД, " +
                                    "ответ: " + res));
                            return false;
                        }
                    }
                }

                MainWindow.setProgress(0.0);
                return true;

            } catch (SQLException e) {
                System.out.println("exception of updating to priceList BD, " + e.getMessage());
            }

        }
        return false;
    }

    @Override
    public boolean deleteData(Object object) {
        if (object instanceof PriceList) {
            PriceList pl = (PriceList) object;

            try {
                deleteData.setInt(1, pl.getId());

                MainWindow.setProgress(1.0);

                if (deleteData.executeUpdate() > 0) {//successful
                    MainWindow.setProgress(0.0);
                    return true;
                } else {
                    Dialogs.showMessage("Ошибка БД", "Ошибка работы с БД");
                }

            } catch (SQLException e) {
                System.out.println();
                showErrorMessage(e.getMessage(), "exception of deleting from BD: " + e.getMessage() + "\n" +
                        e.getStackTrace());
            }
        }
        MainWindow.setProgress(0.0);
        return false;
    }

    public void showErrorMessage(String messageText, String logText){
        Dialogs.showMessage("Ошибка работы с базой данных", messageText);
        System.out.println(logText);
    }
}
