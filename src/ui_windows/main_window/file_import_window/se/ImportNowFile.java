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
import utils.DoublesPreprocessor;
import utils.comparation.se.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import static utils.comparation.se.ComparingParameters.WITHOUT_GONE;

public class ImportNowFile implements Callable<File> {
    private ComparisonResult<Product> result;
    private File reportFile;
    private Comparator<Product> comparator;
    private FileImport fileImport;

    public ImportNowFile() {
        comparator = new Comparator<>();
        fileImport = new FileImport();
    }

    public boolean treat(List<File> files) {
        if (files == null) {
            return false;
        }

        HashSet<Product> changedItemsForDB = new HashSet<>();

        boolean isThereFiles = false;
        for (File file : files) {
            if (file == null) {
                continue;
            }
            isThereFiles = true;

            fileImport.getProductsInManualMode(file);

            if (fileImport.isDeleteOldStatistic() && files.indexOf(file) < 1) {
                changedItemsForDB.addAll(CoreModule.getProducts().resetLastImportCodes());
            }

            FileImportParameter[] importParameters = fileImport.getExcelFile().getMapper().getParameters().toArray(new FileImportParameter[]{});

            comparator.compare(CoreModule.getProducts().getItems(), new DoublesPreprocessor(fileImport.getProductItems()).getTreatedItems(),
                    new ComparingParameters(new Adapter<Product>().convert(importParameters), new ComparingRulesImportNow(), WITHOUT_GONE));
        }
        comparator.fixChanges();

        if (!isThereFiles) {
            return false;
        }

        result = comparator.getComparisonResult();

        Platform.runLater(() -> Dialogs.showMessage("Результаты импорта", "Новых позиций: " +
                result.getNewItems().size() + "\nИзменённых позиций: " + result.getChangedItems().size()));

        if (result.getChangedItems().size() + result.getNewItems().size() > 0) {
            changedItemsForDB.addAll(result.getChangedItems());
            CoreModule.getProducts().getItems().addAll(result.getNewItems());

            MainWindowsController mwc = MainWindow.getFxmlLoader().getController();
//            mwc.getDataSelectorMenu().selectMenuItem(DataSelectorMenu.MENU_DATA_LAST_IMPORT_RESULT);
            DataSelectorMenu.MENU_DATA_LAST_IMPORT_RESULT.activate();

            if (result.getNewItems().size() > 0)
                new ProductsDB().putData(result.getNewItems());// save new items to db
            if (changedItemsForDB.size() > 0)
                new ProductsDB().updateData(new ArrayList<>(changedItemsForDB));//save changed items to db

            System.out.println("DB updating is finished");
        }
        return true;
    }

    @Override
    public File call() throws Exception {
        return reportFile;
    }

    public File getReportFile(File targetFile) {
        return new NowImportResultToExcel().export(result, targetFile);
    }
}
