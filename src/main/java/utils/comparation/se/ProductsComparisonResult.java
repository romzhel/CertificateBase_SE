package utils.comparation.se;

import lombok.Getter;
import ui_windows.main_window.file_import_window.te.ImportColumnParameter;
import ui_windows.product.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ui_windows.product.data.DataItem.DATA_LOCAL_PRICE;

@Getter
public class ProductsComparisonResult extends ComparisonResult<Product> {
    private List<ObjectsComparatorResultSe<Product>> withoutNewPriceResult;

    public ProductsComparisonResult() {
        super();
        withoutNewPriceResult = new ArrayList<>();
    }

    public List<ObjectsComparatorResultSe<Product>> calcItemsWithoutNewPriceResult() {
        withoutNewPriceResult = goneItemsResult.stream()
                .filter(resultItem -> resultItem.getItem().getLocalPrice() > 0)
                .map(resultItem -> new ObjectsComparatorResultSe<>(resultItem.getItem(), resultItem.getItem()))
                .peek(resultItem -> {
                    resultItem.setItem_before(resultItem.getItem().clone());
                    resultItem.getItem().setLocalPrice(0.0);
                    resultItem.addChangedField(new ImportColumnParameter(DATA_LOCAL_PRICE));
                })
                .collect(Collectors.toList());
        return withoutNewPriceResult;
    }

    public List<Product> getItemsWithoutNewPrice() {
        if (withoutNewPriceResult.size() == 0) {
            calcItemsWithoutNewPriceResult();
        }
        return withoutNewPriceResult.stream()
                .map(ObjectsComparatorResultSe::getItem)
                .collect(Collectors.toList());
    }
}
