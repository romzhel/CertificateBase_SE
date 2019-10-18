package ui_windows.options_window.price_lists_editor.se;

import javafx.util.Callback;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.util.ArrayList;

import static files.price_to_excel.ExportPriceListToExcel_SE.*;
import static ui_windows.main_window.file_import_window.NamesMapping.*;

public class PriceListColumns extends ArrayList<PriceListColumn> {
    private ArrayList<PriceListColumn> columns;

    public PriceListColumns() {
        columns = new ArrayList<>();

        final PriceListColumn COLUMN_ORDER_NUMBER_PRINT = new PriceListColumn(DESC_ORDER_NUMBER_PRINT, FIELD_ORDER_NUMBER_PRINT);
        COLUMN_ORDER_NUMBER_PRINT.setValueFactory(param -> {
            XSSFCell cell = param.getRow().createCell(param.getIndex(), CellType.STRING);
            cell.setCellValue(param.getProduct().getProductForPrint());
            cell.setCellStyle(CELL_ALIGN_LEFT);

            return cell;
        });

        PriceListColumn COLUMN_ARTICLE = new PriceListColumn(DESC_ARTICLE, FIELD_ARTICLE);
        COLUMN_ARTICLE.setValueFactory(param -> {
            XSSFCell cell = param.getRow().createCell(param.getIndex(), CellType.STRING);
            cell.setCellValue(param.getProduct().getArticle());
            cell.setCellStyle(CELL_ALIGN_LEFT);

            return cell;
        });

        PriceListColumn COLUMN_DESCRIPTION_RU = new PriceListColumn(DESC_DESCRIPTION_RU, FIELD_DESCRIPTION_RU);
        COLUMN_DESCRIPTION_RU.setValueFactory(param -> {
            XSSFCell cell = param.getRow().createCell(param.getIndex(), CellType.STRING);
            cell.setCellValue(param.getProduct().getDescriptionRuEn());
            cell.setCellStyle(CELL_ALIGN_LEFT);

            return cell;
        });

        PriceListColumn COLUMN_DESCRIPTION_EN = new PriceListColumn(DESC_DESCRIPTION_EN, FIELD_DESCRIPTION_EN);
        COLUMN_DESCRIPTION_EN.setValueFactory(param -> {
            XSSFCell cell = param.getRow().createCell(param.getIndex(), CellType.STRING);
            cell.setCellValue(param.getProduct().getDescriptionen());
            cell.setCellStyle(CELL_ALIGN_LEFT);

            return cell;
        });

        PriceListColumn COLUMN_LOCAL_PRICE = new PriceListColumn(DESC_LOCAL_PRICE, FIELD_LOCAL_PRICE);
        COLUMN_LOCAL_PRICE.setValueFactory(param -> {
            XSSFCell cell = param.getRow().createCell(param.getIndex(), CellType.NUMERIC);
            cell.setCellValue(param.getProduct().getLocalPrice());
            cell.setCellStyle(CELL_CURRENCY_FORMAT);

            return cell;
        });

        PriceListColumn COLUMN_LEAD_TIME = new PriceListColumn(DESC_LEADTIME, FIELD_LEADTIME);
        COLUMN_LEAD_TIME.setValueFactory(param -> {
            XSSFCell cell = param.getRow().createCell(param.getIndex(), CellType.NUMERIC);
            cell.setCellValue(param.getProduct().getPreparedLeadTime());
            cell.setCellStyle(CELL_ALIGN_CENTER);

            return cell;
        });

        PriceListColumn COLUMN_MIN_ORDER = new PriceListColumn(DESC_MIN_ORDER, FIELD_MIN_ORDER);
        COLUMN_MIN_ORDER.setValueFactory(param -> {
            XSSFCell cell = param.getRow().createCell(param.getIndex(), CellType.NUMERIC);
            cell.setCellValue(param.getProduct().getMinOrder());
            cell.setCellStyle(CELL_ALIGN_CENTER);

            return cell;
        });

        PriceListColumn COLUMN_LGBK = new PriceListColumn(DESC_LGBK, FIELD_LGBK);
        COLUMN_LGBK.setValueFactory(param -> {
            XSSFCell cell = param.getRow().createCell(param.getIndex(), CellType.STRING);
            cell.setCellValue(param.getProduct().getLgbk());
            cell.setCellStyle(CELL_ALIGN_CENTER);

            return cell;
        });

        PriceListColumn COLUMN_WEIGHT = new PriceListColumn(DESC_WEIGHT, FIELD_WEIGHT);
        COLUMN_WEIGHT.setValueFactory(param -> {
            XSSFCell cell = param.getRow().createCell(param.getIndex(), CellType.STRING);
            cell.setCellValue(param.getProduct().getWeight());
            cell.setCellStyle(CELL_ALIGN_RIGHT);

            return cell;
        });

        addAll(COLUMN_ORDER_NUMBER_PRINT, COLUMN_ARTICLE, COLUMN_DESCRIPTION_RU, COLUMN_DESCRIPTION_EN, COLUMN_LOCAL_PRICE,
                COLUMN_LEAD_TIME, COLUMN_MIN_ORDER, COLUMN_LGBK, COLUMN_WEIGHT);
    }

    private void addAll(PriceListColumn... columns) {
        for (PriceListColumn plc : columns) {
            this.columns.add(plc);
        }
    }

    public ArrayList<PriceListColumn> getColumns() {
        return columns;
    }
}
