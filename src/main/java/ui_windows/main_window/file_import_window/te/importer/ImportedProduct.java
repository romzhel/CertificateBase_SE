package ui_windows.main_window.file_import_window.te.importer;

import lombok.Getter;
import org.apache.logging.log4j.util.Strings;
import ui_windows.product.data.DataItem;

import java.util.Map;

@Getter
public class ImportedProduct {
    private String id;
    private final Map<DataItem, ImportedProperty> properties;

    public ImportedProduct(Map<DataItem, ImportedProperty> properties) {
        this.properties = properties;
        id = properties.get(DataItem.DATA_ORDER_NUMBER).getValue().toString();
    }

    @Override
    public String toString() {
        return "ImportedProduct{" +
                "id='" + id + '\'' +
                ", properties=" + Strings.join(properties.values(), ',') +
                '}';
    }
}
