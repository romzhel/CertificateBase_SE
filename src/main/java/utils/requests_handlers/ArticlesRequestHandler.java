package utils.requests_handlers;

import files.ExcelCellStyleFactory;
import files.reports.ReportToExcelTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ui_windows.product.Product;
import ui_windows.product.Products;
import ui_windows.product.data.DataItem;
import utils.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ui_windows.product.data.DataItem.*;

public class ArticlesRequestHandler extends ReportToExcelTemplate {
    public static final Logger logger = LogManager.getLogger(ArticlesRequestHandler.class);
    private static ArticlesRequestHandler instance;

    private ArticlesRequestHandler() {
    }

    public static ArticlesRequestHandler getInstance() {
        if (instance == null) {
            instance = new ArticlesRequestHandler();
        }
        return instance;
    }

    public void createArticleExistingReport(String text) {
        logger.trace("запуск отчёта наличия позиций с сокращенными артикулами");
        String[] items = text.split("[,;\\s\n]");
        Set<String> requestItems = Arrays.stream(items)
                .map(String::trim)
                .filter(s -> !s.isEmpty() && !s.matches(".*[а-яА-Я]+.*"))
                .collect(Collectors.toSet());

        logger.trace("позиций для поиска: {}", requestItems.size());
        Map<String, Set<Product>> result = new HashMap<>();
        for (String item : requestItems) {
            result.put(item, Products.getInstance().getItems().stream()
                    .filter(product -> product.getArticle().matches("^" + item + "\\s*[\\d\\-]+.*$"))
                    .collect(Collectors.toSet())
            );
        }

        logger.trace("выгрузка в Excel");
        workbook = new XSSFWorkbook();
        new ExcelCellStyleFactory(workbook);
        XSSFSheet xssfSheet = (XSSFSheet) workbook.createSheet("Отчёт по артикулам");

        int rowIndex = 0;
        XSSFRow xssfRow;
        XSSFCell xssfCell;

        int colIndex = 1;
        xssfRow = xssfSheet.createRow(rowIndex++);
        xssfCell = xssfRow.createCell(colIndex++);
        xssfCell.setCellValue("Найдено позиций");
        for (DataItem die : getColumns()) {
            xssfCell = xssfRow.createCell(colIndex++, CellType.STRING);
            xssfCell.setCellValue(die.getDisplayingName());
            xssfSheet.autoSizeColumn(colIndex - 1);
        }
        logger.trace("заголовки созданы");

        for (Map.Entry<String, Set<Product>> entry : result.entrySet()) {
            colIndex = 0;
            xssfRow = xssfSheet.createRow(rowIndex++);
            xssfCell = xssfRow.createCell(colIndex++);
            xssfCell.setCellValue(entry.getKey());

            xssfCell = xssfRow.createCell(colIndex++);
            xssfCell.setCellValue(entry.getValue().size());

            for (Product product : entry.getValue()) {
                xssfRow = xssfSheet.createRow(rowIndex++);

                colIndex = 2;
                for (DataItem die : getColumns()) {
                    xssfCell = xssfRow.createCell(colIndex++);
                    die.fillExcelCell(xssfCell, product, null);
                }
            }
        }
        logger.trace("Данные внесены, создание файла Excel");

        Utils.openFile(saveToExcelFile());
    }

    private DataItem[] getColumns() {
        return new DataItem[]{
                DATA_FAMILY_NAME,
                DATA_RESPONSIBLE,
                DATA_ARTICLE,
                DATA_DESCRIPTION,
                DATA_IS_IN_PRICE,
                DATA_DCHAIN_WITH_COMMENT
        };
    }
}
