package ui_windows.main_window.file_import_window.se;

import core.CoreModule;
import core.Dialogs;
import database.ProductsDB;
import javafx.application.Platform;
import javafx.util.Callback;
import ui_windows.main_window.DataSelectorMenu;
import ui_windows.main_window.MainWindow;
import ui_windows.main_window.MainWindowsController;
import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.product.Product;
import ui_windows.product.Products;
import utils.comparation.products.ProductsComparator;
import utils.comparation.products.ProductsComparatorResult;
import utils.comparation.se.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import static utils.comparation.se.ComparingParameters.WITHOUT_GONE;

public class ImportNowFile {

    public ImportNowFile(File file) {
        if (file == null || !file.exists()) return;
        FileImport fileImport = new FileImport();
        fileImport.getProductsInManualMode(file, () -> {
            MainWindow.setProgress(-1);

            new Thread(() -> {
                ArrayList<Product> resetedItems = fileImport.isDeleteOldStatistic() ? CoreModule.getProducts().resetLastImportCodes() : new ArrayList<>();
                HashSet<Product> changedItemsForDB = new HashSet<>(resetedItems);

                FileImportParameter[] importParameters = fileImport.getExcelFile().getMapper().getParameters().toArray(new FileImportParameter[]{});


                Comparator<Product> comparator = new Comparator<>();
                comparator.compare(CoreModule.getProducts().getItems(), fileImport.getResult(),
                        new ComparingParameters(new Adapter<Product>().convert(importParameters), new ComparingRulesProducts(), WITHOUT_GONE));

                comparator.fixChanges();

                System.out.println();




                /*ProductsComparator pc = new ProductsComparator(CoreModule.getProducts().getItems(), fileImport.getResult(), importParameters);
                ProductsComparatorResult lastComparationResult = pc.getResult();

                if (lastComparationResult.getNewItems().size() + lastComparationResult.getChangedItems().size() > 0) {
                    lastComparationResult.saveReport();
                }

                changedItemsForDB.addAll(lastComparationResult.getChangedProducts());

                if ((lastComparationResult.getNewItems().size() + lastComparationResult.getChangedItems().size()) > 0) {
                    MainWindowsController mwc = MainWindow.getFxmlLoader().getController();
                    mwc.getDataSelectorMenu().selectMenuItem(DataSelectorMenu.MENU_DATA_LAST_IMPORT_RESULT);
                }

                if (lastComparationResult.getNewItems().size() > 0)
                    new ProductsDB().putData(lastComparationResult.getNewProducts());// save new items to db
                if (changedItemsForDB.size() > 0)
                    new ProductsDB().updateData(new ArrayList<>(changedItemsForDB));//save changed items to db

                System.out.println("writing to DB finished");

                MainWindow.setProgress(0);

                Platform.runLater(() -> Dialogs.showMessage("Результаты импорта", "Новых позиций: " +
                        lastComparationResult.getNewItems().size() + "\nИзменённых позиций: " +
                        lastComparationResult.getChangedItems().size()));*/
            }).start();
        });
    }
}
