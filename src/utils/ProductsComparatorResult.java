package utils;

import ui_windows.main_window.Product;

import java.util.ArrayList;

public class ProductsComparatorResult {
    ArrayList<Product> changedItems;
    ArrayList<Product> newItems;
    ArrayList<Product> goneItems;

    public ProductsComparatorResult() {
        changedItems = new ArrayList<>();
        newItems = new ArrayList<>();
        goneItems = new ArrayList<>();
    }

    public ArrayList<Product> getChangedItems() {
        return changedItems;
    }

    public ArrayList<Product> getNewItems() {
        return newItems;
    }

    public ArrayList<Product> getGoneItems() {
        return goneItems;
    }

    public void clear(){
        changedItems.clear();
        newItems.clear();
        goneItems.clear();
    }
}
