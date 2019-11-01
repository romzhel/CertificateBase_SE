package files;

import core.CoreModule;
import core.Dialogs;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.*;
import ui_windows.options_window.certificates_editor.Certificate;
import ui_windows.options_window.certificates_editor.CertificateCheckingResult;
import ui_windows.product.Product;
import ui_windows.product.certificatesChecker.CertificateVerificationItem;
import ui_windows.product.data.DataItem;
import ui_windows.request.CertificateRequestResult;
import utils.Utils;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ExportToExcel {
    private File file;
    private Workbook workbook;

    public ExportToExcel(ArrayList<CertificateRequestResult> items) {
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

    public ExportToExcel(CertificateCheckingResult certCheckResult) {
       /* workbook = new XSSFWorkbook();
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet("Сертификаты с несоответствиями");
        String[] titles = new String[]{"Сертификат", "Заказной номер", "Наименование", "Описание", "Поставки",
                "Окончание продаж"};
        file = null;

        int rowNum = 0;
        Cell cell;
        Row row;
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        //fill data

        for (Certificate curCert : certCheckResult.getProblemCertificates()) {
            rowNum++;
            row = sheet.createRow(++rowNum);
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(curCert.getFileName());
            final Hyperlink link = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
            link.setAddress(curCert.getFileName());
            cell.setHyperlink(link);
            cell.setCellStyle(getHyperLinkStyle());

            boolean curProductwasDisplayed;
            for (Product product : certCheckResult.getProblemProducts()) {
                curProductwasDisplayed = false;

                for (CertificateVerificationItem certVer : certCheckResult.getProblemCv()) {

                    if (certVer.getCertificate().equals(curCert) && certVer.getProduct().equals(product)) {

                        if (!curProductwasDisplayed) {
                            rowNum++;
                            row = sheet.createRow(++rowNum);

                            cell = row.createCell(1, CellType.STRING);
                            cell.setCellStyle(cellStyle);
                            cell.setCellValue(product.getMaterial());

                            cell = row.createCell(2, CellType.STRING);
                            cell.setCellStyle(cellStyle);
                            cell.setCellValue(product.getArticle());

                            cell = row.createCell(3, CellType.STRING);
                            cell.setCellValue(product.getDescriptionru());

                            cell = row.createCell(4, CellType.STRING);
                            cell.setCellStyle(cellStyle);
                            cell.setCellValue(product.getOrderableStatus());

                            cell = row.createCell(5, CellType.STRING);
                            cell.setCellStyle(cellStyle);
                            cell.setCellValue(product.getEndofservice());

                            curProductwasDisplayed = true;
                        }

                        row = sheet.createRow(++rowNum);

                        cell = row.createCell(2, CellType.STRING);
                        cell.setCellStyle(getCenterBlueStyle());
                        cell.setCellValue(certVer.getMatchedPart());

                        cell = row.createCell(3, CellType.STRING);
                        cell.setCellStyle(getCenterBlueStyle());
                        cell.setCellValue(certVer.getProdType());

                        cell = row.createCell(4, CellType.STRING);
                        cell.setCellStyle(getCenterBlueStyle());
                        cell.setCellValue(certVer.getStatus());
                    }
                }
            }
        }

        //title row
        row = sheet.createRow(0);
        for (int col = 0; col < titles.length; col++) {
            cell = row.createCell(col, CellType.STRING);
            cell.setCellStyle(getBoldCenterStyle());
            if (col < titles.length) cell.setCellValue(titles[col]);
            if (col != 0) sheet.autoSizeColumn(col);
        }
        sheet.setColumnWidth(0, 5000);

        //output to file
        saveToExcelFile();*/
    }

    public ExportToExcel(ArrayList<DataItem> columns, ArrayList<Product> items) {
        workbook = new XSSFWorkbook();
        XSSFSheet xssfSheet = (XSSFSheet) workbook.createSheet("Отчёт");

        int rowIndex = 0;
        XSSFRow xssfRow = xssfSheet.createRow(rowIndex++);
        XSSFCell xssfCell;

        int colIndex = 0;
        for (DataItem column : columns) {
            xssfCell = xssfRow.createCell(colIndex++, CellType.STRING);
            xssfCell.setCellValue(column.getDisplayingName());
            xssfSheet.autoSizeColumn(colIndex - 1);
        }


        for (Product product : items) {
            xssfRow = xssfSheet.createRow(rowIndex++);

            colIndex = 0;
            for (DataItem column : columns) {
                column.createXssfCell(product, xssfRow, colIndex++, null);
            }
        }

        saveToExcelFile();
        openFile();
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
/*
    private CellStyle getBoldStyle() {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        return style;
    }

    private XSSFCellStyle getBoldCenterStyle() {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);

        return style;
    }

    private XSSFCellStyle getBoldBlueStyle() {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setColor(HSSFColor.BLUE.index);
        style.setFont(font);

        return style;
    }

    private XSSFCellStyle getBoldCenterBlueStyle() {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        font.setColor(HSSFColor.BLUE.index);
        style.setAlignment(HorizontalAlignment.CENTER);

        return style;
    }

    private XSSFCellStyle getBlueStyle() {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.BLUE.index);
        style.setFont(font);

        return style;
    }

    private XSSFCellStyle getCenterBlueStyle() {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        style.setFont(font);
        font.setColor(HSSFColor.BLUE.index);
        style.setAlignment(HorizontalAlignment.CENTER);

        return style;
    }*/

    private boolean saveToExcelFile() {
        String fileExtension = workbook instanceof HSSFWorkbook ? ".xls" : ".xlsx";
        file = new File(CoreModule.getFolders().getTempFolder().getPath() + "\\" +
                "Сертификаты_" + Utils.getDateTime().replaceAll(":", "-") + fileExtension);

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
