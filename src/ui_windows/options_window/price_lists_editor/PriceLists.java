package ui_windows.options_window.price_lists_editor;

import core.Dialogs;
import database.PriceListsDB;

import java.util.ArrayList;

public class PriceLists {
    private ArrayList<PriceList> items;
    private PriceListsDB priceListsDB;
    private PriceListsTable priceListsTable;

    public PriceLists() {
        priceListsDB = new PriceListsDB();
    }

    public PriceLists getFromDB() {
        items = priceListsDB.getData();
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

        ArrayList<PriceList> itemsToSave = new ArrayList<>();
        itemsToSave.add(priceList);
        if (priceListsDB.putData(itemsToSave)) {
            items.addAll(itemsToSave);
            priceListsTable.getTableView().getItems().addAll(itemsToSave);
        }
    }

    public void editItem(PriceList priceList){
        ArrayList<PriceList> itemsToUpdate = new ArrayList<>();
        itemsToUpdate.add(priceList);
        if (priceListsDB.updateData(itemsToUpdate)) {

        }
    }

    public void deleteItem(PriceList priceList){
        if (priceListsDB.deleteData(priceList)) {
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

    public void setPriceListsDB(PriceListsDB priceListsDB) {
        this.priceListsDB = priceListsDB;
    }
}
