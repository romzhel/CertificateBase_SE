package files.reports;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import ui.Dialogs;
import ui_windows.main_window.MainWindow;
import utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.Callable;

public abstract class ReportToExcelTemplate implements Callable<File> {
    protected File reportFile;
    protected Workbook workbook;

    protected File saveToExcelFile() {
        final String fileExtension = workbook instanceof HSSFWorkbook ? ".xls" : ".xlsx";

        if (reportFile == null) {
            reportFile = new Dialogs().selectAnyFileTS(MainWindow.getMainStage(), "Выбор места сохранения",
                    Dialogs.EXCEL_FILES, "report_" + Utils.getDateTime().replaceAll(":", "-")
                            + fileExtension).get(0);
        }

        try {
            FileOutputStream outFile = new FileOutputStream(reportFile);
            workbook.write(outFile);

            workbook.close();
            outFile.close();

            System.out.println("Created file: " + reportFile.getAbsolutePath());
            return reportFile;
        } catch (Exception e) {
            System.out.println("error of excel file creating " + e.getMessage());
            Dialogs.showMessage("Ошибка создания файла", e.getMessage());
            return null;
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
