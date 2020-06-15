package ui_windows.options_window.price_lists_editor.se.price_sheet;

import database.PriceListSheetDB;

import java.util.ArrayList;

public class PriceListSheets {
    private ArrayList<PriceListSheet> sheets;

    public PriceListSheets() {
    }

    public PriceListSheets getFromDB() {
        sheets = new PriceListSheetDB().getData();
        return this;
    }

    public ArrayList<PriceListSheet> getPriceListSheets(int priceListId) {
        ArrayList<PriceListSheet> result = new ArrayList<>();
        for (PriceListSheet sheet : sheets) {
            if (sheet.getPriceListId() == priceListId) {
                result.add(sheet);
            }
        }
        return result;
    }


}
