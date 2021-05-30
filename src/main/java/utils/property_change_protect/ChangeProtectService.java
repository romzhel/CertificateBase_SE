package utils.property_change_protect;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.main_window.file_import_window.te.importer.ImportedProperty;
import ui_windows.product.data.DataItem;
import utils.comparation.te.PropertyProtectService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static utils.property_change_protect.PropertyProtectChange.APPLY_PROTECT;

@Log4j2
public class ChangeProtectService {

    public ProductProtectChange checkProtectChangesAndGetResult(ImportedProduct importedItem) {
        PropertyProtectService propertyProtectService = new PropertyProtectService();
        ProductProtectChange productProtectChange = new ProductProtectChange();

        for (ImportedProperty property : importedItem.getProperties().values()) {
            boolean isPropertySetToProtect = propertyProtectService.isPropertyNeedToProtect(property);
            if (isPropertySetToProtect) {
                PropertyProtectChange propertyProtectChange = new PropertyProtectChange();
                propertyProtectChange.setDataItem(property.getDataItem());
                propertyProtectChange.setNewState(APPLY_PROTECT);

                productProtectChange.setId(importedItem.getId());
                productProtectChange.getPropertyProtectChangeList().add(propertyProtectChange);

                log.info("item property protect {}: {} = {}",
                        importedItem.getId(),
                        property.getDataItem(),
                        propertyProtectChange.getNewState()
                );
            }
        }

        return productProtectChange;
    }

    public Set<DataItem> mapStringToSet(String text) {
        Set<DataItem> result = new HashSet<>();
        if (text != null && !text.isEmpty()) {
            result.addAll(Arrays.stream(text.split(","))
                    .map(DataItem::valueOf)
                    .collect(Collectors.toList())
            );
        }
        return result;
    }

    public String mapSetToString(Set<DataItem> dataItemSet) {
        return Strings.join(dataItemSet
                .stream().map(Enum::name)
                .collect(Collectors.toList()), ',');
    }
}
