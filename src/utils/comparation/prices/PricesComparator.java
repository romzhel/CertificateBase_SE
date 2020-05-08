package utils.comparation.prices;

import files.price_to_excel.PriceStructure;
import files.reports.PriceComparisonMergerResultToExcel;
import ui_windows.main_window.MainWindow;
import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.main_window.file_import_window.se.FileImport;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.price_lists_editor.PriceLists;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.product.Product;
import utils.comparation.merger.ComparisonResultMerger;
import utils.comparation.se.Adapter;
import utils.comparation.se.Comparator;
import utils.comparation.se.ComparingParameters;
import utils.comparation.se.ComparingRulesPricesComparison;

import java.io.File;
import java.util.ArrayList;

import static ui_windows.options_window.price_lists_editor.se.PriceListContentTable.CONTENT_MODE_FAMILY;
import static ui_windows.options_window.price_lists_editor.se.PriceListContentTable.CONTENT_MODE_LGBK;
import static ui_windows.product.data.DataItem.DATA_LOCAL_PRICE;

public class PricesComparator {
    private File file1;
    private File file2;
    private File comparisonResultFile;
    private ComparisonResultMerger<Product> merger;
    private FileImport oldPriceFi;
    public static final PriceList COMPARED_PRICE_LIST = PriceLists.getInstance().getItems().get(0);//internal price list

    public void compare(ArrayList<File> priceListFiles) {
        if (priceListFiles != null) {
            file1 = priceListFiles.get(0);
            file2 = priceListFiles.get(1);

            compare();
        }
    }

    public void compare(File file1, File file2) {
        this.file1 = file1;
        this.file2 = file2;

        compare();
    }

    private void compare() {
//        new Thread(() -> {
        MainWindow.setProgress(-1.0);

        oldPriceFi = new FileImport();

        ArrayList<Product> oldPriceItems;
        ArrayList<Product> newPriceItems;

        merger = new ComparisonResultMerger<>((o1, o2) -> {
            String prod1 = o1.getMaterial().replaceAll("(\\-)*(\\:)*(VBPZ)*(BPZ)*", "");
            String prod2 = o2.getMaterial().replaceAll("(\\-)*(\\:)*(VBPZ)*(BPZ)*", "");
            return prod1.equals(prod2) ? 0 : 1;
        });
        for (int sheetIndex = 0; sheetIndex < COMPARED_PRICE_LIST.getSheets().size(); sheetIndex++) {

            PriceListSheet priceListSheet = COMPARED_PRICE_LIST.getSheets().get(sheetIndex);

            oldPriceItems = oldPriceFi.getProductsInAutoMode(file1, sheetIndex);
            if (file2 == null) {
                PriceStructure priceStructure = new PriceStructure(priceListSheet);
                priceStructure.analysePriceItems();
                newPriceItems = priceStructure.getCorrectProducts();

                if (priceListSheet.getContentMode() == CONTENT_MODE_FAMILY) {
                    priceListSheet.getContentTable().switchContentMode(CONTENT_MODE_LGBK);
                }
            } else {
                newPriceItems = new FileImport().getProductsInAutoMode(file2, sheetIndex);
            }

            FileImportParameter[] params = oldPriceFi.getExcelFile().getMapper().getParameters().toArray(new FileImportParameter[]{});

            Comparator<Product> comparator = new Comparator<>();
            comparator.compare(oldPriceItems, newPriceItems,
                    new ComparingParameters(new Adapter<Product>().convert(params),
                            new ComparingRulesPricesComparison(param -> {
                                if (file2 == null) {//online price
                                    if (param.getField() == DATA_LOCAL_PRICE.getField()) {
                                        Product clone = param.getObject2().clone();
                                        double cost = param.getObject2().getLocalPrice() * (1.0 - (double) priceListSheet.getDiscount() / 100);
                                        clone.setLocalPrice(cost);
                                        param.setObject2(clone);
                                    }
                                }
                                return param;
                            }), ComparingParameters.WITH_GONE));

            merger.addForMerging(comparator.getComparisonResult());
        }

        merger.merge();

        MainWindow.setProgress(0.0);
//        }).start();
    }

    public File getFile1() {
        return file1;
    }

    public File getFile2() {
        return file2;
    }

    public ComparisonResultMerger<Product> getMerger() {
        return merger;
    }

    public FileImport getOldPriceFi() {
        return oldPriceFi;
    }

    public PriceList getCOMPARED_PRICE_LIST() {
        return COMPARED_PRICE_LIST;
    }

    public File exportToExcel(File file) {
        PriceComparisonMergerResultToExcel exporterToExcel = new PriceComparisonMergerResultToExcel();
        comparisonResultFile = exporterToExcel.export(this, file);
        return comparisonResultFile;
    }

    public File getComparisonResultFile() {
        return comparisonResultFile;
    }
}
