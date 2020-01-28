package utils.comparation.prices;

import core.CoreModule;
import core.Dialogs;
import files.price_comparator_result_to_excel.PriceComparatorResultToExcel;
import files.price_to_excel.PriceStructure;
import ui_windows.main_window.MainWindow;
import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.main_window.file_import_window.se.FileImport;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.product.Product;
import ui_windows.product.Products;
import utils.comparation.products.ProductsComparator;
import utils.comparation.products.ProductsComparatorResultItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class PricesComparator {
    private PriceComparatorResult comparationResult;

    public PricesComparator(File file1, File file2) {
        MainWindow.setProgress(-1.0);

        FileImport oldPriceFi = new FileImport();
        PriceList priceList = CoreModule.getPriceLists().getItems().get(0);

        ArrayList<Product> oldPriceItems;
        ArrayList<Product> newPriceItems;

        comparationResult = new PriceComparatorResult();
        for (int sheetIndex = 0; sheetIndex < priceList.getSheets().size(); sheetIndex++) {
            oldPriceItems = oldPriceFi.getProductsInAutoMode(file1, sheetIndex);
            if (file2 == null) {
                PriceStructure priceStructure = new PriceStructure(priceList.getSheets().get(sheetIndex));
                priceStructure.analysePriceItems();
                newPriceItems = priceStructure.getCorrectProducts();
            } else {
                newPriceItems = new FileImport().getProductsInAutoMode(file2, sheetIndex);
            }

            comparationResult.addSheetName(oldPriceFi.getExcelFile().getSheetsName().get(sheetIndex));

            FileImportParameter[] params = oldPriceFi.getExcelFile().getMapper().getParameters().toArray(new FileImportParameter[]{});

            ProductsComparator productsComparator = new ProductsComparator(new Products(oldPriceItems),
                    new Products(newPriceItems), params);

            addToComparationResult(productsComparator.getResult().getChangedItems(), sheetIndex);
            addToComparationResult(productsComparator.getResult().getNewItems(), sheetIndex);
            addToComparationResult(productsComparator.getResult().getGoneItems(), sheetIndex);

        }
        PriceComparatorResultToExcel pcte =  new PriceComparatorResultToExcel();
        pcte.export(new Dialogs().selectAnyFile(MainWindow.getMainStage(), "Выбор файла", Dialogs.EXCEL_FILES,
                "отчёт.xlsx"), comparationResult);

        MainWindow.setProgress(0.0);
    }

    private void addToComparationResult(ArrayList<ProductsComparatorResultItem> resultItems, int sheetIndex) {
        for (ProductsComparatorResultItem pcri : resultItems) {
            comparationResult.addItem(new PriceComparatorResultItem(pcri.getProduct(), sheetIndex, pcri.getChangeComment()));
        }
    }
}
