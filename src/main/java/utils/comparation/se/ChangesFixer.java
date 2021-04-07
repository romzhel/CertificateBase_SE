package utils.comparation.se;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.main_window.file_import_window.te.ImportColumnParameter;
import ui_windows.product.Product;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static ui_windows.main_window.file_import_window.te.FilesImportParametersEnum.BLOCK_PROPERTY;

public class ChangesFixer {
    private static final Logger logger = LogManager.getLogger(ChangesFixer.class);

    public boolean fixChanges(ObjectsComparatorResultSe<Product> ocr) {
        Set<ImportColumnParameter> paramsForRemove = new HashSet<>();

        int fixedCount = 0;
        for (ImportColumnParameter parameter : ocr.getChangedFields()) {
            if (ocr.getItem().getProtectedData().contains(parameter.getDataItem())) {
                logger.info("Field '{}' was not changed due change protection", parameter.getDataItem().getField());
                paramsForRemove.add(parameter);
                continue;
            }

            Field field = parameter.getDataItem().getField();
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(ocr.getItem_after());
                field.set(ocr.getItem(), value);

                boolean forBlock = parameter.getOptions().getOrDefault(BLOCK_PROPERTY, false);
                if (forBlock) {
                    ocr.getItem().getProtectedData().add(parameter.getDataItem());
                }

                fixedCount++;
            } catch (IllegalAccessException e) {
                logger.error("Error setting value '{}' for field '{}'", value, field.getName());
            }
        }

        ocr.getChangedFields().removeAll(paramsForRemove);
        return fixedCount == ocr.getChangedFields().size();
    }
}
