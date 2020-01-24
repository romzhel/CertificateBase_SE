package utils.comparation.prices;

import core.CoreModule;
import files.price_to_excel.PriceStructure;
import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.main_window.file_import_window.se.FileImport;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.product.Product;
import ui_windows.product.Products;
import utils.comparation.products.ProductsComparator;

import java.io.File;
import java.util.ArrayList;

public class PricesComparator {

    public PricesComparator(File file1, File file2) {
        FileImport newPriceFi = new FileImport();
        PriceList priceList = CoreModule.getPriceLists().getItems().get(0);

        ArrayList<Product> oldPrs;
        ArrayList<Product> newPrs;

        for (int sheetIndex = 0; sheetIndex < priceList.getSheets().size(); sheetIndex++) {
            if (file1 == null) {
                PriceStructure priceStructure = new PriceStructure(priceList.getSheets().get(sheetIndex));
                priceStructure.analysePriceItems();
                oldPrs = priceStructure.getCorrectProducts();
            } else {
                FileImport oldPriceFi = new FileImport();
                oldPrs = oldPriceFi.getProductsInAutoMode(file1, sheetIndex);
            }

            newPrs = newPriceFi.getProductsInAutoMode(file2, sheetIndex);

            FileImportParameter[] params = new FileImportParameter[]{};
            newPriceFi.getExcelFile().getMapper().getParameters().toArray(params);

            ProductsComparator productsComparator = new ProductsComparator(new Products(oldPrs), new Products(newPrs), params);
            productsComparator.getResult().saveReport();


            System.out.println("old = " + oldPrs.size() + ", new = " + newPrs.size());
        }
    }
}
