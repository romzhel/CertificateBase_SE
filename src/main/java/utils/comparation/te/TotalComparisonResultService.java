package utils.comparation.te;

import lombok.extern.log4j.Log4j2;
import ui_windows.main_window.file_import_window.te.importer.ImportDataSheet;
import ui_windows.product.Product;
import ui_windows.product.Products;
import ui_windows.product.data.DataItem;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class TotalComparisonResultService {


    public List<ChangedItem> calcNoCostItemsInResult(TotalComparisonResult comparisonResult) {
        comparisonResult.getNoCostItemList().addAll(comparisonResult.getGoneItemList().stream()
                .filter(product -> product.getLocalPrice() > 0.0)
                .map(product -> {
                    ImportDataSheet importDataSheet = new ImportDataSheet();
                    importDataSheet.setFileName("");
                    importDataSheet.setSheetName("absent");

                    ChangedProperty changedProperty = new ChangedProperty();
                    changedProperty.setOldValue(product.getLocalPrice());
                    changedProperty.setNewValue(0.0);
                    changedProperty.setDataItem(DataItem.DATA_LOCAL_PRICE);
                    changedProperty.setSource(importDataSheet);

                    ChangedItem changedItem = new ChangedItem();
                    changedItem.setId(Products.getInstance().getVendorMaterial(product));
                    changedItem.getChangedPropertyList().add(changedProperty);

                    return changedItem;
                })
                .collect(Collectors.toList()));
        return comparisonResult.getNoCostItemList();
    }

    public List<Product> getChangedProductList(TotalComparisonResult comparisonResult) {
        ChangedItemToProductMerger merger = new ChangedItemToProductMerger();
        List<Product> changedProductList = mapToProductList(comparisonResult.getChangedItemList(), merger);
        List<Product> noCostProductList = mapToProductList(comparisonResult.getNoCostItemList(), merger);

        List<Product> result = new LinkedList<>(changedProductList);
        result.addAll(noCostProductList);

        return result;
    }

    private List<Product> mapToProductList(List<ChangedItem> list, ChangedItemToProductMerger merger) {
        return list.stream()
                .map(changedItem -> {
                    Product product = Products.getInstance().getProductByVendorMaterialId(changedItem.getId());
                    return merger.mergeToProduct(product, changedItem);
                })
                .collect(Collectors.toList());
    }
}
