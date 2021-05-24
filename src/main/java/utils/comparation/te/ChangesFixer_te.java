package utils.comparation.te;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui.Dialogs;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.main_window.file_import_window.te.importer.ImportedProperty;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.product_lgbk.ProductLgbks;
import ui_windows.product.Product;
import ui_windows.product.Products;
import ui_windows.product.data.DataItem;
import utils.Utils;
import utils.property_change_protect.ProductProtectChange;
import utils.property_change_protect.PropertyProtectChange;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ui_windows.product.data.DataItem.DATA_HIERARCHY;
import static ui_windows.product.data.DataItem.DATA_LGBK;
import static utils.property_change_protect.PropertyProtectChange.APPLY_PROTECT;

public class ChangesFixer_te {
    private static final Logger logger = LogManager.getLogger(ChangesFixer_te.class);
    private ProductHistoryBuilder historyBuilder = new ProductHistoryBuilder();
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
        Set<ProductLgbk> changedProductLgbk = new TreeSet<>((o1, o2) ->
                o1.getLgbk().concat(o1.getHierarchy()).compareToIgnoreCase(o2.getLgbk().concat(o2.getHierarchy())));
        Set<String> lgbkNames = new TreeSet<>(String::compareToIgnoreCase);

        ChangedItemToProductMerger merger = new ChangedItemToProductMerger();
        LastImportCodeUtils importCodeUtils = new LastImportCodeUtils();
        List<Product> result = new LinkedList<>();

        Matcher matcher;
        for (ChangedItem changedItem : changedItemList) {
            Product existProduct = Products.getInstance().getProductByMaterial(changedItem.getId());

            boolean hasHierarchyChanges = changedItem.getChangedPropertyList().stream()
                    .map(ImportedProperty::getDataItem)
                    .anyMatch(dataItem -> dataItem == DATA_LGBK || dataItem == DATA_HIERARCHY);

            if (hasHierarchyChanges) {
                Map<DataItem, ChangedProperty> propertyMap = changedItem.getChangedPropertyList().stream()
                        .collect(Collectors.toMap(
                                ImportedProperty::getDataItem,
                                property -> property
                        ));
                String oldLgbk = existProduct.getLgbk();
                String oldHierarchy = existProduct.getHierarchy();
                String newLgbk = propertyMap.get(DATA_LGBK) == null ? oldLgbk : propertyMap.get(DATA_LGBK).getNewValue().toString();
                String newHierarchy = propertyMap.get(DATA_HIERARCHY) == null ? oldHierarchy : propertyMap.get(DATA_HIERARCHY).toString();

                ProductLgbk plOld = ProductLgbks.getInstance().getLgbkByLgbk(new ProductLgbk(oldLgbk, oldHierarchy));
                ProductLgbk plNew = ProductLgbks.getInstance().getLgbkByLgbk(new ProductLgbk(newLgbk, newHierarchy));

                if (plNew == null && plOld != null) {
                    plNew = new ProductLgbk(newLgbk, newHierarchy);
                    try {
                        String hier = plNew.getHierarchy();
                        matcher = pattern.matcher(hier);
                        matcher.matches();
                        plNew.setHierarchy(matcher.group(2).concat("..."));

                        plNew.setFamilyId(plOld.getFamilyId());
                        changedProductLgbk.add(plNew);
                        lgbkNames.add(plNew.getLgbk());
//                            logger.debug("new ProductLgbk {} from {}", plNew, plOld);
                    } catch (Exception e) {
                        logger.error("Ошибка преобразования LGBK '{}' - {}", plNew.toString(), e.getMessage());
                    }
                }
            }

            merger.mergeToProduct(existProduct, changedItem);
            String history = historyBuilder.createHistoryForChangedItem(changedItem);
            existProduct.setHistory(existProduct.getHistory().isEmpty() ? history :
                    existProduct.getHistory().concat("|").concat(history));
            existProduct.setLastImportcodes(importCodeUtils.getChangesCodes(changedItem));
            existProduct.setLastChangeDate(Utils.getDateTime());
            result.add(existProduct);
        }

        for (String lgbk : lgbkNames) {
            if (ProductLgbks.getInstance().getGroupLgbkByName(lgbk) == null) {
                changedProductLgbk.add(new ProductLgbk(lgbk, "Все", ProductLgbk.GROUP_NODE));
            }
        }

        if (changedProductLgbk.size() > 0) {
            StringBuilder messageBuilder = new StringBuilder();
            for (ProductLgbk plgbk : changedProductLgbk) {
                messageBuilder.append(plgbk.toString()).append("\n");
            }
            Platform.runLater(() -> Dialogs.showMessage("Новые LGBK/Hierarchy",
                    "Обнаружены новые коды LGBK/Hierarchy:\n" + messageBuilder.toString()));
            ProductLgbks.getInstance().addItems(changedProductLgbk);
            logger.info("Обнаружены новые коды LGBK/Hierarchy: {}", messageBuilder.toString());
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
