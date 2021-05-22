package utils.comparation.se;

import javafx.util.Callback;
import ui_windows.main_window.file_import_window.te.ImportColumnParameter;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.product.Product;
import utils.comparation.te.ChangedProperty;

import java.util.List;

public class ComparingRulesPricesComparison extends ProductComparingRulesTemplate implements ComparingRules<Product> {

    public ComparingRulesPricesComparison() {
    }

    public ComparingRulesPricesComparison(Callback<Param<Product>, Param<Product>> customComparisonRule) {
        super(customComparisonRule);
    }

    @Override
    public boolean isCanBeSkipped_v2(ChangedProperty changedProperty) {
        return false;
    }

    @Override
    public boolean addNewItem(Product item, List<ImportColumnParameter> fields) {
        return true;
    }

    @Override
    public boolean addNewItem_v2(ImportedProduct item) {
        return true;
    }
}
