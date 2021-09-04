package utils.comparation.te;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.product.Product;
import ui_windows.product.Products;
import utils.Utils;
import utils.property_change_protect.ProductProtectChange;
import utils.property_change_protect.PropertyProtectChange;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import static utils.property_change_protect.PropertyProtectChange.APPLY_PROTECT;

public class ChangesFixer_te {
    private static final Logger logger = LogManager.getLogger(ChangesFixer_te.class);
    private ProductHistoryBuilder historyBuilder;
    private Pattern pattern;

    public ChangesFixer_te() {
        historyBuilder = new ProductHistoryBuilder();
        pattern = Pattern.compile("(\\d)?([A-Z0-9]{3})(.*)?");
    }

    public List<Product> fixNewProducts(List<ImportedProduct> importedProductList) {
        ImportedProductToProductMapper mapper = new ImportedProductToProductMapper();
        List<Product> result = new LinkedList<>();

        for (ImportedProduct importedProduct : importedProductList) {
            String history = historyBuilder.createHistoryForNewItem(importedProduct);
            Product newProduct = mapper.mapToProduct(importedProduct);
            newProduct.setHistory(history);
            newProduct.setLastImportcodes("new");
            newProduct.setLastChangeDate(Utils.getDateTime());

            Products.getInstance().getItems().add(newProduct);
            result.add(newProduct);
        }
        Products.getInstance().initMap();

        return result;
    }

    public List<Product> fixChangedProducts(List<ChangedItem> changedItemList) {
        ChangedItemToProductMerger merger = new ChangedItemToProductMerger();
        LastImportCodeUtils importCodeUtils = new LastImportCodeUtils();
        List<Product> result = new LinkedList<>();

        for (ChangedItem changedItem : changedItemList) {
            Product existProduct = Products.getInstance().getProductByMaterial(changedItem.getId());
            merger.mergeToProduct(existProduct, changedItem);
            String history = historyBuilder.createHistoryForChangedItem(changedItem);
            existProduct.setHistory(existProduct.getHistory().isEmpty() ? history :
                    existProduct.getHistory().concat("|").concat(history));
            existProduct.setLastImportcodes(importCodeUtils.getChangesCodes(changedItem));
            existProduct.setLastChangeDate(Utils.getDateTime());
            result.add(existProduct);
        }

        return result;
    }

    public List<Product> fixPropertyProtectChanges(List<ProductProtectChange> protectChangeItemList) throws RuntimeException {
        List<Product> protectedProductChangesList = new LinkedList<>();

        for (ProductProtectChange protectProduct : protectChangeItemList) {
            Product product = Products.getInstance().getProductByMaterial(protectProduct.getId());

            for (PropertyProtectChange change : protectProduct.getPropertyProtectChangeList()) {
                if (change.getNewState() == APPLY_PROTECT) {
                    product.getProtectedData().add(change.getDataItem());
                } else {
                    product.getProtectedData().remove(change.getDataItem());
                }
            }

            protectedProductChangesList.add(product);
        }

        return protectedProductChangesList;
    }
}
