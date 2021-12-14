package scripts;

import files.Folders;
import files.price_to_excel.ExportPriceListToExcel_SE;
import files.reports.ReportParameterEnum;
import files.reports.ReportToExcel;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui.Dialogs;
import ui_windows.main_window.DataSelectorMenu;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.price_lists_editor.PriceLists;
import ui_windows.product.data.DataItem;
import utils.Utils;
import utils.comparation.te.BudgetEvaluationToExcelExporter;
import utils.comparation.te.PricesComparisonTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static core.SharedData.SHD_CUSTOM_DATA;
import static files.reports.ReportParameterEnum.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static ui_windows.product.data.DataItem.*;

public class PriceGenerationScript implements Runnable {
    public static final Logger logger = LogManager.getLogger(PriceGenerationScript.class);
    public final static int ONLY_PRICE = 0;
    public final static int REPORTS_FOR_CHECK = 1;
    public final static int REPORTS_FOR_USING = 2;
    private int priceIndex;
    private int generationMode;

    public PriceGenerationScript(int priceIndex, int generationMode) {
        this.priceIndex = priceIndex;
        this.generationMode = generationMode;
    }

    @Override
    public void run() throws RuntimeException {
//        new Thread(() -> {
//            ExecutionIndicator.getInstance().start();
        PriceList priceList = PriceLists.getInstance().getItems().get(priceIndex);
        priceList.generate();
        logger.trace("прайс-лист сгенерирован");

        File outOfPriceFile = null;
        File priceComparisonFile = null;

        File priceListFile = new ExportPriceListToExcel_SE(priceList, generationMode == ONLY_PRICE).call();

        if (generationMode == REPORTS_FOR_USING) {
            priceListTasks(priceList, priceListFile);
        }

        if (generationMode == ONLY_PRICE) {
            Utils.openFile(priceListFile);
            return;
        }

        if (priceListFile != null) {
            Path path = Folders.getInstance().getTempFolder().resolve(Utils.getDateTimeForFileName() +
                    "_out_of_price_report.xlsx");

            DataItem[] columns = new DataItem[]{DATA_FAMILY_NAME, DATA_RESPONSIBLE, DATA_ORDER_NUMBER,
                    DATA_ARTICLE, DATA_DESCRIPTION, DATA_COUNTRY_WITH_COMMENTS, DATA_DCHAIN_WITH_COMMENT,
                    DATA_CERTIFICATE};

            Map<ReportParameterEnum, Object> reportParams = new HashMap<>();
            reportParams.put(REPORT_PATH, path);
            reportParams.put(REPORT_COLUMNS, Arrays.asList(columns));
            reportParams.put(REPORT_ITEMS, SHD_CUSTOM_DATA.getData());

            new ReportToExcel(reportParams).export();

            DataSelectorMenu.MENU_DATA_CUSTOM_SELECTION.activate();

            logger.trace("out of price report generated");

            PricesComparisonTask pricesComparatorTask = new PricesComparisonTask();

            File previousPriceList = new Dialogs().selectAnyFileTS(MainWindow.getMainStage(),
                    "Выберите прайс для сравнения", Dialogs.EXCEL_FILES_ALL,
                    priceList.getDestination().getPath()).get(0);

            pricesComparatorTask.comparePriceFilesAndGenerateReport(previousPriceList, priceListFile,
                    Folders.getInstance().getTempFolder());

            logger.trace("price lists have been compared");//
        }

//                Utils.copyFilesToClipboardTS(Arrays.asList(priceListFile, outOfPriceFile, priceComparisonFile));
        Utils.openFile(priceListFile.getParentFile());

        String fileName = Utils.getDateTimeForFileName() + ".xlsx";
        Path budgetEvalReportPath = Folders.getInstance().getTempFolder().resolve("budget_eval_" + fileName);
        new BudgetEvaluationToExcelExporter(budgetEvalReportPath).run();

//                OutlookEmailSender outlookEmailSender = new OutlookEmailSender();
//                outlookEmailSender.send();
    }

    private void priceListTasks(PriceList priceList, File tempFile) {
        File resultFile;
        if (priceList.getDestination() != null) {
            resultFile = new File(priceList.getDestination().getPath() + "\\" + tempFile.getName());
        } else {
            resultFile = new Dialogs().selectAnyFileTS(MainWindow.getMainStage(), "Выбор места сохранения",
                    Dialogs.EXCEL_FILES_ALL, tempFile.getName()).get(0);
        }

        if (resultFile == null) {
            Platform.runLater(() -> {
                Dialogs.showMessageTS("Выбор места сохранения", "Операция отменена, так как не было выбрано " +
                        "место сохранения");
            });
            return;
        }

        if (resultFile.getParentFile().exists() /*&& CoreModule.getUsers().getCurrentUser().getProfile().getName().toLowerCase().contains("полный")*/) {
            if (Dialogs.confirmTS("Формирование прайс листа", "Прайс лист сформирован. Желаете " +
                    "скопировать его в " + resultFile.getPath() + "?")) {
                try {
                    Files.copy(tempFile.toPath(), resultFile.toPath(), REPLACE_EXISTING);
                    Utils.openFile(resultFile.getParentFile());
                } catch (IOException e) {
                    Dialogs.showMessageTS("Формирование прайс-листа", e.getMessage());
                }
            }
        }

        if (Dialogs.confirmTS("Формирование прайс листа", "Желаете открыть локальную копию прайс листа?")) {
            Utils.openFile(tempFile);
        }
    }
}
