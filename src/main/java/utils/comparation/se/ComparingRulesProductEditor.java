package utils.comparation.se;

import ui_windows.main_window.file_import_window.te.ImportColumnParameter;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.product.Product;
import utils.comparation.te.ChangedProperty;

import java.util.List;

public class ComparingRulesProductEditor extends ProductComparingRulesTemplate implements ComparingRules<Product> {

    @Override
    public boolean isTheSameItem(Param<Product> params) {
        return false;
    }

    @Override
    public boolean isCanBeSkipped(Param<Product> params) {
        return false;
    }

    @Override
    public boolean isCanBeSkipped_v2(ChangedProperty changedProperty) {
        return false;
    }

    @Override
    public void addHistoryComment(ObjectsComparatorResultSe<Product> result) {
        super.addHistoryComment(result);
    }

    @Override
    public boolean addNewItem(Product item, List<ImportColumnParameter> fields) {
        return false;
    }

    @Override
    public boolean addNewItem_v2(ImportedProduct item) {
        return false;
    }
}
