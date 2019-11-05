package files;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;

public class ExcelCellStyleFactory {
    public static CellStyle CELL_ALIGN_LEFT;
    public static CellStyle CELL_ALIGN_LEFT_BOLD;
    public static CellStyle CELL_ALIGN_RIGHT;
    public static CellStyle CELL_ALIGN_CENTER;
    public static CellStyle CELL_CURRENCY_FORMAT;

    public ExcelCellStyleFactory(Workbook excelDoc) {
        init(excelDoc);
    }

    public void init(Workbook excelDoc) {
        CELL_ALIGN_LEFT = excelDoc.createCellStyle();
        CELL_ALIGN_LEFT.setAlignment(HorizontalAlignment.LEFT);

        CELL_ALIGN_LEFT_BOLD = excelDoc.createCellStyle();
        CELL_ALIGN_LEFT_BOLD.setAlignment(HorizontalAlignment.LEFT);
        Font exFont = excelDoc.getFontAt((short)0);
        Font font = excelDoc.createFont();
        font.setBold(true);
        font.setColor(exFont.getColor());
        font.setFontName(exFont.getFontName());
        font.setFontHeight(exFont.getFontHeight());
//        font.setColor(XSSFFont.DEFAULT_FONT_COLOR);
//        font.setFontName(XSSFFont.DEFAULT_FONT_NAME);
//        font.setFontHeight((short) 8);
        CELL_ALIGN_LEFT_BOLD.setFont(font);

        CELL_ALIGN_RIGHT = excelDoc.createCellStyle();
        CELL_ALIGN_RIGHT.setAlignment(HorizontalAlignment.RIGHT);

        CELL_ALIGN_CENTER = excelDoc.createCellStyle();
        CELL_ALIGN_CENTER.setAlignment(HorizontalAlignment.CENTER);

        CELL_CURRENCY_FORMAT = excelDoc.createCellStyle();
        DataFormat dataFormat = excelDoc.createDataFormat();
        CELL_CURRENCY_FORMAT.setDataFormat(dataFormat.getFormat("# ##0.00\\ [$€-x-euro1];[Red]# ##0.00\\ [$€-x-euro1]"));
    }
}
