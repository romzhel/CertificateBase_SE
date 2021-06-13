package utils.comparation.te;

import lombok.Getter;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
public class TotalPriceComparisonResult {
    private List<ImportedProduct> newItemList = new LinkedList<>();
    private List<ChangedItem> changedItemList = new LinkedList<>();
    private Map<String, ChangedValue<String>> changedSourceMap = new HashMap<>();
    private List<String> sheetNames = new LinkedList<>();
    private List<ImportedProduct> goneItemList = new LinkedList<>();
}
