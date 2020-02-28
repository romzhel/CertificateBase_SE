package files.reports;

import core.Dialogs;
import files.ExcelCellStyleFactory;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import ui_windows.main_window.MainWindow;
import ui_windows.product.Product;
import ui_windows.product.data.DataItem;
import utils.Utils;
import utils.comparation.se.ComparisonResult;
import utils.comparation.se.ObjectsComparatorResultSe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import static ui_windows.product.data.DataItem.*;

public class NowImportResultToExcel {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private XSSFCellStyle style;
    private int rowNum;
    private int colIndex;

    public NowImportResultToExcel() {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("ImportReport_".concat(Utils.getDateTime().replaceAll("\\:", "_")));
    }

    public File export(ComparisonResult<Product> result, File targetFile) {
//        Platform.runLater(() -> {
        File reportFile = targetFile != null ? targetFile : new Dialogs().selectAnyFileTS(MainWindow.getMainStage(),
                "Сохранение результатов импорта", Dialogs.EXCEL_FILES, "ImportReport_" +
                        Utils.getDateTime().replaceAll("\\:", "-") + ".xlsx");

        if ((reportFile != null)) {
//                new Thread(() -> {
            rowNum = 0;
            try {
                style = workbook.createCellStyle();
                style.setAlignment(HorizontalAlignment.LEFT);

                fillTitles();

                fillValues(result.getNewItemsResult());
                fillValues(result.getChangedItemsResult());

                FileOutputStream fos = new FileOutputStream(reportFile);
                workbook.write(fos);

                fos.close();

            } catch (Exception e) {
                Dialogs.showMessageTS("Ошибка сохранения отчёта", e.getMessage());
            } finally {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//                }).start();
        }
//        });
        return reportFile;
    }

    private void fillTitles() {
        XSSFRow row = sheet.createRow(rowNum++);
        String[] titles = new String[]{"Направление", "Ответственный", "Заказной номер", "Артикул", "В прайсе",
                "Тип изменения", "Изменённое свойство", "Исходное значение", "    ", "Новое значение"};
        colIndex = 0;
        for (String title : titles) {
            fillCell(row.createCell(colIndex++), title);
            sheet.autoSizeColumn(colIndex - 1);
        }
    }

    private void fillValues(ArrayList<ObjectsComparatorResultSe<Product>> resultItems) {
        XSSFRow row;

        for (ObjectsComparatorResultSe<Product> resultItem : resultItems) {
            row = sheet.createRow(rowNum++);
            colIndex = 0;

            if (resultItem.getItem() == null && resultItem.getItem_after() != null) {//new
                fillItemDetails(resultItem.getItem_after(), row);
                fillCell(row.createCell(colIndex++), "added");
            } else if ((resultItem.getItem() != null && resultItem.getItem_after() == null)) {// gone
                fillItemDetails(resultItem.getItem(), row);
                fillCell(row.createCell(colIndex++), "gone");
            } else {//change
                fillItemDetails(resultItem.getItem(), row);
                fillChangeDetails(resultItem, row);
            }
        }
    }

    private void fillItemDetails(Product item, XSSFRow row) {
        DataItem values[] = new DataItem[]{DATA_FAMILY_NAME, DATA_RESPONSIBLE, DATA_ORDER_NUMBER, DATA_ARTICLE, DATA_IS_IN_PRICE};
        new ExcelCellStyleFactory(workbook);
        for (DataItem dataItem : values) {
            dataItem.fillExcelCell(row.createCell(colIndex++), item, null);
        }
    }

    private void fillChangeDetails(ObjectsComparatorResultSe<Product> resultItem, XSSFRow row) {
        for (Field field : resultItem.getChangedFields()) {
            field.setAccessible(true);
            colIndex = 5;

            try {
                fillCell(row.createCell(colIndex++), "changed");
                fillCell(row.createCell(colIndex++), DataItem.getDataItemByField(field).getDisplayingName());
                fillCell(row.createCell(colIndex++), field.get(resultItem.getItem_before()));
                fillCell(row.createCell(colIndex++), "->");
                fillCell(row.createCell(colIndex++), field.get(resultItem.getItem_after()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void fillCell(XSSFCell cell, Object object) {
        cell.setCellStyle(style);
        if (object instanceof Integer) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((int) object);
        } else if (object instanceof Double) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((double) object);
        } else if (object instanceof Long) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((long) object);
        } else {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(object.toString());
        }
    }
}
