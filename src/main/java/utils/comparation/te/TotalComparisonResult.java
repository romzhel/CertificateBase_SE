package utils.comparation.te;

import lombok.Getter;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.product.Product;
import utils.property_change_protect.ProductProtectChange;

import java.util.LinkedList;
import java.util.List;

@Getter
public class TotalComparisonResult {
    protected List<ImportedProduct> newItemList = new LinkedList<>();
    protected List<ChangedItem> changedItemList = new LinkedList<>();
    private List<Product> nonChangedItemList = new LinkedList<>();
    protected List<Product> goneItemList = new LinkedList<>();
    private List<ChangedItem> noCostItemList = new LinkedList<>();
    private List<ProductProtectChange> protectChangeItemList = new LinkedList<>();
    private List<ChangedItem> nonChangedProtectedItemList = new LinkedList<>();
}
