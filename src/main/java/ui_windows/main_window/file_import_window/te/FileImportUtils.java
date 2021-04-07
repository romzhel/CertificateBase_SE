package ui_windows.main_window.file_import_window.te;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

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
        return titles.stream()
                .filter(title -> !title.isEmpty())
                .map(title -> {
                    ImportColumnParameter param = new ImportColumnParameter();
                    param.setColumnTitle(title);
                    param.setDataItem(mappingService.getMappingForColumnTitle(title));
                    param.setColumnIndex(titles.indexOf(title));

                    return param;
                })
                .collect(Collectors.toList());
    }

    /*public Map<DataItem, Integer> getColumnMapping(List<ImportColumnParameter> params) {
        logger.debug(params);
        return params.stream()
                .filter(param -> param.getDataItem() != DATA_EMPTY)
                .collect(Collectors.toMap(ImportColumnParameter::getDataItem, ImportColumnParameter::getColumnIndex));
    }*/
}
