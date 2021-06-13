package files.reports;

import files.ExcelCellStyleFactory;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ui.Dialogs;
import ui_windows.main_window.MainWindow;
import utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.Callable;

@Log4j2
public abstract class ReportToExcelTemplate implements Callable<File> {
    protected File reportFile;
    protected Workbook workbook;
    protected ExcelCellStyleFactory styles;

    public ReportToExcelTemplate() {
        workbook = new XSSFWorkbook();
        styles = new ExcelCellStyleFactory();
        styles.init(workbook);
    }

    protected File saveToExcelFile() throws RuntimeException {
        final String fileExtension = workbook instanceof HSSFWorkbook ? ".xls" : ".xlsx";

        reportFile = null;
        reportFile = new Dialogs().selectAnyFileTS(MainWindow.getMainStage(), "Выбор места сохранения",
                Dialogs.EXCEL_FILES_ALL, "report_" + Utils.getDateTime().replaceAll(":", "-")
                        + fileExtension).get(0);

        if (reportFile == null) {
            throw new RuntimeException("При сохранении не выбрано имя файла");
        }

        try {
            FileOutputStream outFile = new FileOutputStream(reportFile);
            workbook.write(outFile);

            workbook.close();
            outFile.close();

            log.info("Created file: {}", reportFile.getAbsolutePath());

            return reportFile;
        } catch (Exception e) {
            log.error("error of excel file creating {}", e.getMessage());
            Dialogs.showMessage("Ошибка создания файла", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public File getReportFile() {
        return reportFile;
    }

    @Override
    public File call() throws Exception {
        return reportFile;
    }
}
