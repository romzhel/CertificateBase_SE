package scripts;

import core.CoreModule;
import core.Dialogs;
import files.price_to_excel.ExportPriceListToExcel_SE;
import files.reports.ReportToExcel;
import javafx.application.Platform;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.product.data.DataItem;
import utils.OutlookEmailSender;
import utils.Utils;
import utils.comparation.prices.PricesComparator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static core.SharedData.SHD_CUSTOM_DATA;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static ui_windows.product.data.DataItem.*;

public class PriceGenerationScript {
    public final static int ONLY_PRICE = 0;
    public final static int REPORTS_FOR_CHECK = 1;
    public final static int REPORTS_FOR_USING = 2;

    public void run(int priceIndex, int generationMode) {
        new Thread(() -> {
            MainWindow.setProgress(-1);
            PriceList priceList = CoreModule.getPriceLists().getItems().get(priceIndex);
            priceList.generate();
            System.out.println("new price list generated");

            File priceListFile = null;
            File outOfPriceFile = null;
            File priceComparisonFile = null;

            try {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                priceListFile = executorService.submit(() -> new ExportPriceListToExcel_SE(priceList,
                        generationMode == ONLY_PRICE).call()).get();

                if (generationMode == REPORTS_FOR_USING) {
                    priceListTasks(priceList, priceListFile);
                }

                if (generationMode == ONLY_PRICE) {
                    Utils.openFile(priceListFile);
                    executorService.shutdown();
                    executorService.awaitTermination(1, TimeUnit.MINUTES);
                    MainWindow.setProgress(0.0);
                    return;
                }

                if (priceListFile != null) {
                    outOfPriceFile = new File(CoreModule.getFolders().getTempFolder().getPath() + "\\" +
                            "out_of_price_report_" + Utils.getDateTimeForFileName() + ".xlsx");

                    DataItem[] columns = new DataItem[]{DATA_FAMILY_NAME, DATA_RESPONSIBLE, DATA_ORDER_NUMBER,
                            DATA_ARTICLE, DATA_DESCRIPTION, DATA_COUNTRY_WITH_COMMENTS, DATA_DCHAIN_WITH_COMMENT,
                            DATA_CERTIFICATE};

                    AtomicReference<File> opf = new AtomicReference<>(outOfPriceFile);
                    outOfPriceFile = executorService.submit(() -> new ReportToExcel().export(Arrays.asList(columns),
                            SHD_CUSTOM_DATA.getData(), opf.get())).get();

                    System.out.println("out of price report generated");

                    PricesComparator pricesComparator = new PricesComparator();
                    if (priceList == pricesComparator.getCOMPARED_PRICE_LIST()) {

                        File previousPriceList = new Dialogs().selectAnyFileTS(MainWindow.getMainStage(),
                                "Выберите прайс для сравнения", Dialogs.EXCEL_FILES,
                                priceList.getDestination().getPath());

                        if (previousPriceList != null) {
                            pricesComparator.compare(previousPriceList, priceListFile);
                            String fileName = String.format("%s\\price_comparison_report_%s vs %s.xlsx", CoreModule.getFolders()
                                    .getTempFolder().getPath(), priceListFile.getName(), previousPriceList.getName());
                            priceComparisonFile = new File(fileName);
                            pricesComparator.exportToExcel(priceComparisonFile);

                            System.out.println("price lists have been compared");
                        } else {
                            System.out.println("price list for comparing wasn't select");
                        }
                    } else {
                        System.out.println("try to compare different price lists - ignored");
                    }
                }

                executorService.shutdown();

                Utils.copyFilesToClipboardTS(Arrays.asList(priceListFile, outOfPriceFile, priceComparisonFile));
                Utils.openFile(priceListFile.getParentFile());

                OutlookEmailSender outlookEmailSender = new OutlookEmailSender();
                outlookEmailSender.send();
            } catch (Exception e) {
                System.out.println(e.getMessage() + "\n" + e.getStackTrace());
            }
            MainWindow.setProgress(0.0);
        }).start();
    }

    private void priceListTasks(PriceList priceList, File tempFile) {
        File resultFile;
        if (priceList.getDestination() != null) {
            resultFile = new File(priceList.getDestination().getPath() + "\\" + tempFile.getName());
        } else {
            resultFile = new Dialogs().selectAnyFileTS(MainWindow.getMainStage(), "Выбор места сохранения",
                    Dialogs.EXCEL_FILES, tempFile.getName());
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