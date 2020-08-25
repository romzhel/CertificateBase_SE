package files.reports;

import javafx.application.Platform;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import ui.Dialogs;
import ui_windows.main_window.MainWindow;
import utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public abstract class ReportToExcelTemplate implements Callable<File> {
    protected File reportFile;
    protected Workbook workbook;

    protected File saveToExcelFile() {
        final String fileExtension = workbook instanceof HSSFWorkbook ? ".xls" : ".xlsx";

        if (reportFile == null) {
            CountDownLatch waitingDialog = new CountDownLatch(1);
            Platform.runLater(() -> {
                reportFile = new Dialogs().selectAnyFile(MainWindow.getMainStage(), "Выбор места сохранения",
                        Dialogs.EXCEL_FILES, "report_" + Utils.getDateTime().replaceAll(":", "-")
                                + fileExtension).get(0);

                waitingDialog.countDown();
            });
            try {
                waitingDialog.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
