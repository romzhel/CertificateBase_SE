package utils.comparation;

import core.Dialogs;
import javafx.application.Platform;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.product.Product;
import utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ProductsComparatorResult {
    private ArrayList<Product> changedItems;
    private ArrayList<Product> newItems;
    private ArrayList<Product> goneItems;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private int rowNum;

    public ProductsComparatorResult() {
        changedItems = new ArrayList<>();
        newItems = new ArrayList<>();
        goneItems = new ArrayList<>();
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("ImportReport");
        rowNum = 1;
    }

    public ArrayList<Product> getChangedItems() {
        return changedItems;
    }

    public ArrayList<Product> getNewItems() {
        return newItems;
    }

    public ArrayList<Product> getGoneItems() {
        return goneItems;
    }

    public void clear() {
        changedItems.clear();
        newItems.clear();
        goneItems.clear();
        workbook = null;
    }

    public void addToReport(ArrayList<String[]> lines) {
        for (String[] line : lines) {
            addToReport(line);
        }
    }

    public void addToReport(Product product, String... line) {
        String[] resultLine = new String[line.length + 5];
        ProductFamily pf = product.getProductFamily();
        resultLine[0] = pf != null ? pf.getName() : "";
        resultLine[1] = pf != null ? pf.getResponsible() : "";
        resultLine[2] = product.getMaterial();
        resultLine[3] = product.getArticle();
        resultLine[4] = product.isPrice() ? "В прайсе" : "---";

        for (int i = 5; i < resultLine.length; i++) {
            resultLine[i] = line[i-5];
        }

        addToReport(resultLine);
    }

    public void addToReport(String... line) {
        XSSFRow row = sheet.createRow(rowNum++);

        int colIndex = 0;
        for (String text : line) {
            XSSFCell cell = row.createCell(colIndex++);
            XSSFCellStyle style = workbook.createCellStyle();
            style.setAlignment(HorizontalAlignment.LEFT);
            cell.setCellStyle(style);

            if (text.matches("^\\d+$")) {
                cell.setCellType(CellType.NUMERIC);
                cell.setCellValue(Long.parseLong(text));
            }  else if (text.matches("^\\d+\\.*\\d+$")) {
                cell.setCellType(CellType.NUMERIC);
                cell.setCellValue(Double.parseDouble(text));
            } else {
                cell.setCellType(CellType.STRING);
                cell.setCellValue(text);
            }
        }
    }

    public void saveReport() {
        Platform.runLater(() -> {
            File reportFile = new Dialogs().selectAnyFile(MainWindow.getMainStage(), "Сохранение результатов импорта",
                    Dialogs.EXCEL_FILES, "ImportReport_" + Utils.getDateTime().replaceAll("\\:", "-") + ".xlsx");

            if ((reportFile != null)) {
                new Thread(() -> {
                    try {
                        XSSFRow row = sheet.createRow(0);
                        String[] titles = new String[]{"Направление", "Ответственный", "Заказной номер", "Артикул", "В прайсе",
                                "Тип изменения", "Изменённое свойство", "Исходное значение", "", "Новое значение"};
                        int colIndex = 0;
                        for (String title : titles) {
                            XSSFCell cell = row.createCell(colIndex++, CellType.STRING);
                            cell.setCellValue(title);

                            sheet.autoSizeColumn(colIndex - 1);
                        }

                        FileOutputStream fos = new FileOutputStream(reportFile);
                        workbook.write(fos);

                        fos.close();
                    } catch (Exception e) {
                        Dialogs.showMessage("Ошибка сохранения отчёта", e.getMessage());
                    } finally {
                        try {
                            workbook.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

}
