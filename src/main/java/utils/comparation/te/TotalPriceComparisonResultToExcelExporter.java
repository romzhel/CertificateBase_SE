package utils.comparation.te;

import files.reports.ReportParameterEnum;
import files.reports.ReportToExcelTemplate_v2;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.product.Product;
import ui_windows.product.Products;
import ui_windows.product.data.DataItem;

import java.util.List;
import java.util.Map;

import static files.reports.ReportParameterEnum.PRICE_COMPARISON_REPORT_PATH;
import static files.reports.ReportParameterEnum.PRICE_COMPARISON_RESULT;
import static ui_windows.product.data.DataItem.*;

@Log4j2
public class TotalPriceComparisonResultToExcelExporter extends ReportToExcelTemplate_v2 {
    private final DataItem[] values = {DATA_FAMILY_NAME, DATA_ORDER_NUMBER, DATA_ARTICLE, DATA_DESCRIPTION};
    private final String[] sheetTitles = {"Тип изменения", "Изменённое свойство", "Исходное значение", "    ", "Новое значение"};
    private final int[] colWidths = {8200, 6150, 6150, 6150, 4346, 7175, 5576, 861, 5576, 4346, 7175, 5576, 861, 5576, 4346, 7175, 5576, 861, 5576};
    private final CellStyle[] sheetTitlesStyles = {styles.CELL_ALIGN_HLEFT_BOLD, styles.CELL_ALIGN_HLEFT_BOLD_BROWN, styles.CELL_ALIGN_HLEFT_BOLD};
    private final CellStyle[] itemDataStyles = {styles.CELL_ALIGN_HLEFT, styles.CELL_ALIGN_HLEFT_BROWN, styles.CELL_ALIGN_HLEFT};
    private TotalPriceComparisonResult comparisonResult;
    private int rowNum;
    private int colIndex;

    public TotalPriceComparisonResultToExcelExporter(Map<ReportParameterEnum, Object> params) {
        super(params);
    }

    @Override
    protected void getAndCheckParams() throws RuntimeException {
        super.getAndCheckParams();

        confirmAndCheckReportFile(PRICE_COMPARISON_REPORT_PATH, "Сохранение отчета сравнения прайсов");
        comparisonResult = (TotalPriceComparisonResult) params.get(PRICE_COMPARISON_RESULT);
        if (comparisonResult == null) {
            log.warn("Illegal params: comparison result = null");
            throw new IllegalArgumentException("Param 'PRICE_COMPARISON_RESULT' not found");
        }
    }

    public void export() throws RuntimeException {
        log.trace("Checking params...");
        getAndCheckParams();

        SXSSFSheet sheet = workbook.createSheet("Price-lists comparison report");

        log.trace("Filling report titles");
        fillTitles(sheet);

        log.trace("Filling report data");
        fillItemsData(sheet, comparisonResult.getNewItemList(), "добавлена");
        fillChangedItemsData(sheet);
        fillItemsData(sheet, comparisonResult.getGoneItemList(), "удалена");

        saveToFile();
    }

    private void fillChangedItemsData(SXSSFSheet sheet) {
        for (ChangedItem item : comparisonResult.getChangedItemList()) {
            Product changedItem = Products.getInstance().getProductByMaterial(item.getId());
            ChangedValue<String> changedSource = comparisonResult.getChangedSourceMap().get(item.getId());

            for (ChangedProperty property : item.getChangedPropertyList()) {
                String initialSheetName = changedSource != null ? changedSource.getOldValue() : property.getSource().getSheetName();
                String currentSheetName = changedSource != null ? changedSource.getNewValue() : property.getSource().getSheetName();

                int initialSheetIndex = comparisonResult.getSheetNames().indexOf(initialSheetName);
                int currentSheetIndex = comparisonResult.getSheetNames().indexOf(currentSheetName);

                if (initialSheetIndex != currentSheetIndex &&
                        (property.getDataItem() == DATA_LEAD_TIME_RU || property.getDataItem() == DATA_WARRANTY)) {
                    continue;
                }

                Row row = sheet.createRow(rowNum++);

                CellStyle cellStyle = itemDataStyles[currentSheetIndex];
                fillProductData(changedItem, row, cellStyle);

                String direction = initialSheetIndex <= currentSheetIndex ? "->" : "<-";

                fillCell(row.createCell(colIndex++ + initialSheetIndex * 5), "изменена", cellStyle);
                fillCell(row.createCell(colIndex++ + initialSheetIndex * 5), property.getDataItem().getDisplayingName(), cellStyle);
                fillCell(row.createCell(colIndex++ + initialSheetIndex * 5), property.getOldValue(), cellStyle);
                fillCell(row.createCell(colIndex + initialSheetIndex * 5), direction, styles.CELL_ALIGN_HCENTER);
                fillCell(row.createCell(colIndex++ + currentSheetIndex * 5), direction, styles.CELL_ALIGN_HCENTER);
                fillCell(row.createCell(colIndex++ + currentSheetIndex * 5), property.getNewValue(), cellStyle);
            }
        }
    }

    private void fillItemsData(SXSSFSheet sheet, List<ImportedProduct> newItemList, String comment) {
        for (ImportedProduct item : newItemList) {
            Product product = Products.getInstance().getProductByMaterial(item.getId());

            Row row = sheet.createRow(rowNum++);

            String itemSheetName = item.getProperties().get(DATA_ORDER_NUMBER).getSource().getSheetName();
            int itemSheetIndex = comparisonResult.getSheetNames().indexOf(itemSheetName);
            fillProductData(product, row, itemDataStyles[itemSheetIndex]);


            colIndex = values.length + itemSheetIndex * sheetTitles.length;

            fillCell(row.createCell(colIndex), comment, itemDataStyles[itemSheetIndex]);
        }
    }

    private void fillProductData(Product changedItem, Row row, CellStyle cellStyle) {
        colIndex = 0;
        for (DataItem dataItem : values) {
            fillCell(row.createCell(colIndex++), dataItem.getValue(changedItem), cellStyle);
        }

        fillCell(row.createCell(colIndex), " ", styles.CELL_ALIGN_HLEFT);
    }

    private void fillTitles(SXSSFSheet sheet) throws RuntimeException {
        Row row0 = sheet.createRow(rowNum++);
        Row row1 = sheet.createRow(rowNum++);

        for (String sheetName : comparisonResult.getSheetNames()) {
            int sheetIndex = comparisonResult.getSheetNames().indexOf(sheetName);
            int colFrom = values.length + sheetIndex * sheetTitles.length;
            int colTo = values.length + (sheetIndex + 1) * sheetTitles.length - 1;

            fillCell(row0.createCell(colFrom), sheetName, sheetTitlesStyles[sheetIndex]);
            sheet.addMergedRegion(new CellRangeAddress(row0.getRowNum(), row0.getRowNum(), colFrom, colTo));

            colIndex = colFrom;
            for (String title : sheetTitles) {
                fillTitlesPart(sheet, row1, title, sheetTitlesStyles[sheetIndex]);
            }
        }

        colIndex = 0;
        for (DataItem di : values) {
            fillTitlesPart(sheet, row1, di.getDisplayingName(), styles.CELL_ALIGN_HLEFT_BOLD);
        }

        sheet.createFreezePane(0, 2);
        sheet.setAutoFilter(new CellRangeAddress(1, 1, 0, colWidths.length - 1));
    }

    private void fillTitlesPart(SXSSFSheet sheet, Row row, String title, CellStyle cellStyle) {
        fillCell(row.createCell(colIndex++), title, cellStyle);
//        sheet.trackColumnForAutoSizing(colIndex - 1);
//        sheet.autoSizeColumn(colIndex - 1);
        sheet.setColumnWidth(colIndex - 1, colWidths[colIndex - 1]);
    }

    private void fillCell(Cell cell, Object value, CellStyle style) {
        if (value == null) {
            return;
        }

        cell.setCellStyle(style);
        if (value instanceof Integer) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((int) value);
        } else if (value instanceof Double) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((double) value);
        } else if (value instanceof Long) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((long) value);
        } else {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(value.toString());
        }
    }
}

