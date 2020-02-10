package utils.comparation.prices;

import core.CoreModule;
import files.price_to_excel.PriceStructure;
import files.reports.PriceComparisonMergerResultToExcel;
import ui_windows.main_window.MainWindow;
import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.main_window.file_import_window.se.FileImport;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.product.Product;
import utils.comparation.merger.ComparisonResultMerger;
import utils.comparation.se.Adapter;
import utils.comparation.se.Comparator;
import utils.comparation.se.ComparingParameters;
import utils.comparation.se.ComparingRulesPricesComparison;

import java.io.File;
import java.util.ArrayList;

public class PricesComparator {

    public PricesComparator(final File file1, final File file2) {
        new Thread(() -> {
            MainWindow.setProgress(-1.0);

            FileImport oldPriceFi = new FileImport();
            PriceList priceList = CoreModule.getPriceLists().getItems().get(0);

            ArrayList<Product> oldPriceItems;
            ArrayList<Product> newPriceItems;

            ComparisonResultMerger<Product> merger = new ComparisonResultMerger<>((o1, o2) -> o1.getMaterial().equals(o2.getMaterial()) ? 0 : 1);
            for (int sheetIndex = 0; sheetIndex < priceList.getSheets().size(); sheetIndex++) {
                oldPriceItems = oldPriceFi.getProductsInAutoMode(file1, sheetIndex);
                if (file2 == null) {
                    PriceStructure priceStructure = new PriceStructure(priceList.getSheets().get(sheetIndex));
                    priceStructure.analysePriceItems();
                    newPriceItems = priceStructure.getCorrectProducts();
                } else {
                    newPriceItems = new FileImport().getProductsInAutoMode(file2, sheetIndex);
                }

                FileImportParameter[] params = oldPriceFi.getExcelFile().getMapper().getParameters().toArray(new FileImportParameter[]{});

                Comparator<Product> comparator = new Comparator<>();
                comparator.compare(oldPriceItems, newPriceItems, new ComparingParameters(new Adapter<Product>().convert(params),
                        new ComparingRulesPricesComparison(), ComparingParameters.WITH_GONE));

                merger.addForMerging(comparator.getComparisonResult());
            }

            merger.merge();
            PriceComparisonMergerResultToExcel exporterToExcel = new PriceComparisonMergerResultToExcel();
            String firstName = file1.getName();
            String secondName = file2 != null ? file2.getName() : "online price";
            String name = String.format("PriceComparisonResult %s vs %s", secondName, firstName);
            exporterToExcel.export(oldPriceFi.getExcelFile().getSheetsName(), merger.getResult(), name);
            MainWindow.setProgress(0.0);
        }).start();
    }
}
