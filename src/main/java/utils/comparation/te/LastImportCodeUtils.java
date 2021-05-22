package utils.comparation.te;

import org.apache.logging.log4j.util.Strings;
import ui_windows.product.Product;
import ui_windows.product.Products;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LastImportCodeUtils {

    public String getChangesCodes(ChangedItem changedItem) {
        Product product = Products.getInstance().getProductByMaterial(changedItem.getId());

        String existingCodes = product.getLastImportcodes();
        Set<String> changedCodeSet = changedItem.getChangedPropertyList().stream()
                .map(property -> property.getDataItem().getField().getName())
                .collect(Collectors.toSet());

        if (!existingCodes.isEmpty()) {
            List<String> existingCodeList = Arrays.asList(existingCodes.split(","));
            changedCodeSet.addAll(existingCodeList);
        }

        return Strings.join(changedCodeSet, ',');
    }
}
