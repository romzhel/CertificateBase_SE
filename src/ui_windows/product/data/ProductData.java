package ui_windows.product.data;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.util.ArrayList;
import java.util.Arrays;

import static files.price_to_excel.ExportPriceListToExcel_SE.*;
import static ui_windows.product.data.ProductProperties.*;
import static ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet.LANG_RU;

public class ProductData {
    public static final DataItem DATA_ORDER_NUMBER_PRINT = new DataItem(DESC_ORDER_NUMBER_PRINT, FIELD_ORDER_NUMBER_PRINT);
    public static final DataItem DATA_ORDER_NUMBER = new DataItem(DESC_ORDER_NUMBER, FIELD_ORDER_NUMBER);
    public static final DataItem DATA_ARTICLE = new DataItem(DESC_ARTICLE, FIELD_ARTICLE);
    public static final DataItem DATA_DESCRIPTION_RU = new DataItem(DESC_DESCRIPTION_RU, FIELD_DESCRIPTION_RU);
    public static final DataItem DATA_DESCRIPTION_EN = new DataItem(DESC_DESCRIPTION_EN, FIELD_DESCRIPTION_EN);
    public static final DataItem DATA_LOCAL_PRICE = new DataItem(DESC_LOCAL_PRICE, FIELD_LOCAL_PRICE);
    public static final DataItem DATA_LEAD_TIME = new DataItem(DESC_LEADTIME, FIELD_LEADTIME);
    public static final DataItem DATA_MIN_ORDER = new DataItem(DESC_MIN_ORDER, FIELD_MIN_ORDER);
    public static final DataItem DATA_LGBK = new DataItem(DESC_LGBK, FIELD_LGBK);
    public static final DataItem DATA_HIERARCHY = new DataItem(DESC_HIERARCHY, FIELD_HIERARCHY);
    public static final DataItem DATA_WEIGHT = new DataItem(DESC_WEIGHT, FIELD_WEIGHT);
    public static final DataItem DATA_COUNTRY = new DataItem(DESC_COUNTRY, FIELD_COUNTRY);
    public static final DataItem DATA_LOGISTIC = new DataItem(DESC_LOGISTIC_LIMITATION, FIELD_LOGISTIC_LIMITATION);
    public static final DataItem DATA_SERVICE_END = new DataItem(DESC_SERVICE_END, FIELD_SERVICE_END);
    public static final DataItem DATA_DCHAIN = new DataItem(DESC_DCHAIN, FIELD_DCHAIN);
    public static final DataItem DATA_DCHAIN_COMMENT = new DataItem(DESC_DCHAIN_COMMENT, null);
    public static final DataItem DATA_PACKSIZE = new DataItem(DESC_PACKSIZE, FIELD_PACKSIZE);
    public static final DataItem DATA_FAMILY = new DataItem(DESC_FAMILY, FIELD_FAMILY);
    public static final DataItem DATA_IS_IN_PRICE = new DataItem(DESC_PRICE, FIELD_PRICE);
    public static final DataItem DATA_COMMENT = new DataItem(DESC_COMMENT, FIELD_COMMENT);
    public static final DataItem DATA_REPLACEMENT = new DataItem(DESC_REPLACEMENT, FIELD_REPLACEMENT);
    public static final DataItem DATA_TYPE = new DataItem(DESC_TYPE, FIELD_TYPE);

    private static ProductData instance;

    private ProductData() {
        DATA_ORDER_NUMBER_PRINT.setExcelCellValueFactory(param -> {
            XSSFCell cell = param.getRow().createCell(param.getIndex(), CellType.STRING);
            cell.setCellValue(param.getProduct().getProductForPrint().isEmpty() ? param.getProduct().getMaterial() :
                    param.getProduct().getProductForPrint());
            cell.setCellStyle(CELL_ALIGN_LEFT);

            return cell;
        });
        DATA_ARTICLE.setExcelCellValueFactory(param -> {
            XSSFCell cell = param.getRow().createCell(param.getIndex(), CellType.STRING);
            cell.setCellValue(param.getProduct().getArticle());
            cell.setCellStyle(CELL_ALIGN_LEFT);

            return cell;
        });
        DATA_DESCRIPTION_RU.setExcelCellValueFactory(param -> {
            XSSFCell cell = param.getRow().createCell(param.getIndex(), CellType.STRING);
            cell.setCellValue(param.getProduct().getDescriptionRuEn());
            cell.setCellStyle(CELL_ALIGN_LEFT);

            return cell;
        });
        DATA_DESCRIPTION_EN.setExcelCellValueFactory(param -> {
            XSSFCell cell = param.getRow().createCell(param.getIndex(), CellType.STRING);
            cell.setCellValue(param.getProduct().getDescriptionen());
            cell.setCellStyle(CELL_ALIGN_LEFT);

            return cell;
        });
        DATA_LOCAL_PRICE.setExcelCellValueFactory(param -> {
            XSSFCell cell = null;
            if (param.getProduct().getLocalPrice() > 0) {
                cell = param.getRow().createCell(param.getIndex(), CellType.NUMERIC);
                if (param.getPriceListSheet() != null) {
                    double correction = 1D - ((double) param.getPriceListSheet().getDiscount() / 100);
                    if (correction > 0.4) {
                        cell.setCellValue(param.getProduct().getLocalPrice() * correction);
                    } else {
                        System.out.println("price list sheet " + param.getPriceListSheet().getSheetName() + ", discount = " + ((int) correction * 100) + " %");
                        cell.setCellValue(param.getProduct().getLocalPrice());
                    }
                } else {
                    cell.setCellValue(param.getProduct().getLocalPrice());
                }
                cell.setCellStyle(CELL_CURRENCY_FORMAT);
            } else {
                cell = param.getRow().createCell(param.getIndex(), CellType.STRING);
                if (param.getPriceListSheet().getLanguage() == LANG_RU) {
                    cell.setCellValue("По запросу");
                } else {
                    cell.setCellValue("By request");
                }
            }

            return cell;
        });
        DATA_LEAD_TIME.setExcelCellValueFactory(param -> {
            XSSFCell cell = param.getRow().createCell(param.getIndex(), CellType.NUMERIC);
            cell.setCellValue(param.getProduct().getPreparedLeadTime());
            cell.setCellStyle(CELL_ALIGN_CENTER);

            return cell;
        });
        DATA_MIN_ORDER.setExcelCellValueFactory(param -> {
            XSSFCell cell = param.getRow().createCell(param.getIndex(), CellType.NUMERIC);
            cell.setCellValue(param.getProduct().getMinOrder());
            cell.setCellStyle(CELL_ALIGN_CENTER);

            return cell;
        });
        DATA_LGBK.setExcelCellValueFactory(param -> {
            XSSFCell cell = param.getRow().createCell(param.getIndex(), CellType.STRING);
            cell.setCellValue(param.getProduct().getLgbk());
            cell.setCellStyle(CELL_ALIGN_CENTER);

            return cell;
        });
        DATA_WEIGHT.setExcelCellValueFactory(param -> {
            XSSFCell cell = param.getRow().createCell(param.getIndex(), CellType.STRING);
            cell.setCellValue(param.getProduct().getWeight());
            cell.setCellStyle(CELL_ALIGN_RIGHT);

            return cell;
        });
    }

    public static void init() {
        if (instance == null) {
            instance = new ProductData();
        }
    }

    public static ArrayList<DataItem> getColumnsForPriceList() {
        return new ArrayList<>(Arrays.asList(DATA_ORDER_NUMBER_PRINT, DATA_ARTICLE, DATA_DESCRIPTION_RU,
                DATA_DESCRIPTION_EN, DATA_LOCAL_PRICE, DATA_LEAD_TIME, DATA_MIN_ORDER, DATA_LGBK, DATA_WEIGHT));
    }

    public static ArrayList<DataItem> getColumnsForCustomExportToExcel() {
        return new ArrayList<>(Arrays.asList(DATA_ORDER_NUMBER, DATA_ORDER_NUMBER_PRINT, DATA_ARTICLE, DATA_DESCRIPTION_RU,
                DATA_DESCRIPTION_EN, DATA_LOCAL_PRICE, DATA_LEAD_TIME, DATA_MIN_ORDER, DATA_LGBK, DATA_HIERARCHY, DATA_WEIGHT,
                DATA_COUNTRY, DATA_SERVICE_END, DATA_DCHAIN, DATA_DCHAIN_COMMENT, DATA_PACKSIZE, DATA_FAMILY,
                DATA_IS_IN_PRICE, DATA_COMMENT, DATA_REPLACEMENT, DATA_TYPE));
    }
}
