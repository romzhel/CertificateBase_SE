package files.reports;

import core.CoreModule;
import core.Dialogs;
import files.ExcelCellStyleFactory;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ui_windows.main_window.MainWindow;
import ui_windows.product.Product;
import ui_windows.product.data.DataItem;
import ui_windows.request.CertificateRequestResult;
import utils.Utils;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class CertificateMatrixReportToExcel {
    private File file;
    private Workbook workbook;

    public CertificateMatrixReportToExcel(ArrayList<CertificateRequestResult> items) {
        workbook = new HSSFWorkbook();
        HSSFSheet sheet = (HSSFSheet) workbook.createSheet("Сертификаты");
        String[] titles = new String[]{"№ п/п", "Заказной номер", "Наименование", "Описание"};
        file = null;

        int rowNum = 0;
        HSSFCell cell;
        HSSFRow row;
        HSSFCellStyle cellStyle = (HSSFCellStyle) workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        int addColumns = 0;

        //fill data
        for (CertificateRequestResult rr : items) {
            rowNum++;
            row = sheet.createRow(rowNum);

            cell = row.createCell(0, CellType.STRING);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(rowNum);

            cell = row.createCell(1, CellType.STRING);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(rr.getProduct().getMaterial());

            cell = row.createCell(2, CellType.STRING);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(rr.getProduct().getArticle());

            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(rr.getProduct().getDescriptionRuEn());

            for (int i = 0; i < rr.getFiles().size(); i++) {
                cell = row.createCell(4 + i, CellType.STRING);
                cell.setCellValue(rr.getFiles().get(i).getName());
                final Hyperlink link = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
                String fgh = rr.getFiles().get(i).getName();
                link.setAddress(fgh);
                cell.setHyperlink(link);
                cell.setCellStyle(getHyperLinkStyle());

                addColumns = addColumns < rr.getFiles().size() ? rr.getFiles().size() : addColumns;
            }
        }

        //title row
        row = sheet.createRow(0);
        for (int col = 0; col < titles.length + addColumns; col++) {
            cell = row.createCell(col, CellType.STRING);
            cell.setCellStyle(cellStyle);
            if (col < titles.length) cell.setCellValue(titles[col]);
            else cell.setCellValue("Файл сертификата " + (col - 3));
            sheet.autoSizeColumn(col);
        }

        //output to file
        saveToExcelFile();
    }

    public File getFile() {
        return file;
    }

    private CellStyle getHyperLinkStyle() {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setUnderline(Font.U_SINGLE);
        font.setColor(IndexedColors.BLUE.index);
        style.setFont(font);

        return style;
    }

    private boolean saveToExcelFile() {
        String fileExtension = workbook instanceof HSSFWorkbook ? ".xls" : ".xlsx";
        file = new File(CoreModule.getFolders().getTempFolder().getPath() + "\\" +
                "Отчет_" + Utils.getDateTime().replaceAll(":", "-") + fileExtension);

        try {
            FileOutputStream outFile = new FileOutputStream(file);
            workbook.write(outFile);

            workbook.close();
            outFile.close();

            System.out.println("Created file: " + file.getAbsolutePath());
            openFile();
            return true;
        } catch (Exception e) {
            System.out.println("error of excel file creating " + e.getMessage());
            Dialogs.showMessage("Ошибка создания файла", e.getMessage());
            return false;
        }
    }

    public boolean openFile() {
        try {
            Desktop.getDesktop().open(file);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Dialogs.showMessage("Ошибка открытия файла", e.getMessage());
            return false;
        }
    }
}
