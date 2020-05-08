package files.price_to_excel;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.product.Product;
import ui_windows.product.Products;
import ui_windows.product.certificatesChecker.CertificatesChecker;
import ui_windows.product.certificatesChecker.CheckStatusResult;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static ui_windows.options_window.price_lists_editor.se.PriceListContentTable.CONTENT_MODE_FAMILY;
import static ui_windows.options_window.price_lists_editor.se.PriceListContentTable.CONTENT_MODE_LGBK;
import static ui_windows.product.certificatesChecker.CheckStatusResult.STATUS_NOT_OK;
import static ui_windows.product.certificatesChecker.CheckStatusResult.STATUS_OK;

public class PriceStructure {
    private PriceListSheet priceListSheet;
    private TreeSet<LgbkGroup> lgbkGroups;
    private Vector<Product> problemItems;
    private ArrayList<Product> correctItems;

    public PriceStructure(PriceListSheet priceListSheet) {
        this.priceListSheet = priceListSheet;
        problemItems = new Vector<>();
        correctItems = new ArrayList<>();
    }

    public void analysePriceItems() {
        problemItems.clear();
        lgbkGroups = new TreeSet<>((o1, o2) -> o1.getName().compareTo(o2.getName()));

        int contentMode = priceListSheet.getContentMode();
        if (contentMode == CONTENT_MODE_FAMILY) priceListSheet.getContentTable().switchContentMode(CONTENT_MODE_LGBK);

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (Product product : Products.getInstance().getItems()) {
            executorService.execute(() -> {
                if (product.isPrice() && priceListSheet.isInPrice(product)) {
                    CheckStatusResult checkingResult = new CertificatesChecker(product).getCheckStatusResult();
                    if (priceListSheet.isCheckCert() && checkingResult.equals(STATUS_OK) ||
                            !priceListSheet.isCheckCert() && !checkingResult.equals(STATUS_NOT_OK)) {
                        addProduct(product);
                    } else {
                        problemItems.add(product);
                    }
                }
            });
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(3, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (contentMode == CONTENT_MODE_FAMILY) priceListSheet.getContentTable().switchContentMode(CONTENT_MODE_FAMILY);
    }

    public synchronized void addProduct(Product product) {
        correctItems.add(product);
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

    public Vector<Product> getProblemItems() {
        return problemItems;
    }

    public ArrayList<Product> getCorrectProducts() {
        return correctItems;
    }
}