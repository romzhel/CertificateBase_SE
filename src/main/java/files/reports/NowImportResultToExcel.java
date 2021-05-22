package files.reports;

import files.ExcelCellStyleFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import ui.Dialogs;
import ui_windows.main_window.MainWindow;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.product.Product;
import ui_windows.product.Products;
import ui_windows.product.data.DataItem;
import utils.Utils;
import utils.comparation.te.ChangedItem;
import utils.comparation.te.ChangedProperty;
import utils.comparation.te.TotalComparisonResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ui_windows.product.data.DataItem.*;

public class NowImportResultToExcel {
    public static final Logger logger = LogManager.getLogger(NowImportResultToExcel.class);
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private XSSFCellStyle style;
    private int rowNum;
    private int colIndex;

    public NowImportResultToExcel() {
        workbook = new XSSFWorkbook();
    }

    public File export(TotalComparisonResult result, File targetFile) {//TODO в отчёте отображается только одно изменение
//        Platform.runLater(() -> {
        File reportFile = targetFile != null ? targetFile : new Dialogs().selectAnyFileTS(MainWindow.getMainStage(),
                "Сохранение результатов импорта", Dialogs.EXCEL_FILES, "ImportReport_" +
                        Utils.getDateTime().replaceAll("\\:", "-") + ".xlsx").get(0);

        if ((reportFile != null)) {
//                new Thread(() -> {

            try {
                logger.trace("filling main import report sheet");

                ExcelCellStyleFactory.init(workbook);
                sheet = workbook.createSheet("ImportReport_".concat(Utils.getDateTime().replaceAll("\\:", "_")));
                style = workbook.createCellStyle();
                style.setAlignment(HorizontalAlignment.LEFT);

                rowNum = 0;
                fillTitles();

                fillNewItemsValues(result.getNewItemList());

                List<ChangedItem> sortedList = new ArrayList<>(result.getChangedItemList());
                sortedList.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));
                fillChangedItemsValue(sortedList);

                sheet.createFreezePane(colIndex, 1);
                sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, colIndex));

                logger.trace("filling items without new price sheet");
                // позиции без новой цены
                if (result.getNoCostItemList().size() > 0) {
                    sheet = workbook.createSheet("NoNewCost_".concat(Utils.getDateTime().replaceAll("\\:", "_")));

                    rowNum = 0;
                    fillTitles();
                    fillChangedItemsValue(result.getNoCostItemList());
                    sheet.createFreezePane(colIndex, 1);
                    sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, colIndex));
                }

                logger.trace("saving Excel file");

                FileOutputStream fos = new FileOutputStream(reportFile);
                workbook.write(fos);

                fos.close();
                logger.trace("Excel file forming finished");

            } catch (Exception e) {
                logger.error("Ошибка сохранения отчёта", e);
                throw new RuntimeException(e);
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
        String[] titles = new String[]{"Направление", "Ответственный", "Заказной номер", "Артикул", "В прайсе", " Прайс",
                "Доступность", "Тип изменения", "Изменённое свойство", "Исходное значение", "    ", "Новое значение"};
        colIndex = 0;
        XSSFCell cell;
        for (String title : titles) {
            cell = row.createCell(colIndex++);
            fillCell(cell, title);
            cell.setCellStyle(ExcelCellStyleFactory.CELL_ALIGN_HLEFT_BOLD);
            sheet.autoSizeColumn(colIndex - 1);
        }
    }

    private void fillNewItemsValues(List<ImportedProduct> importedProductList) {
        XSSFRow row;

        for (ImportedProduct importedProduct : importedProductList) {
            row = sheet.createRow(rowNum++);

            Product newProduct = Products.getInstance().getProductByMaterial(importedProduct.getId());
            fillItemDetails(newProduct, row);
            fillCell(row.createCell(colIndex++), "added");
        }
    }

    private void fillChangedItemsValue(List<ChangedItem> changedItemList) {
        XSSFRow row;

        for (ChangedItem changedItem : changedItemList) {
            for (ChangedProperty changedProperty : changedItem.getChangedPropertyList()) {
                row = sheet.createRow(rowNum++);

                Product changedProduct = Products.getInstance().getProductByMaterial(changedItem.getId());
                fillItemDetails(changedProduct, row);
                fillChangeDetails(changedProperty, row);
            }
        }
    }

    private void fillItemDetails(Product item, XSSFRow row) {
        colIndex = 0;
        DataItem values[] = new DataItem[]{DATA_FAMILY_NAME, DATA_RESPONSIBLE, DATA_ORDER_NUMBER, DATA_ARTICLE, DATA_IS_IN_PRICE,
                DATA_IN_WHICH_PRICE_LIST, DATA_DCHAIN_WITH_COMMENT};
//        ExcelCellStyleFactory.init(workbook);
        for (DataItem dataItem : values) {
            dataItem.fillExcelCell(row.createCell(colIndex++), item, null);
        }
    }

    private void fillChangeDetails(ChangedProperty changedProperty, XSSFRow row) {
        colIndex = 7;

        fillCell(row.createCell(colIndex++), "changed");
        fillCell(row.createCell(colIndex++), changedProperty.getDataItem().getDisplayingName());
        fillCell(row.createCell(colIndex++), changedProperty.getOldValue().toString());
        fillCell(row.createCell(colIndex++), "->");
        fillCell(row.createCell(colIndex++), changedProperty.getNewValue().toString());
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
