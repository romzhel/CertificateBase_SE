package files.price_to_excel;

import core.CoreModule;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import ui_windows.options_window.price_lists_editor.se.PriceListColumn;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.options_window.product_lgbk.LgbkAndParent;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.Product;

import java.util.Comparator;
import java.util.TreeSet;

import static files.price_to_excel.ExportPriceListToExcel_SE.CELL_ALIGN_LEFT;

public class HierarchyGroup {
    public static final Comparator<Product> SORT_MATERIAL = (o1, o2) -> o1.getTextForComparing().compareTo(o2.getTextForComparing());
    public static final Comparator<Product> SORT_ARTICLE = (o1, o2) -> o1.getArticle().compareTo(o2.getArticle());

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

        LgbkAndParent lap = CoreModule.getProductLgbkGroups().getLgbkAndParent(
                new ProductLgbk(lgroupName, name));

        if (lap != null && lap.getLgbkItem() != null && lap.getLgbkParent() != null) {
            String enText = CoreModule.getProductLgbks().getByLgbkName(lgroupName).getDescriptionEnRu() + " / " +
                    lap.getLgbkItem().getDescriptionEnRu();
            String ruText = CoreModule.getProductLgbks().getByLgbkName(lgroupName).getDescriptionRuEn() + " / " +
                    lap.getLgbkItem().getDescriptionRuEn();
            String printText;

            if (sheet.getSheetName().toLowerCase().contains("en")) {
                printText = enText;
            } else {
                printText = ruText;
            }

            if (!printText.isEmpty()) {
                row = sheet.createRow(rowIndex++);
                cell = row.createCell(0, CellType.STRING);
                cell.setCellStyle(CELL_ALIGN_LEFT);
                cell.setCellValue(printText);
            }
        } else {
            firstRowForGroup--;
        }

        for (Product product : products) {
            row = sheet.createRow(rowIndex++);

            int colIndex = 0;
            for (PriceListColumn plc : priceListSheet.getColumnsSelector().getSelectedItems()) {
                plc.createXssfCell(product, row, colIndex++, priceListSheet);
            }
        }
        sheet.groupRow(firstRowForGroup + 1, rowIndex - 1);
//            sheet.setRowGroupCollapsed(rowIndex - 1, true);
        return rowIndex;
    }

}
