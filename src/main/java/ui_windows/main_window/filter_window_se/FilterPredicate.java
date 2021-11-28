package ui_windows.main_window.filter_window_se;

import ui_windows.product.Product;

import java.util.function.Predicate;

public class FilterPredicate {
    private FilterPredicate other;
    private Predicate<Product> predicate;

    public FilterPredicate(Predicate<Product> predicate) {
        this.predicate = predicate;
    }

    public FilterPredicate addPredicate(FilterPredicate other) {
        if (this.other == null) {
            this.other = other;
        } else {
            this.other.addPredicate(other);
        }
        return this;
    }

    public boolean check(Product product) {
        return other == null ? predicate.test(product) : predicate.test(product) && other.check(product);
    }
}
