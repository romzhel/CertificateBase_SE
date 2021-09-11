package ui_windows.main_window.file_import_window.te.importer;

import lombok.Data;
import ui_windows.main_window.file_import_window.te.ImportColumnParameter;
import ui_windows.product.vendors.VendorEnum;

import java.util.List;
import java.util.Map;

@Data
public class ImportDataSheet {
    private String fileName;
    private String sheetName;
    private List<ImportColumnParameter> columnParams;
    private Map<String, Boolean> sheetParams;
    private VendorEnum vendor;

    @Override
    public String toString() {
        return fileName + '\\' + sheetName;
    }
}
