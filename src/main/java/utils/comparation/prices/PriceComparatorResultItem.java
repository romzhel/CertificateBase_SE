package utils.comparation.prices;

import ui_windows.product.Product;

import java.util.ArrayList;

public class PriceComparatorResultItem {
    private Product product;
    private ArrayList<PriceComparatorResultDetails> details;

    public PriceComparatorResultItem(Product product, int sheetIndex, String comment) {
        details = new ArrayList<>();
        details.add(new PriceComparatorResultDetails(sheetIndex, comment));
        this.product = product;
    }

    public void addItemDetails(PriceComparatorResultDetails pcrd) {
        details.add(pcrd);
    }

    public class PriceComparatorResultDetails {
        private int sheetIndex;
        private String comment;

        public PriceComparatorResultDetails(int sheetIndex, String comment) {
            this.sheetIndex = sheetIndex;
            this.comment = comment;
        }

        public int getSheetIndex() {
            return sheetIndex;
        }

        public String getComment() {
            return comment;
        }
    }

    public Product getProduct() {
        return product;
    }

    public ArrayList<PriceComparatorResultDetails> getDetails() {
        return details;
    }
}
