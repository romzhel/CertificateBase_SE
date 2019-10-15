package files.price_to_excel;

import core.CoreModule;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.Product;

import java.util.Comparator;
import java.util.TreeSet;

public class LgbkGroup {
    private String name;
    private TreeSet<HierarchyGroup> hierarchyGroups;

    public LgbkGroup(String name) {
        this.name = name;
        hierarchyGroups = new TreeSet<>((o1, o2) -> o1.getName().compareTo(o2.getName()));
    }

    public void addProduct(Product product) {
        for (HierarchyGroup group : hierarchyGroups) {
            if (isSpProduct(product) || product.getHierarchy().contains(group.getName())) {
                group.addProduct(product);
                return;
            }
        }
        HierarchyGroup newGroup = new HierarchyGroup(product.getHierarchy());
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

    public int print(XSSFSheet sheet, int rowIndex) {
//        boolean isInPriceList = priceList.getLgbks().indexOf(name) != -1;
//        boolean isEmpty = name.isEmpty() || getSize() == 0;
//        if (!isInPriceList || isEmpty) return rowIndex;

        XSSFRow row;
        XSSFCell cell;

        rowIndex++;
        int firstRowForGroup = rowIndex;
        row = sheet.createRow(rowIndex++);
        cell = row.createCell(0, CellType.STRING);
//        cell.setCellStyle(CELL_ALIGN_LEFT_BOLD);

        ProductLgbk pl = CoreModule.getProductLgbks().getByLgbkName(name);

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

    private boolean isSpProduct(Product product) {
        int id = CoreModule.getProductLgbks().getFamilyIdByLgbk(new ProductLgbk(product.getLgbk(), product.getHierarchy()));
        boolean productFamId = product.getFamily() == 24;
        return id == 24 || productFamId;
    }
}
