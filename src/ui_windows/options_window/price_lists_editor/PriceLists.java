package ui_windows.options_window.price_lists_editor;

import core.Dialogs;
import database.PriceListsDB;

import java.util.ArrayList;

public class PriceLists {
    private ArrayList<PriceList> items;
    private PriceListsTable priceListsTable;

    public PriceLists() {

    }

    public PriceLists getFromDB() {
        items = new PriceListsDB().getData();
        return this;
    }

    public PriceListsTable getPriceListsTable() {
        return priceListsTable;
    }

    public void addItem(PriceList priceList) {
        if (isDouble(priceList.getName())) {
            Dialogs.showMessage("Добавление записи", "Прайс-лист с таким именем уже существует. " +
                    "Введите другое имя.");
            return;
        }

        if (new PriceListsDB().putData(priceList)) {
            items.add(priceList);
            priceListsTable.getTableView().getItems().add(priceList);
        }
    }

    public void editItem(PriceList priceList){
        if (new PriceListsDB().updateData(priceList)) {

        }
    }

    public void deleteItem(PriceList priceList){
        if (new PriceListsDB().deleteData(priceList)) {
            items.remove(priceList);
            priceListsTable.getTableView().getItems().remove(priceList);
        }
    }

    public boolean isDouble(String name) {
        for (PriceList pl : items) {
            if (pl.getName().equals(name)) return true;
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
