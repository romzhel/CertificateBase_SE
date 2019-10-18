package files.price_to_excel;

import core.CoreModule;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.product.Product;

import java.util.TreeSet;

import static ui_windows.options_window.price_lists_editor.se.PriceListContentTable.CONTENT_MODE_FAMILY;
import static ui_windows.options_window.price_lists_editor.se.PriceListContentTable.CONTENT_MODE_LGBK;

public class PriceStructure {
    private PriceListSheet priceListSheet;
    private TreeSet<LgbkGroup> lgbkGroups;

    public PriceStructure(PriceListSheet priceListSheet) {
        this.priceListSheet = priceListSheet;
        lgbkGroups = new TreeSet<>((o1, o2) -> o1.getName().compareTo(o2.getName()));

        int contentMode = priceListSheet.getContentMode();
        if (contentMode == CONTENT_MODE_FAMILY) priceListSheet.getContentTable().switchContentMode(CONTENT_MODE_LGBK);

        for (Product product : CoreModule.getProducts().getItems()) {
            if (product.isPrice() && priceListSheet.isInPrice(product)) {
                addProduct(product);
            }
        }

        if (contentMode == CONTENT_MODE_FAMILY) priceListSheet.getContentTable().switchContentMode(CONTENT_MODE_FAMILY);
    }

    public void addProduct(Product product) {
        for (LgbkGroup group : lgbkGroups) {
            String l = product.getLgbk();
            String n = group.getName();
            if (l.equals(n)) {
                group.addProduct(product);
                return;
            }
        }
        LgbkGroup newGroup = new LgbkGroup(product.getLgbk(), priceListSheet);
        newGroup.addProduct(product);
        lgbkGroups.add(newGroup);
    }

    public int getSize() {
        int result = 0;
        for (LgbkGroup lg : lgbkGroups) {
            result += lg.getSize();
        }
        return result;
    }

    public TreeSet<LgbkGroup> getLgbkGroups() {
        return lgbkGroups;
    }

    public int export(XSSFSheet sheet) {
        int initialRowIndex = Math.max(priceListSheet.getInitialRow(), 2);
        int rowIndex = initialRowIndex;
        for (LgbkGroup lgroup : lgbkGroups) {
            rowIndex = lgroup.export(sheet, rowIndex);
        }

        XSSFTable table = sheet.getTables().get(0);
        CTTable cttable = table.getCTTable();

        if (rowIndex > initialRowIndex) {
            char lastColumnLetter = table.getCellReferences().getLastCell().formatAsString().charAt(0);
            cttable.setRef(table.getCellReferences().getFirstCell().formatAsString() + ":" + lastColumnLetter + String.valueOf(rowIndex));
        }

        return rowIndex;
    }
}
