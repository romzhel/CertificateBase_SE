package files;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelCellStyleFactory {
    public static CellStyle CELL_ALIGN_LEFT;
    public static CellStyle CELL_ALIGN_LEFT_BOLD;
    public static CellStyle CELL_ALIGN_RIGHT;
    public static CellStyle CELL_ALIGN_CENTER;
    public static CellStyle CELL_ALIGN_CENTER_BOLD;
    public static CellStyle CELL_CURRENCY_FORMAT;

    public static HSSFCellStyle CELL_STYLE_CENTER_BOLD;
    public static HSSFCellStyle CELL_STYLE_CENTER;
    public static HSSFCellStyle CELL_STYLE_LEFT_GRAY;
    public static HSSFCellStyle CELL_STYLE_CENTER_GRAY;
    public static HSSFCellStyle CELL_STYLE_LEFT;

    public ExcelCellStyleFactory() {
    }

    public static void init(Workbook excelDoc) {
        if (excelDoc instanceof XSSFWorkbook) {
            CELL_ALIGN_LEFT = excelDoc.createCellStyle();
            CELL_ALIGN_LEFT.setAlignment(HorizontalAlignment.LEFT);

            CELL_ALIGN_LEFT_BOLD = excelDoc.createCellStyle();
            CELL_ALIGN_LEFT_BOLD.setAlignment(HorizontalAlignment.LEFT);
            Font exFont = excelDoc.getFontAt((short) 0);
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

            CELL_ALIGN_CENTER_BOLD = excelDoc.createCellStyle();
            CELL_ALIGN_CENTER_BOLD.setAlignment(HorizontalAlignment.CENTER);
            Font font2 = excelDoc.createFont();
            font2.setBold(true);
            font2.setColor(excelDoc.getFontAt((short) 0).getColor());
            font2.setFontName(excelDoc.getFontAt((short) 0).getFontName());
            font2.setFontHeight(excelDoc.getFontAt((short) 0).getFontHeight());

            CELL_CURRENCY_FORMAT = excelDoc.createCellStyle();
            DataFormat dataFormat = excelDoc.createDataFormat();
            CELL_CURRENCY_FORMAT.setDataFormat(dataFormat.getFormat("# ##0.00\\ [$€-x-euro1];[Red]# ##0.00\\ [$€-x-euro1]"));
        } else if (excelDoc instanceof HSSFWorkbook) {
            //HSSFcellStyles
            CELL_STYLE_CENTER = (HSSFCellStyle) excelDoc.createCellStyle();
            CELL_STYLE_CENTER.setAlignment(HorizontalAlignment.CENTER);

            CELL_STYLE_LEFT = (HSSFCellStyle) excelDoc.createCellStyle();
            CELL_STYLE_LEFT.setAlignment(HorizontalAlignment.LEFT);

            CELL_STYLE_CENTER_BOLD = (HSSFCellStyle) excelDoc.createCellStyle();
            CELL_STYLE_CENTER_BOLD.setAlignment(HorizontalAlignment.CENTER);
            HSSFFont boldFont = ((HSSFWorkbook) excelDoc).createFont();
            boldFont.setBold(true);
            CELL_STYLE_CENTER_BOLD.setFont(boldFont);

            CELL_STYLE_CENTER_GRAY = (HSSFCellStyle) excelDoc.createCellStyle();
            CELL_STYLE_CENTER_GRAY.setAlignment(HorizontalAlignment.CENTER);
            HSSFFont invisibleFont = ((HSSFWorkbook) excelDoc).createFont();
            invisibleFont.setColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
            CELL_STYLE_CENTER_GRAY.setFont(invisibleFont);

            CELL_STYLE_LEFT_GRAY = (HSSFCellStyle) excelDoc.createCellStyle();
            CELL_STYLE_LEFT_GRAY.setAlignment(HorizontalAlignment.LEFT);
            invisibleFont.setColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
            CELL_STYLE_LEFT_GRAY.setFont(invisibleFont);
        }
    }
}
