package files.price_to_excel;

import core.CoreModule;
import javafx.scene.control.TreeItem;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import ui_windows.options_window.price_lists_editor.se.PriceListContentItem;
import ui_windows.options_window.price_lists_editor.se.PriceListContentTableItem;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.options_window.product_lgbk.LgbkAndParent;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.Product;

import java.util.TreeSet;

public class PriceStructure {

    private TreeSet<LgbkGroup> lgbkGroups;

    public PriceStructure() {
        lgbkGroups = new TreeSet<>((o1, o2) -> o1.getName().compareTo(o2.getName()));
    }

    public PriceStructure getFromPriceSheet(PriceListSheet priceSheet) {


        return this;
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
        LgbkGroup newGroup = new LgbkGroup(product.getLgbk());
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

    public int export(XSSFSheet sheet, int rowIndex) {
        for (LgbkGroup lgroup : lgbkGroups) {
            rowIndex = lgroup.print(sheet, rowIndex);
        }
        return rowIndex;
    }
}
