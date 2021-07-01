package files.price_to_excel;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.options_window.product_lgbk.LgbkAndParent;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.product_lgbk.ProductLgbkGroups;
import ui_windows.options_window.product_lgbk.ProductLgbks;
import ui_windows.product.Product;
import ui_windows.product.data.DataItem;
import utils.comparation.products.ProductNameResolver;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import static files.ExcelCellStyleFactory.CELL_ALIGN_HLEFT;
import static ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet.LANG_RU;

public class HierarchyGroup {
    public static final Comparator<Product> SORT_MATERIAL = Comparator.comparing(o ->
            ProductNameResolver.prepareMaterialForComparing(o.getMaterial()));
    public static final Comparator<Product> SORT_ARTICLE = Comparator.comparing(Product::getArticle);

    private PriceListSheet priceListSheet;
    private String name;
    private TreeSet<Product> products;

    public HierarchyGroup(String name, PriceListSheet priceListSheet) {
        this.priceListSheet = priceListSheet;
        if (name == null || name.length() < 4 && name.matches("^\\d") || name.length() < 3 && !name.matches("^\\d")) {
            this.name = "no name";
        } else {
            this.name = name.replaceAll("^\\d", "").substring(0, 3);
        }

        products = new TreeSet<>(priceListSheet.getSortOrder());
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return products.size();
    }

    public TreeSet<Product> getProducts() {
        return products;
    }

    public int export(XSSFSheet sheet, int rowIndex, String lgroupName, boolean space) {
        if (name.isEmpty() || getSize() == 0) return rowIndex;
        int firstRowForGroup = rowIndex;
        XSSFRow row;
        XSSFCell cell;

        if (!space) {
            rowIndex++;
            firstRowForGroup++;
        }

        LgbkAndParent lap = ProductLgbkGroups.getInstance().getLgbkAndParent(
                new ProductLgbk(lgroupName, name));

        if (lap != null && lap.getLgbkItem() != null && lap.getLgbkParent() != null) {
            String enText = ProductLgbks.getInstance().getGroupLgbkByName(lgroupName).getDescriptionEnRu() + " / " +
                    lap.getLgbkItem().getDescriptionEnRu();
            String ruText = ProductLgbks.getInstance().getGroupLgbkByName(lgroupName).getDescriptionRuEn() + " / " +
                    lap.getLgbkItem().getDescriptionRuEn();
            String printText = priceListSheet.getLanguage() == LANG_RU ? ruText : enText;

            if (!printText.isEmpty()) {
                row = sheet.createRow(rowIndex++);
                cell = row.createCell(0, CellType.STRING);
                cell.setCellStyle(CELL_ALIGN_HLEFT);
                cell.setCellValue(printText);
            }
        } else {
            firstRowForGroup--;
        }

        Map<String, Object> options = new HashMap<>();
        options.put("priceListSheet", priceListSheet);

        for (Product product : products) {
            row = sheet.createRow(rowIndex++);

            int colIndex = 0;
            for (DataItem die : priceListSheet.getColumnsSelector().getSelectedItems()) {
                cell = row.createCell(colIndex++);
                die.fillExcelCell(cell, product, options);
            }

        }
        sheet.groupRow(firstRowForGroup + 1, rowIndex - 1);
//            sheet.setRowGroupCollapsed(rowIndex - 1, true);
        return rowIndex;
    }

}
