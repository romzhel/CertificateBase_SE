package files.price_to_excel;

import core.CoreModule;
import core.Dialogs;
import javafx.application.Platform;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class ExportPriceListToExcel_SE {
    public static final int INITIAL_ROW = 2;
    public static XSSFCellStyle CELL_ALIGN_LEFT;
    public static XSSFCellStyle CELL_ALIGN_LEFT_BOLD;
    public static XSSFCellStyle CELL_ALIGN_RIGHT;
    public static XSSFCellStyle CELL_ALIGN_CENTER;
    public static XSSFCellStyle CELL_CURRENCY_FORMAT;
    private PriceList priceList;
    private XSSFWorkbook excelDoc;
    private File templateFile;
    private File tempFile;
    private File resultFile;
    private int itemCount;

    public ExportPriceListToExcel_SE(PriceList priceList) {
        this.priceList = priceList;

        if (loadTemplate()) {
            MainWindow.setProgress(-1);

            new Thread(() -> {
                fillDoc();
                saveToFile();

                Platform.runLater(() -> MainWindow.setProgress(0.0));
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

    private void fillDoc() {
        initCellStyles();

        int sheetIndex = 0;
        for (PriceListSheet priceListSheet : priceList.getSheets()) {
            String sheetName = priceListSheet.getSheetName();
            if (sheetName != null && !sheetName.isEmpty()) {
                excelDoc.setSheetName(sheetIndex, sheetName);
            }

            new PriceStructure(priceListSheet).export(excelDoc.getSheetAt(sheetIndex++));
        }
    }

    private void initCellStyles() {
        CELL_ALIGN_LEFT = excelDoc.createCellStyle();
        CELL_ALIGN_LEFT.setAlignment(HorizontalAlignment.LEFT);

        CELL_ALIGN_LEFT_BOLD = excelDoc.createCellStyle();
        CELL_ALIGN_LEFT_BOLD.setAlignment(HorizontalAlignment.LEFT);
        XSSFFont font = excelDoc.createFont();
        font.setBold(true);
        font.setColor(XSSFFont.DEFAULT_FONT_COLOR);
        font.setFontName(XSSFFont.DEFAULT_FONT_NAME);
        font.setFontHeight(8);
        CELL_ALIGN_LEFT_BOLD.setFont(font);

        CELL_ALIGN_RIGHT = excelDoc.createCellStyle();
        CELL_ALIGN_RIGHT.setAlignment(HorizontalAlignment.RIGHT);

        CELL_ALIGN_CENTER = excelDoc.createCellStyle();
        CELL_ALIGN_CENTER.setAlignment(HorizontalAlignment.CENTER);

        CELL_CURRENCY_FORMAT = excelDoc.createCellStyle();
        XSSFDataFormat dataFormat = excelDoc.createDataFormat();
        CELL_CURRENCY_FORMAT.setDataFormat(dataFormat.getFormat("# ##0.00\\ [$€-x-euro1];[Red]# ##0.00\\ [$€-x-euro1]"));
    }

    private boolean saveToFile() {
        try {
            FileOutputStream fos = new FileOutputStream(tempFile);
            excelDoc.write(fos);
            fos.close();
            excelDoc.close();

            if (priceList.getDestination() != null) resultFile = new File(priceList.getDestination().getPath() +
                    "\\" + tempFile.getName());
            else resultFile = new Dialogs().selectAnyFile(MainWindow.getMainStage(), "Выбор места сохранения",
                    Dialogs.EXCEL_FILES, tempFile.getName());
            if (resultFile == null) {
                Platform.runLater(() -> {
                    Dialogs.showMessage("Выбор места сохранения", "Операция отменена, так как не было выбрано " +
                            "место сохранения");
                });
                return false;
            }

            Platform.runLater(() -> {
                if (CoreModule.getUsers().getCurrentUser().getProfile().getName().toLowerCase().contains("полный")) {
                    if (Dialogs.confirm("Формирование прайс листа", "Прайс лист сформирован. Желаете " +
                            "скопировать его в " + resultFile.getPath() + "?")) {
                        try {
                            Files.copy(tempFile.toPath(), resultFile.toPath(), REPLACE_EXISTING);
                        } catch (IOException e) {
                            Dialogs.showMessage("Формирование прайс-листа", e.getMessage());;
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
