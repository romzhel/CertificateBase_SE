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
    protected int maxColIndex;

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

    protected void fillRow(int colIndex, ReportCell... cells) {
        SXSSFRow row = currentSheet.createRow(rowNum++);
        for (ReportCell reportCell : cells) {
            fillCell(row.createCell(colIndex), reportCell.getValue(), reportCell.getStyle());

            if (reportCell.getCombinedCellsCount() > 0) {
                currentSheet.addMergedRegion(
                        new CellRangeAddress(rowNum, rowNum, colIndex, colIndex + reportCell.getCombinedCellsCount()));
            }

            colIndex += 1 + reportCell.getCombinedCellsCount();
        }

        maxColIndex = Math.max(maxColIndex, colIndex);
    }

    protected void setColumnSize(int... widthList) {
        for (int i = 0; i < currentSheet.getRow(rowNum - 1).getLastCellNum() + 1; i++) {
            if (widthList.length == 0) {
                currentSheet.trackColumnForAutoSizing(i);
                currentSheet.autoSizeColumn(i);
                currentSheet.setColumnWidth(i, currentSheet.getColumnWidth(i) + 1000);
            } else if (i < widthList.length) {
                currentSheet.setColumnWidth(i, widthList[i]);
            }
        }
    }

    protected void decorateTitles() {
        currentSheet.createFreezePane(maxColIndex + 10, 1);
        currentSheet.setAutoFilter(new CellRangeAddress(0, Math.max(0, rowNum - 1), 0, Math.max(0, maxColIndex - 1)));
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
