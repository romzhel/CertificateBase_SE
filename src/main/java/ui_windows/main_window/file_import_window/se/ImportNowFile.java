package ui_windows.main_window.file_import_window.se;

import database.ProductsDB;
import files.reports.NowImportResultToExcel;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui.Dialogs;
import ui_windows.main_window.DataSelectorMenu;
import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.product.Product;
import ui_windows.product.Products;
import utils.DoublesPreprocessor;
import utils.comparation.se.*;

import java.io.File;
import java.util.*;

import static utils.comparation.se.ComparingParameters.WITHOUT_GONE;

public class ImportNowFile {
    private static final Logger logger = LogManager.getLogger(ImportNowFile.class);
    private ComparisonResult<Product> result;
    private ProductsComparator comparator;
    private FileImport fileImport;

    public ImportNowFile() {
        comparator = new ProductsComparator();
        fileImport = new FileImport();
    }

    public void treat(List<File> files) throws Exception {
        if (files == null || files.size() == 0 || files.stream().allMatch(Objects::isNull)) {
            throw new RuntimeException("Не выбраны файлы для импорта");
        }

        Set<Product> changedItemsForDB = new HashSet<>();
        for (File file : files) {
            fileImport.getProductsInManualMode(file);

            if (fileImport.isDeleteOldStatistic() && files.indexOf(file) < 1) {
                changedItemsForDB.addAll(Products.getInstance().resetLastImportCodes());
            }

            FileImportParameter[] importParameters = fileImport.getExcelFile().getMapper().getParameters().toArray(new FileImportParameter[]{});

            comparator.compare(Products.getInstance().getItems(), new DoublesPreprocessor(fileImport.getProductItems()).getTreatedItems(),
                    new ComparingParameters(new Adapter<Product>().convert(importParameters), new ComparingRulesImportNow(), WITHOUT_GONE));
            comparator.fixChanges();
        }

        result = comparator.getComparisonResult();

        Platform.runLater(() -> Dialogs.showMessage("Результаты импорта", "Новых позиций: " +
                result.getNewItems().size() + "\nИзменённых позиций: " + result.getChangedItems().size()));

        if (result.getChangedItems().size() + result.getNewItems().size() > 0) {
            changedItemsForDB.addAll(result.getChangedItems());
            Products.getInstance().getItems().addAll(result.getNewItems());

            DataSelectorMenu.MENU_DATA_LAST_IMPORT_RESULT.activate();

            if (result.getNewItems().size() > 0)
                new ProductsDB().putData(result.getNewItems());// save new items to db
            if (changedItemsForDB.size() > 0)
                new ProductsDB().updateData(new ArrayList<>(changedItemsForDB));//save changed items to db

            /*Statement vacuumStatement = null;
            try {
                vacuumStatement = DataBase.getInstance().getDbConnection().createStatement();
                logger.debug("db file vacuum started");
                vacuumStatement.executeUpdate("vacuum;");
                logger.debug("db file vacuum finished");
            } catch (SQLException e) {
                logger.warn("sql request vacuum error: {}", e.getMessage());
            }*/

            logger.info("DB updating is finished");
        }
    }

    public File getReportFile(File targetFile) {
        return new NowImportResultToExcel().export(result, targetFile);
    }
}
