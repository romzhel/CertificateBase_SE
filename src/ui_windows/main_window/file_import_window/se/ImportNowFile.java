package ui_windows.main_window.file_import_window.se;

import core.CoreModule;
import core.Dialogs;
import database.ProductsDB;
import files.reports.NowImportResultToExcel;
import javafx.application.Platform;
import ui_windows.main_window.DataSelectorMenu;
import ui_windows.main_window.MainWindow;
import ui_windows.main_window.MainWindowsController;
import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.product.Product;
import utils.comparation.se.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Callable;

import static utils.comparation.se.ComparingParameters.WITHOUT_GONE;

public class ImportNowFile implements Callable<File> {
    private ComparisonResult<Product> result;
    private File reportFile;

    public ImportNowFile(File file) {
        if (file == null || !file.exists()) return;

        MainWindow.setProgress(-1);

        FileImport fileImport = new FileImport();
        fileImport.getProductsInManualMode(file);

        ArrayList<Product> resetedItems = fileImport.isDeleteOldStatistic() ? CoreModule.getProducts().resetLastImportCodes() : new ArrayList<>();
        HashSet<Product> changedItemsForDB = new HashSet<>(resetedItems);

        FileImportParameter[] importParameters = fileImport.getExcelFile().getMapper().getParameters().toArray(new FileImportParameter[]{});

        Comparator<Product> comparator = new Comparator<>();
        comparator.compare(CoreModule.getProducts().getItems(), fileImport.getProductItems(),
                new ComparingParameters(new Adapter<Product>().convert(importParameters), new ComparingRulesImportNow(), WITHOUT_GONE));
        comparator.fixChanges();

        result = comparator.getComparisonResult();

        Platform.runLater(() -> Dialogs.showMessage("Результаты импорта", "Новых позиций: " +
                result.getNewItems().size() + "\nИзменённых позиций: " + result.getChangedItems().size()));

        if (result.getChangedItems().size() + result.getNewItems().size() > 0) {
            changedItemsForDB.addAll(result.getChangedItems());
            CoreModule.getProducts().getItems().addAll(result.getNewItems());

            MainWindowsController mwc = MainWindow.getFxmlLoader().getController();
            mwc.getDataSelectorMenu().selectMenuItem(DataSelectorMenu.MENU_DATA_LAST_IMPORT_RESULT);

            if (result.getNewItems().size() > 0)
                new ProductsDB().putData(result.getNewItems());// save new items to db
            if (changedItemsForDB.size() > 0)
                new ProductsDB().updateData(new ArrayList<>(changedItemsForDB));//save changed items to db

            System.out.println("writing to DB finished");
        }

        MainWindow.setProgress(0);


//            }).start();

    }

    @Override
    public File call() throws Exception {
        return reportFile;
    }

    public File getReportFile(File targetFile) {
        return new NowImportResultToExcel().export(result, targetFile);
    }
}
