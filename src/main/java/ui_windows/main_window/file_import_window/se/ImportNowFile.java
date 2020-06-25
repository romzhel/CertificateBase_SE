package ui_windows.main_window.file_import_window.se;

import core.Dialogs;
import database.DataBase;
import database.ProductsDB;
import files.reports.NowImportResultToExcel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.main_window.DataSelectorMenu;
import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.product.Product;
import ui_windows.product.Products;
import utils.DoublesPreprocessor;
import utils.comparation.se.*;

import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
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
    private static final Logger logger = LogManager.getLogger(ImportNowFile.class);

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
                changedItemsForDB.addAll(Products.getInstance().resetLastImportCodes());
            }

            FileImportParameter[] importParameters = fileImport.getExcelFile().getMapper().getParameters().toArray(new FileImportParameter[]{});

            comparator.compare(Products.getInstance().getItems(), new DoublesPreprocessor(fileImport.getProductItems()).getTreatedItems(),
                    new ComparingParameters(new Adapter<Product>().convert(importParameters), new ComparingRulesImportNow(), WITHOUT_GONE));
            comparator.fixChanges();
        }

        if (!isThereFiles) {
            return false;
        }

        result = comparator.getComparisonResult();

        Dialogs.showMessageTS("Результаты импорта", "Новых позиций: " +
                result.getNewItems().size() + "\nИзменённых позиций: " + result.getChangedItems().size());

        if (result.getChangedItems().size() + result.getNewItems().size() > 0) {
            changedItemsForDB.addAll(result.getChangedItems());
            Products.getInstance().getItems().addAll(result.getNewItems());

            DataSelectorMenu.MENU_DATA_LAST_IMPORT_RESULT.activate();

            if (result.getNewItems().size() > 0)
                new ProductsDB().putData(result.getNewItems());// save new items to db
            if (changedItemsForDB.size() > 0)
                new ProductsDB().updateData(new ArrayList<>(changedItemsForDB));//save changed items to db

            Statement vacuumStatement = null;
            try {
                vacuumStatement = DataBase.getInstance().getDbConnection().createStatement();
                logger.debug("db file vacuum started");
                vacuumStatement.executeUpdate("vacuum;");
                logger.debug("db file vacuum finished");
            } catch (SQLException e) {
                logger.warn("sql request vacuum error: {}", e.getMessage());
            }

            logger.info("DB updating is finished");
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
