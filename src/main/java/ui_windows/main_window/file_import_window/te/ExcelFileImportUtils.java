package ui_windows.main_window.file_import_window.te;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExcelFileImportUtils {
    private static final Logger logger = LogManager.getLogger(ExcelFileImportUtils.class);
    private static ExcelFileImportUtils instance;

    private ExcelFileImportUtils() {
    }

    public static ExcelFileImportUtils getInstance() {
        if (instance == null) {
            instance = new ExcelFileImportUtils();
        }
        return instance;
    }

    public List<ImportColumnParameter> getImportColumnParameters(Row row) {
        FileColumnMappingService mappingService = FileColumnMappingService.getInstance();
        Cell cell;

        final List<ImportColumnParameter> params = new ArrayList<>();
        for (int cellIndex = 0; cellIndex <= row.getLastCellNum(); cellIndex++) {
            if ((cell = row.getCell(cellIndex)) == null) {
                continue;
            }

            String title = cell.getStringCellValue().trim().toLowerCase();

            ImportColumnParameter param = new ImportColumnParameter();
            param.setColumnIndex(cellIndex);
            param.setColumnTitle(title);
            param.setDataItem(mappingService.getMappingForColumnTitle(title));

            params.add(param);
        }
        return params;
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
