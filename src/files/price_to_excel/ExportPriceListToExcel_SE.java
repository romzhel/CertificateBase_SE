package files.price_to_excel;

import core.CoreModule;
import core.Dialogs;
import files.ExcelCellStyleFactory;
import files.Folders;
import javafx.application.Platform;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.price_lists_editor.PriceList;
import utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.concurrent.Callable;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class ExportPriceListToExcel_SE implements Callable<File> {
    private PriceList priceList;
    private XSSFWorkbook excelDoc;
    private File resultFile;

    public ExportPriceListToExcel_SE(PriceList priceList) {
        export(priceList, true);
    }

    public ExportPriceListToExcel_SE(PriceList priceList, boolean askProblemItems) {
        export(priceList, askProblemItems);
    }

    public void export(PriceList priceList, boolean askProblemItems) {
        this.priceList = priceList;

        if (loadTemplate() && priceList.getPriceStructures() != null) {
            MainWindow.setProgress(-1);

            if (priceList.getProblemItems().size() > 0) {
                Platform.runLater(() -> CoreModule.setAndDisplayCustomItems(priceList.getProblemItems()));

                if (!askProblemItems || Dialogs.confirmTS("Формирование прайс-листа", "Найдены заказные" +
                        " позиции, статус проверки сертификатов которых не позволяет добавить их в прайс-лист (" +
                        priceList.getProblemItems().size() + "). Они будут отображены в наборе данных Запросы.\n\n" +
                        "Продолжить формирование прайс-листа без данных позиций?")) {
                    fillDoc();
                    saveToFile();
                }
            }
        } else {
            System.out.println(String.format("pricelist %s wasn't generated !!! can't be exported to Excel", priceList.getName()));
        }
    }

    private boolean loadTemplate() {
        File templateFile = priceList.getTemplate();
        if (templateFile == null || !templateFile.exists()) {
            File dialogFile = new Dialogs().selectAnyFile(MainWindow.getMainStage(), "Выбор файла шаблона",
                    Dialogs.EXCEL_FILES, null).get(0);
            if (dialogFile == null || !dialogFile.exists()) {
                Dialogs.showMessage("Формирование прайс листа", "Не найден файл шаблона.");
                return false;
            }
            templateFile = dialogFile;
        }

        String targetFileName = priceList.getFileName().isEmpty() ? "PriceList" : priceList.getFileName();
        targetFileName = targetFileName.concat("_").concat(Utils.getDate(new Date()));

        try {
            resultFile = new File(Folders.getInstance().getTempFolder().getPath() + "\\" + targetFileName + ".xlsx");
            Files.copy(templateFile.toPath(), resultFile.toPath(), REPLACE_EXISTING);
        } catch (Exception e) {
            Dialogs.showMessage("Ошибка копирования шаблона", "Ошибка копирования файла шаблона: " + e.getMessage());
            return false;
        }

        try {
            FileInputStream fis = new FileInputStream(resultFile);
            excelDoc = new XSSFWorkbook(fis);
            fis.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    private void fillDoc() {
        new ExcelCellStyleFactory(excelDoc);

        int index = 0;
        String sheetName;
        for (PriceStructure priceStructure : priceList.getPriceStructures()) {
            sheetName = priceList.getSheets().get(index).getSheetName();

            if (sheetName != null && !sheetName.isEmpty() && !sheetName.equals(excelDoc.getSheetName(index))) {
                excelDoc.setSheetName(index, sheetName);
            }

            priceStructure.export(excelDoc.getSheetAt(index++));
        }
    }

    private File saveToFile() {
        try {
            FileOutputStream fos = new FileOutputStream(resultFile);
            excelDoc.write(fos);
            fos.close();
            excelDoc.close();
        } catch (IOException e) {
            Platform.runLater(() -> {
                Dialogs.showMessage("Формирование прайс-листа", "Произошла ошибка " + e.getMessage());
            });
        }
        return resultFile;
    }

    @Override
    public File call() throws Exception {
        return resultFile;
    }

    public interface DelayedResult {
        void sendDelayedResult(File resultFile);
    }
}