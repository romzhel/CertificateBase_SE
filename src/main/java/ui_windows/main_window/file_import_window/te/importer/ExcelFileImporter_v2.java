package ui_windows.main_window.file_import_window.te.importer;

import core.ThreadManager;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import ui_windows.main_window.file_import_window.te.ColumnMappingWindow;
import ui_windows.main_window.file_import_window.te.ExcelFileImportUtils;
import ui_windows.main_window.file_import_window.te.ImportColumnParameter;
import ui_windows.main_window.file_import_window.te.mapper.ExcelFileSaxRowDataToImportedProductMapper;
import ui_windows.product.data.DataItem;
import ui_windows.product.vendors.VendorEnum;
import utils.BytesToReadableFormatConverter;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class ExcelFileImporter_v2 extends AbstractFileImporter {
    private List<ImportColumnParameter> params;
    private boolean isTitlesFound;
    private ExcelFileDataRecognizer dataRecognizer;
    private ImportDataSheet currentSheet;
    private ExcelFileSaxRowDataToImportedProductMapper saxRowDataMapper = new ExcelFileSaxRowDataToImportedProductMapper();
    private boolean manualMode;
    private boolean isImportFromNow;
    @Getter
    private List<String> sheetNames = new LinkedList<>();

    public ExcelFileImporter_v2() {
        super();
    }

    @Override
    public List<ImportedProduct> getProducts(List<File> files, boolean manualMode, boolean isImportFromNow) throws RuntimeException {
        this.manualMode = manualMode;
        this.isImportFromNow = isImportFromNow;
        conflictItemsPreprocessor.clearCash();

        try {
            dataRecognizer = new ExcelFileDataRecognizer();
            ExcelFileSaxImporter saxImporter = new ExcelFileSaxImporter();

            saxImporter.setImportDataSheetConsumer(this::processNextSheetData);
            saxImporter.getSaxRowDataParser().setRowDataConsumer(this::processSaxData);
            saxImporter.processFiles(files);

            logMemorySize("");

            return conflictItemsPreprocessor.processConflictsAndGetItems();
        } catch (Exception e) {
            log.error("Excel file import error: {} {}", e.getClass().getSimpleName(), e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void processNextSheetData(ImportDataSheet sheet) throws RuntimeException {
        log.debug("next sheet name {} / {}", sheet.getFileName(), sheet.getSheetName());
        if (!sheetNames.contains(sheet.getSheetName())) {
            sheetNames.add(sheet.getSheetName());
        }
        currentSheet = sheet;
        isTitlesFound = false;
    }

    private void processSaxData(SaxRowData saxRowData) throws RuntimeException {
        if (isTitlesFound) {
            ImportedProduct importedItem = saxRowDataMapper.getProductFromFileRecord(saxRowData, currentSheet, isImportFromNow);
            if (importedItem != null) {
                conflictItemsPreprocessor.process(importedItem, isImportFromNow);
            }
        } else {
            if (dataRecognizer.isRowContainsTitles(saxRowData)) {
                params = ExcelFileImportUtils.getInstance().getImportColumnParams(Arrays.asList(saxRowData.getData()));
                currentSheet.setColumnParams(params);
                currentSheet.setVendor(VendorEnum.SIEMENS);

                if (manualMode) {
                    ThreadManager.executeFxTaskSafe(() -> new ColumnMappingWindow(currentSheet).getResult());
                }

                List<ImportColumnParameter> actualParams = params.stream()
                        .filter(par -> par.getDataItem() != DataItem.DATA_EMPTY)
                        .collect(Collectors.toList());
                log.debug("column mapping params for mapping {}", actualParams);

                currentSheet.setColumnParams(actualParams);
                log.debug("sheet params = {}", currentSheet);
                isTitlesFound = true;
            }
        }
    }

    private void logMemorySize(String comment) {
        long freeMem = Runtime.getRuntime().freeMemory();
        long totalMem = Runtime.getRuntime().totalMemory();
        long maxMem = Runtime.getRuntime().maxMemory();
        int availableProcessors = Runtime.getRuntime().availableProcessors();

        BytesToReadableFormatConverter converter = new BytesToReadableFormatConverter();
        log.debug("{}, system memory: free {}, total {}, max {}", comment,
                converter.convert(freeMem), converter.convert(totalMem), converter.convert(maxMem));
    }
}
