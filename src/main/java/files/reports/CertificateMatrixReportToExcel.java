package files.reports;

import core.reports.CertificatesReportResult;
import files.ExcelCellStyleFactory;
import files.Folders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import ui_windows.product.Products;
import utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static files.ExcelCellStyleFactory.*;

public class CertificateMatrixReportToExcel {
    private static final Logger logger = LogManager.getLogger(CertificateMatrixReportToExcel.class);
    public static CellStyle HYPERLINK_STYLE;
    int rowNum;
    private File file;
    private HSSFWorkbook workbook;

    public CertificateMatrixReportToExcel(Collection<CertificatesReportResult> productCertificatesReport) throws Exception {
        logger.trace("запуск создания отчёта в Excel");
        workbook = new HSSFWorkbook();
        ExcelCellStyleFactory.init(workbook);

        HYPERLINK_STYLE = getHyperLinkStyle();
        HSSFSheet sheet = (HSSFSheet) workbook.createSheet("Сертификаты");
        String[] titles = new String[]{"№ п/п", "Заказной номер", "Наименование", "Описание"};
        int[] width = new int[]{2500, 5000, 5000, 10000};
        file = null;

        rowNum = 0;
        HSSFCell cell;
        HSSFRow row;

        HashMap<String, Integer> displayedNorms = new HashMap<>();

        //title row
        row = sheet.createRow(0);
        for (int column = 0; column < titles.length; column++) {
            cell = row.createCell(column, CellType.STRING);
            cell.setCellStyle(ExcelCellStyleFactory.CELL_STYLE_HCENTER_BOLD);
            cell.setCellValue(titles[column]);
            sheet.setColumnWidth(column, width[column]);
        }

        //fill data
        int itemIndex = 0;
        for (CertificatesReportResult crr : productCertificatesReport) {
//            logger.trace("добавление сведений по {}", crr.getProduct());
            row = sheet.createRow(++rowNum);
            fillProductData(row, new HSSFCellStyle[]{CELL_STYLE_HCENTER, CELL_STYLE_HLEFT}, ++itemIndex, crr);

            int rowOffset = 0;
            int col;
            for (Map.Entry<String, Set<Path>> normAndCertFiles : crr.getCertFilesGroupedByNorms().entrySet()) {
                if (normAndCertFiles.getValue().size() == 0) {
                    continue;
                }

                if (displayedNorms.containsKey(normAndCertFiles.getKey())) {
                    col = displayedNorms.get(normAndCertFiles.getKey());
                } else {
                    col = displayedNorms.values().stream().max(Integer::compareTo).orElse(titles.length - 1) + 1;
                    displayedNorms.put(normAndCertFiles.getKey(), col);
                    HSSFRow titleRow = sheet.getRow(0);
                    cell = titleRow.createCell(col, CellType.STRING);
                    cell.setCellStyle(ExcelCellStyleFactory.CELL_STYLE_HCENTER_BOLD);
                    cell.setCellValue("регламент " + normAndCertFiles.getKey());
                }

                int fileRowOffset = 0;
                for (Path fileName : normAndCertFiles.getValue()) {
                    row = sheet.getRow(rowNum);

//                    int fileRowOffset = normAndCertFiles.getValue().indexOf(fileName);
                    if (fileRowOffset > 0) {
                        row = sheet.getRow(rowNum + fileRowOffset);
                        row = row == null ? sheet.createRow(rowNum + fileRowOffset) : row;
                        rowOffset = Math.max(rowOffset, fileRowOffset);
                        fillProductData(row, new HSSFCellStyle[]{CELL_STYLE_HCENTER_GRAY, CELL_STYLE_HLEFT_GRAY}, itemIndex, crr);
                    }

                    cell = row.createCell(col, CellType.STRING);
                    cell.setCellValue(fileName.toString());
                    final Hyperlink link = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
                    link.setAddress(fileName.toString());
                    cell.setHyperlink(link);
                    cell.setCellStyle(HYPERLINK_STYLE);
                    fileRowOffset++;
                }
            }
            rowNum += rowOffset;
        }

        int maxColIndex = displayedNorms.values().stream().max(Integer::compareTo).orElse(0);
        for (int colIndex = titles.length; colIndex <= maxColIndex; colIndex++) {
            sheet.autoSizeColumn(colIndex);
        }

        sheet.createFreezePane(0, 1);
        int lastCol = displayedNorms.values().stream().max(Integer::compareTo).orElse(titles.length - 1);
        sheet.setAutoFilter(new CellRangeAddress(0, rowNum, 0, lastCol));

        //output to file
        saveToExcelFile();
    }

    private int fillProductData(HSSFRow row, HSSFCellStyle[] styles, int itemIndex, CertificatesReportResult crr) {
        HSSFCell cell;
        int locCol = 0;

        HSSFCellStyle cellCenterStyle = styles[0];
        HSSFCellStyle cellLeftStyle = styles[1];

        cell = row.createCell(locCol++, CellType.STRING);
        cell.setCellStyle(cellCenterStyle);
        cell.setCellValue(itemIndex);

        cell = row.createCell(locCol++, CellType.STRING);
        cell.setCellStyle(cellCenterStyle);
        cell.setCellValue(crr.getProduct().getMaterial());

        cell = row.createCell(locCol++, CellType.STRING);
        cell.setCellStyle(cellCenterStyle);
        cell.setCellValue(crr.getProduct().getArticle());

        cell = row.createCell(locCol++, CellType.STRING);
        cell.setCellStyle(cellLeftStyle);
        cell.setCellValue(Products.getInstance().getDescriptionRuEn(crr.getProduct()));

        //избегаем отображения текста на соседней ячейке
        cell = row.createCell(locCol, CellType.STRING);
        cell.setCellValue("");
        return itemIndex;
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

    private void saveToExcelFile() throws Exception {
        logger.trace("сохранение Exсel в файл");
        String fileExtension = workbook instanceof HSSFWorkbook ? ".xls" : ".xlsx";
        file = Folders.getInstance().getTempFolder()
                .resolve("Отчет_" + Utils.getDateTime().replaceAll(":", "-") + fileExtension)
                .toFile();

//        try {
        FileOutputStream outFile = new FileOutputStream(file);
        workbook.write(outFile);

        workbook.close();
        outFile.close();
//        } catch (Exception e) {
//            System.out.println("error of excel file creating " + e.getMessage());
//            Dialogs.showMessage("Ошибка создания файла", e.getMessage());
//            return false;
//        }
    }
}
