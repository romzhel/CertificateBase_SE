package files.price_to_excel;

import core.CoreModule;
import core.Dialogs;
import files.ExcelCellStyleFactory;
import javafx.application.Platform;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ui_windows.main_window.DataSelectorMenu;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.product.Product;
import utils.Utils;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class ExportPriceListToExcel_SE {
    public static final int INITIAL_ROW = 2;
    private PriceList priceList;
    private XSSFWorkbook excelDoc;
    private File templateFile;
    private File tempFile;
    private File resultFile;
    private int itemCount;
    private ArrayList<PriceStructure> priceStructures;
    private ArrayList<Product> allProblemItems;

    public ExportPriceListToExcel_SE(PriceList priceList) {
        this.priceList = priceList;
        Runnable endActions = () -> {
            fillDoc();
            saveToFile();
            MainWindow.setProgress(0.0);
        };

        if (loadTemplate()) {
            MainWindow.setProgress(-1);

            new Thread(() -> {
                createPriceStructures();

                if (allProblemItems.size() > 0) {
                    Platform.runLater(() -> {
                        CoreModule.setAndDisplayCustomItems(allProblemItems);

                        if (Dialogs.confirm("Формирование прайс-листа", "Найдены заказные" +
                                " позиции, статус проверки сертификатов которых не позволяет добавить их в прайс-лист (" +
                                allProblemItems.size() + "). Они будут отображены в наборе данных Запросы.\n\n" +
                                "Продолжить формирование прайс-листа без данных позиций?")) {
                            new Thread(endActions).start();
                        } else {
                            MainWindow.setProgress(0.0);
                        }
                    });
                } else {
                    endActions.run();
                }
            }).start();
        }
    }

    private boolean loadTemplate() {
        templateFile = priceList.getTemplate();
        if (templateFile == null || !templateFile.exists()) {
            File dialogFile = new Dialogs().selectAnyFile(MainWindow.getMainStage(), "Выбор файла шаблона",
                    Dialogs.EXCEL_FILES, null);
            if (dialogFile == null || !dialogFile.exists()) {
                Dialogs.showMessage("Формирование прайс листа", "Не найден файл шаблона.");
                return false;
            }
            templateFile = dialogFile;
        }

        String targetFileName = priceList.getFileName().isEmpty() ? "PriceList" : priceList.getFileName();
        targetFileName = targetFileName.concat("_").concat(Utils.getDate(new Date()));

        try {
            tempFile = new File(CoreModule.getFolders().getTempFolder().getPath() + "\\" + targetFileName + ".xlsx");
            Files.copy(templateFile.toPath(), tempFile.toPath(), REPLACE_EXISTING);
        } catch (Exception e) {
            Dialogs.showMessage("Ошибка копирования шаблона", "Ошибка копирования файла шаблона: " + e.getMessage());
            return false;
        }

        try {
            FileInputStream fis = new FileInputStream(tempFile);
            excelDoc = new XSSFWorkbook(fis);
            fis.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    private void createPriceStructures() {
        allProblemItems = new ArrayList<>();
        priceStructures = new ArrayList<>();

        int sheetIndex = 0;
        for (PriceListSheet priceListSheet : priceList.getSheets()) {
            String sheetName = priceListSheet.getSheetName();
            if (sheetName != null && !sheetName.isEmpty()) {
                excelDoc.setSheetName(sheetIndex, sheetName);
            }

            PriceStructure priceStructure = new PriceStructure(priceListSheet);
            priceStructure.analysePriceItems();
            allProblemItems.addAll(priceStructure.getProblemItems());
            priceStructures.add(priceStructure);
        }
    }

    private void fillDoc() {
        new ExcelCellStyleFactory(excelDoc);

        int index = 0;
        for (PriceStructure priceStructure : priceStructures) {
            priceStructure.export(excelDoc.getSheetAt(index++));
        }
    }

    private boolean saveToFile() {
        try {
            FileOutputStream fos = new FileOutputStream(tempFile);
            excelDoc.write(fos);
            fos.close();
            excelDoc.close();

            if (priceList.getDestination() != null) {
                resultFile = new File(priceList.getDestination().getPath() + "\\" + tempFile.getName());
            } else {
                resultFile = new Dialogs().selectAnyFile(MainWindow.getMainStage(), "Выбор места сохранения",
                        Dialogs.EXCEL_FILES, tempFile.getName());
            }

            if (resultFile == null) {
                Platform.runLater(() -> {
                    Dialogs.showMessage("Выбор места сохранения", "Операция отменена, так как не было выбрано " +
                            "место сохранения");
                });
                return false;
            }

            Platform.runLater(() -> {
                if (resultFile.getParentFile().exists() /*&& CoreModule.getUsers().getCurrentUser().getProfile().getName().toLowerCase().contains("полный")*/) {
                    if (Dialogs.confirm("Формирование прайс листа", "Прайс лист сформирован. Желаете " +
                            "скопировать его в " + resultFile.getPath() + "?")) {
                        try {
                            Files.copy(tempFile.toPath(), resultFile.toPath(), REPLACE_EXISTING);
                            Utils.openFile(resultFile.getParentFile());
                        } catch (IOException e) {
                            Dialogs.showMessage("Формирование прайс-листа", e.getMessage());
                        }
                    }
                }

                if (Dialogs.confirm("Формирование прайс листа", "Желаете открыть локальную копию прайс листа?")) {
                    Utils.openFile(tempFile);
                }
            });
        } catch (IOException e) {
            Platform.runLater(() -> {
                Dialogs.showMessage("Формирование прайс-листа", "Произошла ошибка " + e.getMessage());
            });
        }
        return true;
    }
}