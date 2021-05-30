package utils.comparation.te;

import lombok.Getter;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.product.Product;
import utils.property_change_protect.ProductProtectChange;

import java.util.LinkedList;
import java.util.List;

@Getter
public class TotalComparisonResult {
    private List<ImportedProduct> newItemList = new LinkedList<>();
    private List<ChangedItem> changedItemList = new LinkedList<>();
    private List<Product> nonChangedItemList = new LinkedList<>();
    private List<Product> goneItemList = new LinkedList<>();
    private List<ChangedItem> noCostItemList = new LinkedList<>();
    private List<ProductProtectChange> protectChangeItemList = new LinkedList<>();
    private List<ChangedItem> nonChangedProtectedItemList = new LinkedList<>();
}
