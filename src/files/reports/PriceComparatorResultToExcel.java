package files.reports;

import core.Dialogs;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import ui_windows.product.data.DataItem;
import utils.Utils;
import utils.comparation.prices.PriceComparatorResult;
import utils.comparation.prices.PriceComparatorResultItem;

import java.io.File;
import java.io.FileOutputStream;

import static ui_windows.product.data.DataItem.*;

public class PriceComparatorResultToExcel {


    public void export(File file, PriceComparatorResult result) {
        if (file != null) {
            SXSSFWorkbook workbook = new SXSSFWorkbook();
            SXSSFSheet sheet = workbook.createSheet("Price comparison");
            SXSSFRow row;
            SXSSFCell cell;

            String[] titles = new String[] {"Направление", "Заказной номер [для печати]", "Артикул", "Описание"};

            DataItem[] dataItems = new DataItem[]{DATA_FAMILY_NAME, DATA_ORDER_NUMBER, DATA_ARTICLE, DATA_DESCRIPTION_RU};

            int rowIndex = 0;
            int colIndex = 0;
            row = sheet.createRow(rowIndex++);
            for (int i = 0; i < titles.length; i++) {
                cell = row.createCell(colIndex++, CellType.STRING);
                cell.setCellValue(titles[i]);
            }

            for (int i = 0; i < result.getSheetNames().size(); i++) {
                cell = row.createCell(colIndex++, CellType.STRING);
                cell.setCellValue(result.getSheetNames().get(i));
            }

            for (PriceComparatorResultItem pcri : result.getItems()) {
                row = sheet.createRow(rowIndex++);
                colIndex = 0;
                for (DataItem dataItem : dataItems) {
                    cell = row.createCell(colIndex++, CellType.STRING);
                    cell.setCellValue((String) dataItem.getValue(pcri.getProduct()));
                }


                for (PriceComparatorResultItem.PriceComparatorResultDetails pcrd : pcri.getDetails()) {
                    cell = row.createCell(colIndex + pcrd.getSheetIndex(), CellType.STRING);
                    cell.setCellValue(pcrd.getComment());
                }

            }

            /*for (int i = 0; i < titles.length + result.getSheetNames().size(); i++) {
                sheet.autoSizeColumn(i);
            }*/

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                workbook.write(fos);
                fos.close();
                workbook.close();

                Utils.openFile(file);
            } catch (Exception e) {
                Dialogs.showMessage("Сохранение файла", "Ошибка: " + e.getMessage());
            }

        }
    }
}
