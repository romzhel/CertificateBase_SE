package utils.comparation.te;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import ui_windows.main_window.file_import_window.te.importer.ImportDataSheet;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.main_window.file_import_window.te.importer.ImportedProperty;
import utils.Utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
public class ProductHistoryBuilder {

    public String createHistoryForNewItem(ImportedProduct importedProduct) throws RuntimeException {
        return getDateTime() + ", added from " + getMainSource(importedProduct.getProperties().values());
    }

    public String createHistoryForChangedItem(ChangedItem changedItem) throws RuntimeException {
        ImportDataSheet mainSource = getMainSource(changedItem.getChangedPropertyList());
        List<String> changesDescriptionList = changedItem.getChangedPropertyList().stream()
                .map(changedProperty -> getChangeInfo(changedProperty, mainSource))
                .collect(Collectors.toList());
        return getDateTime() + "," + Strings.join(changesDescriptionList, ',') + ", [" + getSourceFileName(mainSource) + "]";
    }

    private String getDateTime() {
        return Utils.getDateTime();
    }

    private String getChangeInfo(ChangedProperty changedProperty, ImportDataSheet mainSource) {
        return String.format(" %s: %s -> %s%s",
                changedProperty.getDataItem().getField().getName(),
                changedProperty.getOldValue().toString(),
                changedProperty.getNewValue().toString(),
                changedProperty.getSource().equals(mainSource) ? "" : ", (" + getSourceFileName(changedProperty.getSource()) + ")"
        );
    }

    private String getSourceFileName(ImportDataSheet source) {
        Path sourcePath = Paths.get(source.getFileName());

        return sourcePath.getFileName().toString();
    }

    private ImportDataSheet getMainSource(Collection<? extends ImportedProperty> propertyList) throws RuntimeException {
        Map<ImportDataSheet, Integer> statisticMap = new HashMap<>();

        for (ImportedProperty property : propertyList) {
            statisticMap.merge(property.getSource(), 1, Integer::sum);
        }

        Map.Entry<ImportDataSheet, Integer> resultEntry = statisticMap.entrySet().stream()
                .max((o1, o2) -> o1.getValue().compareTo(o2.getValue()))
                .get();

        return resultEntry.getKey();
    }
}
