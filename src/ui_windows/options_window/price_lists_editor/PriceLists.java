package ui_windows.options_window.price_lists_editor;

import core.Dialogs;
import database.PriceListSheetDB;
import database.PriceListsDB;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheets;

import java.util.ArrayList;

public class PriceLists {
    private ArrayList<PriceList> items;
    private PriceListsTable priceListsTable;

    public PriceLists() {

    }

    public PriceLists getFromDB() {
        items = new PriceListsDB().getData();
        PriceListSheets sheets = new PriceListSheets().getFromDB();

        for (PriceList item : items) {
            item.getSheets().addAll(sheets.getPriceListSheets(item.getId()));
        }

        return this;
    }

    public PriceListsTable getPriceListsTable() {
        return priceListsTable;
    }

    public boolean addItem(PriceList priceList) {
        if (isDouble(priceList.getName())) {
            Dialogs.showMessage("Добавление записи", "Прайс-лист с таким именем уже существует. " +
                    "Введите другое имя.");
            return false;
        }

        if (new PriceListsDB().putData(priceList)) {
            for (PriceListSheet sheet : priceList.getSheets()) {
                sheet.setSheetId(priceList.getId());
                if (!new PriceListSheetDB().putData(sheet)) {
                    return false;
                }
            }

            items.add(priceList);
            priceListsTable.getTableView().getItems().add(priceList);
        }
        return true;
    }

    public boolean editItem(PriceList refreshedItem) {
        if (new PriceListsDB().updateData(refreshedItem)) {
//            PriceList selectedItem = priceListsTable.getSelectedItem();
            items.set(priceListsTable.getTableView().getSelectionModel().getSelectedIndex(), refreshedItem);

            for (PriceListSheet sheet : refreshedItem.getSheets()) {
                sheet.setPriceListId(refreshedItem.getId());

                if (sheet.getSheetId() == -1) {
                    if (!(new PriceListSheetDB().putData(sheet))) {
                        return false;
                    }
                } else {
                    if (!(new PriceListSheetDB().updateData(sheet))) {
                        return false;
                    }
                }
            }

            priceListsTable.getTableView().getItems().clear();
            priceListsTable.getTableView().getItems().addAll(items);
            return true;
        }
        return false;
    }

    public void deleteItem(PriceList priceList) {
        if (new PriceListsDB().deleteData(priceList)) {
            items.remove(priceList);
            priceListsTable.getTableView().getItems().remove(priceList);
        }
    }

    public boolean isDouble(String name) {
        for (PriceList pl : items) {
            if (pl.getName() != null && pl.getName().equals(name)) return true;
        }

        return false;
    }

    public void setPriceListsTable(PriceListsTable priceListsTable) {
        this.priceListsTable = priceListsTable;
    }

    public ArrayList<PriceList> getItems() {
        return items;
    }

    public void setItems(ArrayList<PriceList> items) {
        this.items = items;
    }
}
