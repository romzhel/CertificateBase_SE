package utils.comparation.prices;

import java.util.ArrayList;

public class PriceComparatorResult {
    private ArrayList<PriceComparatorResultItem> result;
    private ArrayList<String> sheetNames;

    public PriceComparatorResult() {
        result = new ArrayList<>();
        sheetNames = new ArrayList<>();
    }

    public void addItem(PriceComparatorResultItem pcri) {
        boolean isNew = true;
        for (PriceComparatorResultItem pcrit : result) {
            if (pcrit.getProduct().getMaterial().equals(pcri.getProduct().getMaterial())) {
                pcrit.getDetails().add(pcri.getDetails().get(0));
                isNew = false;
            }
        }

        if (isNew) {
            result.add(pcri);
        }
    }

    public ArrayList<PriceComparatorResultItem> getItems() {
        return result;
    }

    public ArrayList<String> getSheetNames() {
        return sheetNames;
    }

    public void addSheetName(String sheetName) {
        sheetNames.add(sheetName);
    }
}
