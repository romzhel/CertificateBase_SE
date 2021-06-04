package ui_windows.main_window.file_import_window.te.importer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ui_windows.main_window.file_import_window.te.FileColumnMappingService;
import ui_windows.product.data.DataItem;


public class ExcelFileDataRecognizer {


    public int getTitleRowIndex(Sheet sheet) throws RuntimeException {
        int resultRowIndex = 0;

        while (!isRowContainsTitles(resultRowIndex, sheet)) {
            resultRowIndex++;

            if (resultRowIndex > 25) {
                throw new RuntimeException("Title row not found");
            }
        }

        return resultRowIndex;
    }

    private boolean isRowContainsTitles(int resultRowIndex, Sheet sheet) {
        FileColumnMappingService mappingService = FileColumnMappingService.getInstance();
        Row row = sheet.getRow(resultRowIndex);

        Cell cell;
        for (int colIndex = 0; colIndex <= row.getLastCellNum(); colIndex++) {
            if ((cell = row.getCell(colIndex)) != null) {
                String cellValue = cell.toString();
                if (mappingService.getMappingForColumnTitle(cellValue) == DataItem.DATA_ORDER_NUMBER) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isRowContainsTitles(SaxRowData rowData) {
        FileColumnMappingService mappingService = FileColumnMappingService.getInstance();

        for (int colIndex = 0; colIndex <= rowData.getSize(); colIndex++) {
            String cellValue = rowData.getCellValue(colIndex);

            if (cellValue == null) {
                continue;
            }

            if (mappingService.getMappingForColumnTitle(cellValue) == DataItem.DATA_ORDER_NUMBER) {
                return true;
            }
        }

        return false;
    }
}
