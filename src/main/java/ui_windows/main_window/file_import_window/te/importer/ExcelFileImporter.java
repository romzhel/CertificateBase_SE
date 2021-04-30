package ui_windows.main_window.file_import_window.te.importer;

import core.ThreadManager;
import org.apache.poi.ss.usermodel.*;
import ui_windows.main_window.file_import_window.te.ColumnMappingWindow;
import ui_windows.main_window.file_import_window.te.ExcelFileImportUtils;
import ui_windows.main_window.file_import_window.te.ImportColumnParameter;
import ui_windows.main_window.file_import_window.te.mapper.ExcelFileRecordToProductMapper;
import ui_windows.product.data.DataItem;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ExcelFileImporter extends AbstractFileImporter {

    @Override
    public List<ImportedProduct> getProducts(List<File> files, boolean manualMode) throws RuntimeException {
        ExcelFileRecordToProductMapper mapper = new ExcelFileRecordToProductMapper();
        Workbook workbook = null;
        Cell cell;

        conflictItemsPreprocessor.clearCash();

        for (File file : files) {
            logger.info("opening file '{}'", file);
            try {
                workbook = WorkbookFactory.create(file);

                for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                    Sheet sheet = workbook.getSheetAt(sheetIndex);
                    logger.info("treat sheet '{}'", sheet.getSheetName());

                    Row row = sheet.getRow(0);

                    final List<ImportColumnParameter> params = ExcelFileImportUtils.getInstance().getImportColumnParameters(row);
                    logger.debug("column mapping params {}", params);

                    final ImportDataSheet importDataSheet = new ImportDataSheet();
                    importDataSheet.setFileName(file.getName());
                    importDataSheet.setSheetName(sheet.getSheetName());
                    importDataSheet.setColumnParams(params);

                    if (manualMode) {
                        ThreadManager.executeFxTaskSafe(() -> new ColumnMappingWindow(importDataSheet).getResult());
                    }

                    List<ImportColumnParameter> actualParams = params.stream()
                            .filter(par -> par.getDataItem() != DataItem.DATA_EMPTY)
                            .collect(Collectors.toList());
                    logger.debug("column mapping params for mapping {}", actualParams);
                    importDataSheet.setColumnParams(actualParams);
                    logger.debug("import sheet data {}", importDataSheet);

                    for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                        if ((row = sheet.getRow(rowIndex)) != null) {
                            ImportedProduct importedItem = mapper.getProductFromFileRecord(row, importDataSheet);
                            conflictItemsPreprocessor.process(importedItem);
                        }
                    }
                }
            } catch (Exception e) {
                closeWorkbook(workbook);
                throw new RuntimeException(e);
            }
        }

        return conflictItemsPreprocessor.processConflictsAndGetItems();
    }

    public void closeWorkbook(Workbook workbook) {
        try {
            workbook.close();
        } catch (Exception e) {

        }
    }
}
