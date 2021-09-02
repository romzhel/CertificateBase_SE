package ui_windows.main_window.file_import_window.te.mapper;

import lombok.extern.log4j.Log4j2;
import ui_windows.main_window.file_import_window.te.ImportColumnParameter;
import ui_windows.main_window.file_import_window.te.importer.ImportDataSheet;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.main_window.file_import_window.te.importer.ImportedProperty;
import ui_windows.main_window.file_import_window.te.importer.SaxRowData;
import ui_windows.product.data.DataItem;
import utils.PriceUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;

import static ui_windows.product.data.DataItem.DATA_EMPTY;
import static ui_windows.product.data.DataItem.DATA_ORDER_NUMBER;

@Log4j2
public class ExcelFileSaxRowDataToImportedProductMapper {

    public ImportedProduct getProductFromFileRecord(SaxRowData record, ImportDataSheet importDataSheet) throws RuntimeException {
        Map<DataItem, ImportedProperty> propertyMap = importDataSheet.getColumnParams().stream()
                .filter(param -> param.getDataItem() != DATA_EMPTY)
                .map(param -> {
                    ImportedProperty property = new ImportedProperty();
                    property.setDataItem(param.getDataItem());
                    property.setSource(importDataSheet);
                    property.setNewValue(getValue(record, param));
                    property.setParams(param.getOptions());
                    return property;
                })
                .filter(property -> property.getNewValue() != null && !property.getNewValue().toString().isEmpty())
                .collect(Collectors.toMap(
                        ImportedProperty::getDataItem,
                        (property) -> property)
                );

        return propertyMap.get(DATA_ORDER_NUMBER) != null && propertyMap.values().size() > 1 ? new ImportedProduct(propertyMap) : null;
    }

    private Object getValue(SaxRowData record, ImportColumnParameter param) throws RuntimeException {
        Object result = null;
        Field field = null;
        String value = null;
        try {
            field = param.getDataItem().getField();
            String fieldType = field.getType().getSimpleName();
            value = record.getCellValue(param.getColumnIndex());

            if (value == null) {
                return null;
            }

            if (fieldType.equals("String")) {
                result = value.replaceAll("00\\.00\\.0000", "");
            } else if (fieldType.equals("Integer")) {
                result = (int) getDoubleFromString(value);
            } else if (fieldType.equals("Double")) {
                result = getDoubleFromString(value);
            } else {
                throw new RuntimeException("unsupported field type for value " + value);
            }

            return result;
        } catch (Exception e) {
            log.error("error set Product field '{}' with value '{} => {}', {}", field, value, result, record);
            return null;
        }
    }

    private double getDoubleFromString(String text) throws RuntimeException {
        String initialValue = text;
        if (text == null || text.isEmpty() || text.equals("По запросу")) {
            return 0.0;
        }

        if (text.matches("\\d+\\.+\\d+[.,]+\\d+")) {
            text = text.replaceFirst("\\.", "");
        }

        try {
            text = text.replaceAll("\\,", ".");
            return PriceUtils.getFromString(text);
        } catch (Exception e) {
            throw new RuntimeException("error parsing do Double from " + text + " (" + initialValue + ")");
        }
    }
}
