package files.reports;

import core.Dialogs;
import files.ExcelCellStyleFactory;
import javafx.application.Platform;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import ui_windows.main_window.MainWindow;
import ui_windows.product.Product;
import ui_windows.product.data.DataItem;
import utils.Utils;
import utils.comparation.merger.MergerResult;
import utils.comparation.merger.MergerResultItem;
import utils.comparation.se.ObjectsComparatorResultSe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import static ui_windows.product.data.DataItem.*;

public class PriceComparisonMergerResultToExcel {
    private String[] mainTitles = new String[]{"Направление", "Заказной номер", "Артикул"};
    private String[] sheetTitles = new String[]{"Тип изменения", "Изменённое свойство", "Исходное значение", "    ", "Новое значение"};
    private DataItem values[] = new DataItem[]{DATA_FAMILY_NAME, DATA_ORDER_NUMBER, DATA_ARTICLE};
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private XSSFCellStyle style;
    private int rowNum;
    private int colIndex;

    public void export(ArrayList<String> sheetNames, MergerResult<Product> mergerResult, String name) {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet(Utils.getDateTime().replaceAll("\\:", "_").concat("_").concat(name));

        Platform.runLater(() -> {
            File reportFile = new Dialogs().selectAnyFile(MainWindow.getMainStage(), "Сохранение результатов сравнения",
                    Dialogs.EXCEL_FILES, name + ".xlsx");

            if ((reportFile != null)) {
                XSSFRow row;

                fillTitles(sheetNames);

                for (MergerResultItem<Product> resultItem : mergerResult.getResultItems()) {
//                    row = sheet.createRow(rowNum++);
//                    colIndex = 0;
//
//                    fillItemDetails(resultItem.getItem(), row);

                    if (resultItem.getItem().getArticle().equals("GDB331.9E")) {
                        System.out.println();
                    }

                    for (String sheetName : sheetNames) {
//                        fillValues(resultItem, sheetNames.indexOf(sheetName), row);
                        fillValues(resultItem, sheetNames.indexOf(sheetName));
                    }
                }

                try {
                    FileOutputStream fos = new FileOutputStream(reportFile);
                    workbook.write(fos);

                    fos.close();
                    Utils.openFile(reportFile);
                } catch (Exception e) {
                    Platform.runLater(() -> Dialogs.showMessage("Ошибка сохранения отчёта", e.getMessage()));
                } finally {
                    try {
                        workbook.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void fillTitles(ArrayList<String> sheetsNames) {
        XSSFRow row = sheet.createRow(rowNum++);

        for (String sheetName : sheetsNames) {
            int colFrom = mainTitles.length + sheetsNames.indexOf(sheetName) * sheetTitles.length;
            int colTo = mainTitles.length + (sheetsNames.indexOf(sheetName) + 1) * sheetTitles.length - 1;

            colIndex = colFrom;
            fillCell(row.createCell(colIndex++), sheetName);

            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), colFrom, colTo));
        }

        row = sheet.createRow(rowNum++);
        colIndex = 0;
        for (String title : mainTitles) {
            fillCell(row.createCell(colIndex++), title);
            sheet.autoSizeColumn(colIndex - 1);
        }

        for (String sheetName : sheetsNames) {
            for (String title : sheetTitles) {
                fillCell(row.createCell(colIndex++), title);
            }

        }
    }

    private void fillItemDetails(Product item, XSSFRow row) {
        new ExcelCellStyleFactory(workbook);
        for (DataItem dataItem : values) {
            dataItem.fillExcelCell(row.createCell(colIndex++), item, null);
        }
    }

    private void fillValues(MergerResultItem<Product> resultItem, int index) {
        ObjectsComparatorResultSe<Product> res;
        if (resultItem.getDetails().size() > index) {
            res = resultItem.getDetails().get(index);

            if (res != null) {
                XSSFRow row;
                if (res.getItem() != null && res.getItem_after() != null) {//changed
                    fillChangeDetails(res, index);
                } else {//new or gone
                    row = sheet.createRow(rowNum++);
                    colIndex = 0;
                    fillItemDetails(resultItem.getItem(), row);

                    if (res.getItem() == null && res.getItem_after() != null) {//new
                        fillCell(row.createCell(colIndex++), "added");
                    } else if ((res.getItem() != null && res.getItem_after() == null)) {// gone
                        fillCell(row.createCell(colIndex++), "gone");
                    }
                }
            }
        }
    }

    private void fillChangeDetails(ObjectsComparatorResultSe<Product> resultItem, int index) {
        XSSFRow row;
        for (Field field : resultItem.getChangedFields()) {

            row = sheet.createRow(rowNum++);
            colIndex = 0;
            fillItemDetails(resultItem.getItem(), row);

            colIndex = values.length + sheetTitles.length * index;
            field.setAccessible(true);

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
        }


        /*else if (text.matches("^\\d+\\.*\\d+$")) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(Double.parseDouble(text));
        }*/
        else {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(object.toString());
        }
    }
}
