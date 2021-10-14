package ui_windows.main_window.file_import_window.te.importer;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import ui_windows.product.data.DataItem;
import ui_windows.product.vendors.VendorEnum;

import java.util.Map;

@Data
@NoArgsConstructor
public class ImportedProduct {
    private String id;
    private VendorEnum vendor;
    private Map<DataItem, ImportedProperty> properties;

    @Override
    public String toString() {
        return "ImportedProduct{" +
                "id='" + id + '\'' +
                "vendor='" + vendor.name() + '\'' +
                ", properties=" + Strings.join(properties.values(), ',') +
                '}';
    }
}
