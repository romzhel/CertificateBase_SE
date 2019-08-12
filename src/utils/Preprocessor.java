package utils;

import ui_windows.main_window.Product;

import java.util.ArrayList;
import java.util.HashSet;

public class Preprocessor {
    private ArrayList<Product> result;

    public Preprocessor(ArrayList<Product> items) {
        System.out.println("preprocessing...");

        ArrayList<String> orderNumbersS = new ArrayList<>();
        result = items;
        int originalItemsCount = items.size();

        HashSet<String> orderNumbers = new HashSet<>();
        for (Product pr : items) {
            orderNumbers.add(pr.getMaterial());
            orderNumbersS.add(pr.getMaterial());
        }

        if (items.size() == orderNumbers.size()) return;

        int index = 0;
        int firstPos;
        int lastPos;
        while (index < items.size()) {
            do {
                firstPos = orderNumbersS.indexOf(items.get(index).getMaterial());
                lastPos = orderNumbersS.lastIndexOf(items.get(index).getMaterial());

                if (firstPos != lastPos) {
                    orderNumbersS.remove(firstPos);
                    items.remove(firstPos);
                }

            } while (firstPos != lastPos);

            index++;
        }

        System.out.println("doubles were found, original size: " + originalItemsCount + ", treated size: " + items.size());
    }

    public ArrayList<Product> getTreatedItems() {
        return result;
    }
}
