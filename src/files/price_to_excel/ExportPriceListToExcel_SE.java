package files.price_to_excel;

import core.CoreModule;
import core.Dialogs;
import javafx.application.Platform;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.Product;
import utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class ExportPriceListToExcel_SE {
    public static final int INITIAL_ROW = 2;
    private PriceList priceList;
    private XSSFWorkbook excelDoc;
    private File templateFile;
    private File resultFile;
    private int itemCount;
    //    private ExportPriceListToExcel_SE.Structure priceRuEn;
//    private ExportPriceListToExcel_SE.Structure priceService;
    public static XSSFCellStyle CELL_ALIGN_LEFT;
    public static XSSFCellStyle CELL_ALIGN_LEFT_BOLD;
    public static XSSFCellStyle CELL_ALIGN_RIGHT;
    public static XSSFCellStyle CELL_ALIGN_CENTER;
    public static XSSFCellStyle CELL_CURRENCY_FORMAT;

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
        if (priceList.getDestination() != null) resultFile = new File(priceList.getDestination().getPath() +
                "\\" + targetFileName + ".xlsx");
        else resultFile = new Dialogs().selectAnyFile(MainWindow.getMainStage(), "Выбор места сохранения",
                Dialogs.EXCEL_FILES, targetFileName + ".xlsx");
        if (resultFile == null) {
            Dialogs.showMessage("Выбор места сохранения", "Операция отменена, так как не было выбрано " +
                    "место сохранения");
            return false;
        }

        try {
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
        initCellStyles();
        PriceListSheet priceListSheet = priceList.getSheets().get(0);
        XSSFSheet sheet = excelDoc.getSheetAt(0);

        PriceStructure priceStructure = new PriceStructure(priceListSheet);
        priceStructure.export(sheet);


        System.out.println("end test");


//        priceRuEn = new ExportPriceListToExcel_SE.Structure();
//        priceService = new ExportPriceListToExcel_SE.Structure();


/*
        fillSheet(0, priceRuEn);
        fillSheet(1, priceService);

        System.out.println("price items: " + priceRuEn.getSize() + " / " + priceService.getSize());*/
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

    private void saveToFile() {
        try {
            FileOutputStream fos = new FileOutputStream(resultFile);
            excelDoc.write(fos);
            fos.close();
            excelDoc.close();

            Platform.runLater(() -> {
                if (Dialogs.confirm("Формирование прайс листа", "Прайс лист сформирован. Желаете открыть его?")) {
                    Utils.openFile(resultFile);
                }
            });
        } catch (IOException e) {
            Platform.runLater(() -> {
                Dialogs.showMessage("Формирование прайс-листа", "Произошла ошибка " + e.getMessage());
            });
        }
    }

    private boolean isSpProduct(Product product) {
        int id = CoreModule.getProductLgbks().getFamilyIdByLgbk(new ProductLgbk(product.getLgbk(), product.getHierarchy()));
        boolean productFamId = product.getFamily() == 24;
        return id == 24 || productFamId;
    }

    private boolean isEvacProduct(Product product) {
        int id = CoreModule.getProductLgbks().getFamilyIdByLgbk(new ProductLgbk(product.getLgbk(), product.getHierarchy()));
        boolean productFamId = product.getFamily() == 28;
        return id == 28 || productFamId;
    }

    private boolean isNewProduct(Product product) {
        String status = product.getDchain();
        return status.equals("0") || status.equals("20") || status.equals("22") || status.equals("23") || status.equals("24");
    }

    private boolean isPricePosition(Product product) {
        String status = product.getDchain();
        return status.equals("28") || status.equals("30") || (status.isEmpty() && isSpProduct(product));
//                /*|| isEvacProduct(product)*/);//эвакуация
    }

    private boolean isServicePosition(Product product) {
        String status = product.getDchain();
        return status.equals("36") || status.equals("52") || status.equals("56") || status.equals("58") ||
                status.equals("60") || status.equals("61") || status.equals("62");
    }
}
