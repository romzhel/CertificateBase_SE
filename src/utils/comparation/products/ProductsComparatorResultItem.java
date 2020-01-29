package utils.comparation.products;

import ui_windows.product.Product;

public class ProductsComparatorResultItem {
    private Product product;
    private String changeComment;

    public ProductsComparatorResultItem() {
    }

    public ProductsComparatorResultItem(Product product, String changeComment) {
        this.product = product;
        this.changeComment = changeComment;
    }

    public ProductsComparatorResultItem setProduct(Product product) {
        this.product = product;
        return this;
    }

    public ProductsComparatorResultItem setChangeComment(String comment) {
        this.changeComment = comment;
        return this;
    }

    public Product getProduct() {
        return product;
    }

    public String getChangeComment() {
        return changeComment;
    }
}
