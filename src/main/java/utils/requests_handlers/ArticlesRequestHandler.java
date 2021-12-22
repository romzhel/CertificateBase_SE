package utils.requests_handlers;

import files.ExcelCellStyleFactory;
import files.reports.ReportToExcelTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ui_windows.product.Product;
import ui_windows.product.Products;
import ui_windows.product.data.DataItem;
import utils.Utils;

import java.util.*;
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

    public void createArticleExistingReport(String text) throws Exception {
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
                            .filter(product -> product.getArticle().equals(item) || product.getArticle().matches("^" + item + "\\s*[\\d\\-]+.*$"))
//                    .filter(product -> product.getArticle().matches("^" + item + "(\\s*[\\d\\-]+.*)?$"))//todo значение item со знаками распознаётся как выражение
                            .collect(Collectors.toSet())
            );
        }

        logger.trace("выгрузка в Excel");
        workbook = new XSSFWorkbook();
        ExcelCellStyleFactory.init(workbook);
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

        int groupStart = 0;
        for (Map.Entry<String, Set<Product>> entry : result.entrySet()) {
            groupStart = rowIndex;
            colIndex = 0;
            xssfRow = xssfSheet.createRow(rowIndex++);
            xssfCell = xssfRow.createCell(colIndex++);
            xssfCell.setCellValue(entry.getKey());

            List<String> sortedCountryList = entry.getValue().stream()
                    .map(Product::getCountry)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            xssfCell = xssfRow.createCell(8);
            xssfCell.setCellValue(Strings.join(sortedCountryList, ','));

            xssfCell = xssfRow.createCell(colIndex++);
            xssfCell.setCellValue(entry.getValue().size());

            List<Product> sortedList = entry.getValue().stream()
                    .sorted((o1, o2) -> (o1.getCountry() + o1.getDchain()).compareTo(o2.getCountry() + o2.getDchain()))
                    .collect(Collectors.toList());

            for (Product product : sortedList) {
                xssfRow = xssfSheet.createRow(rowIndex++);

                colIndex = 2;
                for (DataItem die : getColumns()) {
                    xssfCell = xssfRow.createCell(colIndex++);
                    die.fillExcelCell(xssfCell, product, null);
                }
            }

//            xssfRow = xssfSheet.createRow(rowIndex++);
//            xssfSheet.groupRow(groupStart, rowIndex - 1);
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
                DATA_IN_WHICH_PRICE_LIST,
                DATA_DCHAIN_WITH_COMMENT,
                DATA_COUNTRY
        };
    }
}
