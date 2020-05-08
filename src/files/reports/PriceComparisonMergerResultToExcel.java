package files.reports;

import core.Dialogs;
import files.ExcelCellStyleFactory;
import javafx.application.Platform;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ui_windows.ExecutionIndicator;
import ui_windows.main_window.MainWindow;
import ui_windows.product.Product;
import ui_windows.product.data.DataItem;
import utils.Utils;
import utils.comparation.merger.MergerResultItem;
import utils.comparation.prices.PricesComparator;
import utils.comparation.se.ObjectsComparatorResultSe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import static files.ExcelCellStyleFactory.CELL_ALIGN_LEFT_BOLD;
import static ui_windows.product.data.DataItem.*;

public class PriceComparisonMergerResultToExcel {
    private String[] sheetTitles = new String[]{"Тип изменения", "Изменённое свойство", "Исходное значение", "    ", "Новое значение"};
    private DataItem[] values = new DataItem[]{DATA_FAMILY_NAME, DATA_ORDER_NUMBER, DATA_ARTICLE, DATA_DESCRIPTION};
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private File reportFile;
    private int rowNum;
    private int colIndex;

    public File export(PricesComparator pricesComparator, File reportFile) {
        this.reportFile = reportFile;
        String firstName = pricesComparator.getFile1().getName();
        String secondName = pricesComparator.getFile2() != null ? pricesComparator.getFile2().getName() : "online price";
        String name = String.format("PriceComparisonResult %s vs %s", secondName, firstName);

        workbook = new XSSFWorkbook();
        new ExcelCellStyleFactory(workbook);
        sheet = workbook.createSheet(Utils.getDateTime().replaceAll("\\:", "_").concat("_").concat(name));

//        Platform.runLater(() -> {
        if (reportFile == null) {
            reportFile = new Dialogs().selectAnyFileTS(MainWindow.getMainStage(), "Сохранение результатов сравнения",
                    Dialogs.EXCEL_FILES, name + ".xlsx").get(0);
        }

        if ((reportFile != null)) {
//                new Thread(() -> {
            ExecutionIndicator.getInstance().start();
            XSSFRow row;

            fillTitles(pricesComparator.getOldPriceFi().getExcelFile().getSheetsName());

            for (MergerResultItem<Product> resultItem : pricesComparator.getMerger().getResult().getResultItems()) {
                row = sheet.createRow(rowNum++);

                boolean isGone = false;
                for (ObjectsComparatorResultSe<Product> objCompRes : resultItem.getDetails()) {
                    if (objCompRes == null) {
                        fillCell(row.createCell(colIndex++), " ");
                        fillCell(row.createCell(colIndex++), " ");
                        fillCell(row.createCell(colIndex++), " ");
                        fillCell(row.createCell(colIndex++), " ");
                        continue;
                    }

                    int index = resultItem.getDetails().indexOf(objCompRes);

                    if (objCompRes.getItem() != null && objCompRes.getItem_after() != null) {//changed
                        fillChangeDetails(objCompRes, index);
                    } else {//new or gone
                        fillItemDetails(resultItem.getItem(), row);
                        colIndex += sheetTitles.length * index;

                        if (objCompRes.getItem() == null && objCompRes.getItem_after() != null) {//new
                            fillCell(row.createCell(colIndex++), "добавлена");

                            if (isGone) {//moved from main to service positions
                                fillCell(row.createCell(colIndex - sheetTitles.length), "Цена в прайсе");
                                fillCell(row.createCell(colIndex++), "Цена в прайсе");
                                fillCell(row.createCell(colIndex++ - sheetTitles.length), DATA_LOCAL_PRICE.getValue(resultItem.getItem()));
                                fillCell(row.createCell(colIndex - sheetTitles.length), "->");
                                fillCell(row.createCell(colIndex++), "->");

                                if (pricesComparator.getFile2() == null) {
                                    int discount = pricesComparator.getCOMPARED_PRICE_LIST().getSheets().get(resultItem.getDetails().indexOf(objCompRes)).getDiscount();
                                    double costInPrice = (double) DATA_LOCAL_PRICE.getValue(objCompRes.getItem_after()) * (1 - (double) discount / 100);
                                    fillCell(row.createCell(colIndex++), costInPrice);
                                } else {
                                    fillCell(row.createCell(colIndex++), DATA_LOCAL_PRICE.getValue(objCompRes.getItem_after()));
                                }
                            }

                        } else if ((objCompRes.getItem() != null && objCompRes.getItem_after() == null)) {// gone
                            fillCell(row.createCell(colIndex++), "удалена");
                            isGone = true;
                        }
                    }
                }
            }

            try {
                FileOutputStream fos = new FileOutputStream(reportFile);
                workbook.write(fos);

                fos.close();
            } catch (Exception e) {
                Platform.runLater(() -> Dialogs.showMessage("Ошибка сохранения отчёта", e.getMessage()));
            } finally {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ExecutionIndicator.getInstance().stop();
            }

//                }).start();
        }
//        });
        return reportFile;
    }

    private void fillTitles(ArrayList<String> sheetsNames) {
        XSSFRow row0 = sheet.createRow(rowNum++);
        XSSFRow row1 = sheet.createRow(rowNum++);

        for (String sheetName : sheetsNames) {
            int colFrom = values.length + sheetsNames.indexOf(sheetName) * sheetTitles.length;
            int colTo = values.length + (sheetsNames.indexOf(sheetName) + 1) * sheetTitles.length - 1;

            fillCell(row0.createCell(colFrom), sheetName, CELL_ALIGN_LEFT_BOLD);
            sheet.addMergedRegion(new CellRangeAddress(row0.getRowNum(), row0.getRowNum(), colFrom, colTo));

            colIndex = colFrom;
            for (String title : sheetTitles) {
                fillCell(row1.createCell(colIndex++), title, CELL_ALIGN_LEFT_BOLD);
                sheet.autoSizeColumn(colIndex - 1);
            }
        }

        colIndex = 0;
        for (DataItem di : values) {
            fillCell(row1.createCell(colIndex++), di.getDisplayingName(), CELL_ALIGN_LEFT_BOLD);
            sheet.autoSizeColumn(colIndex - 1);
        }
    }

    private void fillItemDetails(Product item, XSSFRow row) {
        colIndex = 0;
        new ExcelCellStyleFactory(workbook);
        for (DataItem dataItem : values) {
            dataItem.fillExcelCell(row.createCell(colIndex++), item, null);
        }
    }

    private void fillChangeDetails(ObjectsComparatorResultSe<Product> resultItem, int index) {
        XSSFRow row;
        for (Field field : resultItem.getChangedFields()) {

            if (resultItem.getChangedFields().indexOf(field) > 0) {
                row = sheet.createRow(rowNum++);
            } else {
                row = sheet.getRow(rowNum - 1);
            }
            fillItemDetails(resultItem.getItem(), row);

            colIndex = values.length + sheetTitles.length * index;
            field.setAccessible(true);

            try {
                fillCell(row.createCell(colIndex++), "изменена");
                DataItem dataItem = DataItem.getDataItemByField(field);

                if (dataItem == DATA_LOCAL_PRICE) {
                    fillCell(row.createCell(colIndex++), "Цена в прайсе");
                    if ((double) field.get(resultItem.getItem_before()) == 0.0) {
                        fillCell(row.createCell(colIndex++), "По запросу");
                    } else {
                        fillCell(row.createCell(colIndex++), field.get(resultItem.getItem_before()));
                    }
                } else {
                    fillCell(row.createCell(colIndex++), DataItem.getDataItemByField(field).getDisplayingName());
                    fillCell(row.createCell(colIndex++), field.get(resultItem.getItem_before()));
                }

                fillCell(row.createCell(colIndex++), "->");
                fillCell(row.createCell(colIndex++), field.get(resultItem.getItem_after()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void fillCell(XSSFCell cell, Object object) {
        fillCell(cell, object, ExcelCellStyleFactory.CELL_ALIGN_LEFT);
    }

    private void fillCell(XSSFCell cell, Object object, CellStyle style) {
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
