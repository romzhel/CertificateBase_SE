package ui_windows.main_window.file_import_window.te;

import javafx.beans.property.SimpleBooleanProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.product.data.DataItem;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ui_windows.main_window.file_import_window.te.FilesImportParametersEnum.FOR_IMPORT;
import static ui_windows.main_window.file_import_window.te.FilesImportParametersEnum.FOR_LOGGING;
import static ui_windows.product.data.DataItem.DATA_EMPTY;

public class FileImportUtils {
    private static final Logger logger = LogManager.getLogger(FileImportUtils.class);
    private static FileImportUtils instance;

    private FileImportUtils() {
    }

    public static FileImportUtils getInstance() {
        if (instance == null) {
            instance = new FileImportUtils();
        }
        return instance;
    }

    public List<ImportColumnParameter> getImportColumnParams(List<String> titles) {
        FileColumnMappingService mappingService = FileColumnMappingService.getInstance();
        Map<String, DataItem> columnMapping = mappingService.getMappingByColumnTitles(titles);
        return titles.stream()
                .filter(title -> !title.isEmpty())
                .map(title -> {
                    ImportColumnParameter param = new ImportColumnParameter();
                    param.setColumnTitle(title);
                    param.setDataItem(columnMapping.getOrDefault(title, DATA_EMPTY));
                    param.setColumnIndex(titles.indexOf(title));
                    param.getOptions().put(FOR_IMPORT, new SimpleBooleanProperty(param.getDataItem() != DATA_EMPTY));
                    param.getOptions().put(FOR_LOGGING, new SimpleBooleanProperty(param.getDataItem() != DATA_EMPTY));

                    return param;
                })
                .collect(Collectors.toList());
    }

    public Map<DataItem, Integer> getColumnMapping(List<ImportColumnParameter> params) {
        logger.debug(params);
        return params.stream()
                .filter(param -> param.getDataItem() != DATA_EMPTY)
                .collect(Collectors.toMap(ImportColumnParameter::getDataItem, ImportColumnParameter::getColumnIndex));
    }
}
