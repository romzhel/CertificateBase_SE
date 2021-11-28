package files.reports;

import core.ThreadManager;
import exceptions.OperationCancelledByUserException;
import files.ExcelCellStyleFactory_v2;
import javafx.application.Platform;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import ui.Dialogs;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;

@Log4j2
public abstract class ReportToExcelTemplate_v3<T> implements Runnable {
    protected SXSSFWorkbook workbook;
    protected SXSSFSheet currentSheet;
    protected ExcelCellStyleFactory_v2 styles;
    protected T data;
    protected Path reportFilePath;
    protected File reportFile;
    protected int rowNum;
    protected int colIndex;

    public ReportToExcelTemplate_v3(T data, Path reportFilePath) {
        this.data = data;
        this.reportFilePath = reportFilePath;
        workbook = new SXSSFWorkbook(1000);
        workbook.setCompressTempFiles(true);
        styles = new ExcelCellStyleFactory_v2(workbook);
    }

    protected void getAndCheckParams() throws RuntimeException {
        if (data == null) {
            throw new IllegalArgumentException("Data parameters are empty");
        }
    }

    protected void confirmAndCheckReportFile(String dialogTitle) throws RuntimeException {
        reportFile = ThreadManager.executeFxTaskSafe(() -> new Dialogs().selectSavePath(dialogTitle, reportFilePath));
        if (reportFile == null) {
            log.warn("Illegal params: reportFile='{}'", reportFile);
            throw new OperationCancelledByUserException();
        }
    }

    protected void fillRow(boolean autoSize, ReportCell... cells) {
        SXSSFRow row = currentSheet.createRow(rowNum++);
        colIndex = 0;
        for (ReportCell reportCell : cells) {
            fillCell(row.createCell(colIndex), reportCell.getValue(), reportCell.getStyle());
            if (autoSize) {
                currentSheet.trackColumnForAutoSizing(colIndex);
                currentSheet.autoSizeColumn(colIndex);
                currentSheet.setColumnWidth(colIndex, currentSheet.getColumnWidth(colIndex) + 1000);
            }

            if (reportCell.getCombinedCellsCount() > 0) {
                currentSheet.addMergedRegion(
                        new CellRangeAddress(rowNum, rowNum, colIndex, colIndex + reportCell.getCombinedCellsCount()));
            }

            colIndex += 1 + reportCell.getCombinedCellsCount();
        }
    }

    protected void autoSizeColumns() {
        for (int i = 0; i < currentSheet.getRow(rowNum - 1).getLastCellNum() + 1; i++) {
            currentSheet.autoSizeColumn(i);
        }
    }

    protected void fillCell(SXSSFCell cell, Object value, CellStyle style) {
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

    protected void saveToFile() throws RuntimeException {
        try {
            FileOutputStream outFile = new FileOutputStream(reportFile);
            workbook.write(outFile);

            outFile.close();
            workbook.dispose();

            log.info("Created file: {}", reportFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("error of excel file creating {}", e.getMessage());
            Platform.runLater(() -> Dialogs.showMessage("Ошибка создания файла отчета", e.getMessage()));
            throw new RuntimeException(e);
        }
    }
}