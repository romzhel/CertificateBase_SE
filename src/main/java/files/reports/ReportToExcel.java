package files.reports;

import files.ExcelCellStyleFactory;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ui_windows.ExecutionIndicator;
import ui_windows.product.Product;
import ui_windows.product.data.DataItem;

import java.io.File;
import java.util.List;

public class ReportToExcel extends ReportToExcelTemplate {


    public File export(List<DataItem> columns, List<Product> items, File reportFile) {
        ExecutionIndicator.getInstance().start();
        this.reportFile = reportFile;
        workbook = new XSSFWorkbook();
        ExcelCellStyleFactory.init(workbook);
        XSSFSheet xssfSheet = (XSSFSheet) workbook.createSheet("Отчёт");

        int rowIndex = 0;
        XSSFRow xssfRow = xssfSheet.createRow(rowIndex++);
        XSSFCell xssfCell;

        int colIndex = 0;
        for (DataItem die : columns) {
            xssfCell = xssfRow.createCell(colIndex++, CellType.STRING);
            xssfCell.setCellValue(die.getDisplayingName());
            xssfSheet.autoSizeColumn(colIndex - 1);
        }

        for (Product product : items) {
            xssfRow = xssfSheet.createRow(rowIndex++);

            colIndex = 0;
            for (DataItem die : columns) {
                xssfCell = xssfRow.createCell(colIndex++);
                die.fillExcelCell(xssfCell, product, null);
            }
        }

        reportFile = saveToExcelFile();
        ExecutionIndicator.getInstance().stop();
        return reportFile;
    }


}
