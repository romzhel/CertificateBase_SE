package files;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelCellStyleFactory {
    public static CellStyle CELL_ALIGN_HLEFT;
    public static CellStyle CELL_ALIGN_HLEFT_VCENTER;
    public static CellStyle CELL_ALIGN_HLEFT_VCENTER_WRAP;
    public static CellStyle CELL_ALIGN_HLEFT_BOLD;
    public static CellStyle CELL_ALIGN_HLEFT_WRAP;
    public static CellStyle CELL_ALIGN_HRIGHT;
    public static CellStyle CELL_ALIGN_HRIGHT_VCENTER;
    public static CellStyle CELL_ALIGN_HCENTER;
    public static CellStyle CELL_ALIGN_HCENTER_BOLD;
    public static CellStyle CELL_ALIGN_HCENTER_HCENTER;
    public static CellStyle CELL_CURRENCY_FORMAT;
    public static CellStyle CELL_CURRENCY_FORMAT_VCENTER;

    public static HSSFCellStyle CELL_STYLE_HCENTER_BOLD;
    public static HSSFCellStyle CELL_STYLE_HCENTER;
    public static HSSFCellStyle CELL_STYLE_HLEFT_GRAY;
    public static HSSFCellStyle CELL_STYLE_HCENTER_GRAY;
    public static HSSFCellStyle CELL_STYLE_HLEFT;

    public ExcelCellStyleFactory() {
    }

    public static void init(Workbook excelDoc) {
        if (excelDoc instanceof XSSFWorkbook) {
            CELL_ALIGN_HLEFT = excelDoc.createCellStyle();
            CELL_ALIGN_HLEFT.setAlignment(HorizontalAlignment.LEFT);

            CELL_ALIGN_HLEFT_VCENTER = excelDoc.createCellStyle();
            CELL_ALIGN_HLEFT_VCENTER.setAlignment(HorizontalAlignment.LEFT);
            CELL_ALIGN_HLEFT_VCENTER.setVerticalAlignment(VerticalAlignment.CENTER);

            CELL_ALIGN_HLEFT_VCENTER_WRAP = excelDoc.createCellStyle();
            CELL_ALIGN_HLEFT_VCENTER_WRAP.setAlignment(HorizontalAlignment.LEFT);
            CELL_ALIGN_HLEFT_VCENTER_WRAP.setVerticalAlignment(VerticalAlignment.CENTER);
            CELL_ALIGN_HLEFT_VCENTER_WRAP.setWrapText(true);

            CELL_ALIGN_HLEFT_BOLD = excelDoc.createCellStyle();
            CELL_ALIGN_HLEFT_BOLD.setAlignment(HorizontalAlignment.LEFT);
            Font exFont = excelDoc.getFontAt((short) 0);
            Font font = excelDoc.createFont();
            font.setBold(true);
            font.setColor(exFont.getColor());
            font.setFontName(exFont.getFontName());
            font.setFontHeight(exFont.getFontHeight());
//        font.setColor(XSSFFont.DEFAULT_FONT_COLOR);
//        font.setFontName(XSSFFont.DEFAULT_FONT_NAME);
//        font.setFontHeight((short) 8);
            CELL_ALIGN_HLEFT_BOLD.setFont(font);

            CELL_ALIGN_HLEFT_WRAP = excelDoc.createCellStyle();
            CELL_ALIGN_HLEFT_WRAP.setAlignment(HorizontalAlignment.LEFT);
            CELL_ALIGN_HLEFT_WRAP.setWrapText(true);

            CELL_ALIGN_HRIGHT = excelDoc.createCellStyle();
            CELL_ALIGN_HRIGHT.setAlignment(HorizontalAlignment.RIGHT);

            CELL_ALIGN_HRIGHT_VCENTER = excelDoc.createCellStyle();
            CELL_ALIGN_HRIGHT_VCENTER.setAlignment(HorizontalAlignment.RIGHT);
            CELL_ALIGN_HRIGHT_VCENTER.setVerticalAlignment(VerticalAlignment.CENTER);

            CELL_ALIGN_HCENTER = excelDoc.createCellStyle();
            CELL_ALIGN_HCENTER.setAlignment(HorizontalAlignment.CENTER);

            CELL_ALIGN_HCENTER_HCENTER = excelDoc.createCellStyle();
            CELL_ALIGN_HCENTER_HCENTER.setAlignment(HorizontalAlignment.CENTER);
            CELL_ALIGN_HCENTER_HCENTER.setVerticalAlignment(VerticalAlignment.CENTER);

            CELL_ALIGN_HCENTER_BOLD = excelDoc.createCellStyle();
            CELL_ALIGN_HCENTER_BOLD.setAlignment(HorizontalAlignment.CENTER);
            Font font2 = excelDoc.createFont();
            font2.setBold(true);
            font2.setColor(excelDoc.getFontAt((short) 0).getColor());
            font2.setFontName(excelDoc.getFontAt((short) 0).getFontName());
            font2.setFontHeight(excelDoc.getFontAt((short) 0).getFontHeight());

            CELL_CURRENCY_FORMAT = excelDoc.createCellStyle();
            DataFormat dataFormat = excelDoc.createDataFormat();
            CELL_CURRENCY_FORMAT.setDataFormat(dataFormat.getFormat("# ##0.00\\ [$€-x-euro1];[Red]# ##0.00\\ [$€-x-euro1]"));

            CELL_CURRENCY_FORMAT_VCENTER = excelDoc.createCellStyle();
            CELL_CURRENCY_FORMAT_VCENTER.setDataFormat(dataFormat.getFormat("# ##0.00\\ [$€-x-euro1];[Red]# ##0.00\\ [$€-x-euro1]"));
            CELL_CURRENCY_FORMAT_VCENTER.setVerticalAlignment(VerticalAlignment.CENTER);
        } else if (excelDoc instanceof HSSFWorkbook) {
            //HSSFcellStyles
            CELL_STYLE_HCENTER = (HSSFCellStyle) excelDoc.createCellStyle();
            CELL_STYLE_HCENTER.setAlignment(HorizontalAlignment.CENTER);

            CELL_STYLE_HLEFT = (HSSFCellStyle) excelDoc.createCellStyle();
            CELL_STYLE_HLEFT.setAlignment(HorizontalAlignment.LEFT);

            CELL_STYLE_HCENTER_BOLD = (HSSFCellStyle) excelDoc.createCellStyle();
            CELL_STYLE_HCENTER_BOLD.setAlignment(HorizontalAlignment.CENTER);
            HSSFFont boldFont = ((HSSFWorkbook) excelDoc).createFont();
            boldFont.setBold(true);
            CELL_STYLE_HCENTER_BOLD.setFont(boldFont);

            CELL_STYLE_HCENTER_GRAY = (HSSFCellStyle) excelDoc.createCellStyle();
            CELL_STYLE_HCENTER_GRAY.setAlignment(HorizontalAlignment.CENTER);
            HSSFFont invisibleFont = ((HSSFWorkbook) excelDoc).createFont();
            invisibleFont.setColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
            CELL_STYLE_HCENTER_GRAY.setFont(invisibleFont);

            CELL_STYLE_HLEFT_GRAY = (HSSFCellStyle) excelDoc.createCellStyle();
            CELL_STYLE_HLEFT_GRAY.setAlignment(HorizontalAlignment.LEFT);
            invisibleFont.setColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
            CELL_STYLE_HLEFT_GRAY.setFont(invisibleFont);
        }
    }
}
