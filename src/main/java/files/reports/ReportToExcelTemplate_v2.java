package files.reports;

import core.ThreadManager;
import exceptions.OperationCancelledByUserException;
import files.ExcelCellStyleFactory_v2;
import javafx.application.Platform;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import ui.Dialogs;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Map;

@Log4j2
public abstract class ReportToExcelTemplate_v2 {
    protected Map<ReportParameterEnum, Object> params;
    protected SXSSFWorkbook workbook;
    protected ExcelCellStyleFactory_v2 styles;
    protected File reportFile;
    protected int rowNum;
    protected int colIndex;

    public ReportToExcelTemplate_v2(Map<ReportParameterEnum, Object> params) {
        this.params = params;
        workbook = new SXSSFWorkbook(1000);
        workbook.setCompressTempFiles(true);
        styles = new ExcelCellStyleFactory_v2(workbook);
    }

    protected void getAndCheckParams() throws RuntimeException {
        if (params == null) {
            throw new IllegalArgumentException("Parameters are empty");
        }
    }

    protected void confirmAndCheckReportFile(ReportParameterEnum paramEnum, String dialogTitle) throws RuntimeException {
        Path reportPath = (Path) params.get(paramEnum);
        reportFile = ThreadManager.executeFxTaskSafe(() -> new Dialogs().selectSavePath(dialogTitle, reportPath));
        if (reportFile == null) {
            log.warn("Illegal params: reportFile='{}'", reportPath);
            throw new OperationCancelledByUserException();
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

    protected void fillCell(Cell cell, Object value, CellStyle style) {
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
