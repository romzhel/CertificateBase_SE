package files.price_to_excel;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.product_lgbk.ProductLgbks;
import ui_windows.product.Product;

import java.util.TreeSet;

import static files.ExcelCellStyleFactory.CELL_ALIGN_HLEFT_BOLD;

public class LgbkGroup {
    private PriceListSheet priceListSheet;
    private String name;
    private TreeSet<HierarchyGroup> hierarchyGroups;

    public LgbkGroup(String name, PriceListSheet priceListSheet) {
        this.priceListSheet = priceListSheet;
        this.name = name;
        hierarchyGroups = new TreeSet<>((o1, o2) -> o1.getName().compareTo(o2.getName()));
    }

    public void addProduct(Product product) {
        for (HierarchyGroup group : hierarchyGroups) {
//            if (product.isSpProduct() || product.getHierarchy().contains(group.getName())) { todo перепроверить
            if (product.getHierarchy().contains(group.getName()) ||
                    product.getHierarchy().isEmpty() && group.getName().equals("no name")) {
                group.addProduct(product);
                return;
            }
        }
        HierarchyGroup newGroup = new HierarchyGroup(product.getHierarchy(), priceListSheet);
        newGroup.addProduct(product);
        hierarchyGroups.add(newGroup);
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        int result = 0;
        for (HierarchyGroup hg : hierarchyGroups) {
            result += hg.getSize();
        }
        return result;
    }

    public int export(XSSFSheet sheet, int rowIndex) {
//        boolean isInPriceList = priceList.getLgbks().indexOf(name) != -1;
//        boolean isEmpty = name.isEmpty() || getSize() == 0;
//        if (!isInPriceList || isEmpty) return rowIndex;

        XSSFRow row;
        XSSFCell cell;

        rowIndex++;
        int firstRowForGroup = rowIndex;
        row = sheet.createRow(rowIndex++);
        cell = row.createCell(0, CellType.STRING);
        cell.setCellStyle(CELL_ALIGN_HLEFT_BOLD);

        ProductLgbk pl = ProductLgbks.getInstance().getGroupLgbkByName(name);

        if (sheet.getSheetName().toLowerCase().contains("en")) {
            cell.setCellValue(pl.getDescriptionEnRu());
        } else {
            cell.setCellValue(pl.getDescriptionRuEn());
        }

        for (HierarchyGroup hgroup : hierarchyGroups) {
            rowIndex = hgroup.export(sheet, rowIndex, name, hierarchyGroups.first().equals(hgroup));
        }

        sheet.groupRow(firstRowForGroup + 1, rowIndex);

        return rowIndex;
    }

    public TreeSet<HierarchyGroup> getHierarchyGroups() {
        return hierarchyGroups;
    }
}
