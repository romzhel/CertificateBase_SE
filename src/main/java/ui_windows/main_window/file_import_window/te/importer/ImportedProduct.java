package ui_windows.main_window.file_import_window.te.importer;

import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import ui_windows.product.data.DataItem;

import java.util.Map;

@Data
public class ImportedProduct {
    private String id;
    private Map<DataItem, ImportedProperty> properties;

    @Override
    public String toString() {
        return "ImportedProduct{" +
                "id='" + id + '\'' +
                ", properties=" + Strings.join(properties.values(), ',') +
                '}';
    }
}
