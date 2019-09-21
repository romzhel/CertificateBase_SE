package files;

import core.CoreModule;
import core.Dialogs;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import ui_windows.product.Product;
import ui_windows.options_window.certificates_editor.Certificate;
import ui_windows.options_window.certificates_editor.CertificateCheckingResult;
import ui_windows.product.certificatesChecker.CertificateVerificationItem;
import ui_windows.request_certificates.RequestResult;
import utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ExportToExcel {
    private File file;
    private HSSFWorkbook workbook;

    public ExportToExcel(ArrayList<RequestResult> items) {
        workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Сертификаты");
        String[] titles = new String[]{"№ п/п", "Заказной номер", "Наименование", "Описание"};
        file = null;

        int rowNum = 0;
        Cell cell;
        Row row;
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        int addColumns = 0;

        //fill data
        for (RequestResult rr : items) {
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
            cell.setCellValue(rr.getProduct().getDescriptionru());

            for (int i = 0; i < rr.getFiles().size(); i++) {
                cell = row.createCell(4 + i, CellType.STRING);
                cell.setCellValue(rr.getFiles().get(i).getName());
                final Hyperlink link = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
                link.setAddress(rr.getFiles().get(i).getName());
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
//        if (!CoreModule.getFolders().getTempFolder().exists()) {
//            if (!CoreModule.getFolders().getTempFolder().mkdir()) {
//                Dialogs.showMessage("Создание временной папки", "Не удалось создать временную папку\n" +
//                        CoreModule.getFolders().getTempFolder() + "\n\nОперация не может быть выполнена.");
//                return;
//            }
//        }
//
//        file = new File(CoreModule.getFolders().getTempFolder().getPath() + "\\" +
//                "Сертификаты_" + Utils.getDateTime().replaceAll(":", "-") + ".xls");
//
//        try {
//            FileOutputStream outFile = new FileOutputStream(file);
//            workbook.write(outFile);
//
//            workbook.close();
//            outFile.close();
//        } catch (Exception e) {
//            System.out.println("Something wrong...." + e.getMessage());
//        }
//
//        System.out.println("Created file: " + file.getAbsolutePath());
    }

    public ExportToExcel(CertificateCheckingResult certCheckResult) {
        workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Сертификаты с несоответствиями");
        String[] titles = new String[]{"Сертификат", "Заказной номер", "Наименование", "Описание", "Поставки",
                "Окончание продаж"};
        file = null;

        int rowNum = 0;
        Cell cell;
        Row row;
        HSSFCellStyle cellStyle = workbook.createCellStyle();
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

                    if (certVer.getCertificate().equals(curCert) && certVer.getProduct().equals(product)){

                        if (!curProductwasDisplayed){
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
            if (col != 0)  sheet.autoSizeColumn(col);
        }
        sheet.setColumnWidth(0, 5000);

        //output to file
        saveToExcelFile();
    }

    public File getFile() {
        return file;
    }

    private HSSFCellStyle getHyperLinkStyle() {
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setUnderline(HSSFFont.U_SINGLE);
        font.setColor(HSSFColor.BLUE.index);
        style.setFont(font);

        return style;
    }

    private HSSFCellStyle getBoldStyle() {
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        return style;
    }

    private HSSFCellStyle getBoldCenterStyle() {
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);

        return style;
    }

    private HSSFCellStyle getBoldBlueStyle() {
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setColor(HSSFColor.BLUE.index);
        style.setFont(font);

        return style;
    }

    private HSSFCellStyle getBoldCenterBlueStyle() {
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        font.setColor(HSSFColor.BLUE.index);
        style.setAlignment(HorizontalAlignment.CENTER);

        return style;
    }

    private HSSFCellStyle getBlueStyle() {
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.BLUE.index);
        style.setFont(font);

        return style;
    }

    private HSSFCellStyle getCenterBlueStyle() {
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        style.setFont(font);
        font.setColor(HSSFColor.BLUE.index);
        style.setAlignment(HorizontalAlignment.CENTER);

        return style;
    }

    private boolean saveToExcelFile(){
        if (!CoreModule.getFolders().getTempFolder().exists()) {
            if (!CoreModule.getFolders().getTempFolder().mkdir()) {
                Dialogs.showMessage("Создание временной папки", "Не удалось создать временную папку\n" +
                        CoreModule.getFolders().getTempFolder() + "\n\nОперация не может быть выполнена.");
                return false;
            }
        }

        file = new File(CoreModule.getFolders().getTempFolder().getPath() + "\\" +
                "Сертификаты_" + Utils.getDateTime().replaceAll(":", "-") + ".xls");

        try {
            FileOutputStream outFile = new FileOutputStream(file);
            workbook.write(outFile);

            workbook.close();
            outFile.close();
        } catch (Exception e) {
            System.out.println("error of excel file creating " + e.getMessage());
        }

        System.out.println("Created file: " + file.getAbsolutePath());

        return true;
    }
}
