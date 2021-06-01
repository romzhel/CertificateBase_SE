package utils.comparation.te;

import com.sun.istack.internal.NotNull;
import lombok.extern.log4j.Log4j2;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.product.data.DataItem;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
public class TotalPriceComparator {


    public TotalPriceComparisonResult compare(Collection<ImportedProduct> items1, Collection<ImportedProduct> items2) {
        log.debug("Start comparing prices");
        long t0 = System.currentTimeMillis();
        TotalPriceComparisonResult result = new TotalPriceComparisonResult();
        SingleComparator comparator = new SingleComparator();

        Map<String, ImportedProduct> leftItems = collectionToMap(items1);
        log.trace("Подготовка к сравнению завершена, прошло времени {} мс", System.currentTimeMillis() - t0);

        for (ImportedProduct item2 : items2) {
            ImportedProduct item1 = leftItems.remove(item2.getId());

            if (item1 != null) {
                ChangedItem changedItem = comparator.compare(item1, item2);

                if (changedItem.getChangedPropertyList().size() > 0) {
                    result.getChangedItemList().add(changedItem);
                }

                String item1Source = getSource(item1);
                String item2Source = getSource(item2);
                if (!item1Source.equals(item2Source)) {
                    ChangedValue<String> changedSource = new ChangedValue<>(item1Source, item2Source);
                    result.getChangedSourceMap().put(item2.getId(), changedSource);
                }
            } else {
                result.getNewItemList().add(item2);
            }
        }

        ImportedProductToProductMapper mapper = new ImportedProductToProductMapper();
        result.goneItemList.addAll(mapper.mapToProductList(leftItems.values()));

        log.trace("сравнение завершено, прошло времени {} мс", System.currentTimeMillis() - t0);

        return result;
    }

    private String getSource(@NotNull ImportedProduct item) {
        return item.getProperties().get(DataItem.DATA_ORDER_NUMBER).getSource().getSheetName();
    }

    private Map<String, ImportedProduct> collectionToMap(Collection<ImportedProduct> items) {
        return items.stream()
//                .peek(item -> logger.debug("add collection item to map '{}'", item))
                .collect(Collectors.toMap(
                        ImportedProduct::getId,
                        (item) -> item
                ));
    }
}
