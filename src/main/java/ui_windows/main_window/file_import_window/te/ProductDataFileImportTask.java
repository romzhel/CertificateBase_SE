package ui_windows.main_window.file_import_window.te;

import core.ThreadManager;
import database.ProductsDB;
import exceptions.OperationCancelledByUserException;
import files.Folders;
import files.reports.NowImportResultToExcel_v2;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui.Dialogs;
import ui_windows.main_window.DataSelectorMenu;
import ui_windows.main_window.file_import_window.te.importer.ExcelFileImporter_v2;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.options_window.product_lgbk.ProductLgbks;
import ui_windows.product.Product;
import ui_windows.product.Products;
import utils.Utils;
import utils.comparation.se.ComparingRules;
import utils.comparation.se.ComparingRulesImportNow;
import utils.comparation.te.ChangesFixer_te;
import utils.comparation.te.TotalComparator;
import utils.comparation.te.TotalComparisonResult;
import utils.comparation.te.TotalComparisonResultService;

import java.util.*;

import static ui_windows.main_window.file_import_window.te.FilesImportParametersEnum.*;

public class ProductDataFileImportTask implements Runnable {
    private static final Logger logger = LogManager.getLogger(ProductDataFileImportTask.class);
    private final FilesImportParameters filesImportParameters;

    public ProductDataFileImportTask(FilesImportParameters filesImportParameters) {
        this.filesImportParameters = filesImportParameters;
    }

    @Override
    public void run() throws RuntimeException {
        logger.info("Выполнение импорта c параметрами {}", filesImportParameters);

        if (filesImportParameters == null || filesImportParameters.getFiles().size() == 0) {
            throw new OperationCancelledByUserException();
        }

        boolean applyChanges = filesImportParameters.getParams().getOrDefault(APPLY_CHANGES, false);

        Set<Product> changedItemsForDB = new HashSet<>();
        ExcelFileImporter_v2 importer = new ExcelFileImporter_v2();
        TotalComparator comparator = new TotalComparator();
        ChangesFixer_te changesFixer = new ChangesFixer_te();
        TotalComparisonResultService costResetter = new TotalComparisonResultService();

        if (applyChanges) {
            boolean deleteOldStatistic = filesImportParameters.getParams().getOrDefault(RESET_STATISTIC, false);
            if (deleteOldStatistic) {
                changedItemsForDB.addAll(Products.getInstance().resetLastImportCodes());
                logger.debug("Old statistic was deleted");
            }
        }

        List<Product> existItems = Products.getInstance().getItems();
        List<ImportedProduct> importedItems = importer.getProducts(filesImportParameters.getFiles(), true, true);
        ComparingRules<Product> comparingRules = new ComparingRulesImportNow();
        TotalComparisonResult comparisonResult = comparator.compare(existItems, importedItems, comparingRules);

        boolean treatItemWithNoCost = filesImportParameters.getParams().getOrDefault(RESET_NON_FOUND_ITEMS_COST, false);
        if (treatItemWithNoCost) {
            costResetter.calcNoCostItemsInResult(comparisonResult);
            logger.trace("Reset cost of non-found positions during import");
        }

        Platform.runLater(() -> Dialogs.showMessage("Результаты импорта",
                String.format("Новых позиций: %d\n" +
                                "Изменённых позиций: %d\n" +
                                "Актуальных позиций: %d\n" +
                                "Ненайденных позиций: %d\n" +
                                "Позиций с защитой полей: %d\n" +
                                "Позиций с неизменёнными полями из-за защиты: %d\n" +
                                (treatItemWithNoCost ? "\n" + "Позиций без цены: %d" : "%s"),
                        comparisonResult.getNewItemList().size(),
                        comparisonResult.getChangedItemList().size(),
                        comparisonResult.getNonChangedItemList().size(),
                        comparisonResult.getGoneItemList().size(),
                        comparisonResult.getProtectChangeItemList().size(),
                        comparisonResult.getNonChangedProtectedItemList().size(),
                        treatItemWithNoCost ? comparisonResult.getNoCostItemList().size() : ""))
        );

        if (applyChanges) {
            logger.info("find and treat GBK/Hierarchy structure changes");//STRONGLY before apply changes to existing Products
            ProductLgbks.getInstance().treatStructureChanges(comparisonResult.getChangedItemList());

            List<Product> newItemsForDB = new LinkedList<>(changesFixer.fixNewProducts(comparisonResult.getNewItemList()));
            changedItemsForDB.addAll(changesFixer.fixChangedProducts(comparisonResult.getChangedItemList()));
            changedItemsForDB.addAll(changesFixer.fixChangedProducts(comparisonResult.getNoCostItemList()));

            changedItemsForDB.addAll(changesFixer.fixPropertyProtectChanges(comparisonResult.getProtectChangeItemList()));

            DataSelectorMenu.MENU_DATA_LAST_IMPORT_RESULT.activate();

            logger.info("Start updating DB");
            if (newItemsForDB.size() > 0) {
                new ProductsDB().putData(newItemsForDB);
            }

            if (changedItemsForDB.size() > 0) {
                new ProductsDB().updateData(new ArrayList<>(changedItemsForDB));
            }

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

        String fileName = Utils.getDateTimeForFileName().concat("_import_report.xlsx");
        ThreadManager.startNewThread("Excel Report Thread",
                new NowImportResultToExcel_v2(comparisonResult, Folders.getInstance().getTempFolder().resolve(fileName)),
                throwable -> {
                    logger.error("error creating import result file: {}", throwable.getMessage());
                    Platform.runLater(() -> Dialogs.showMessage("Ошибка во время выполнения", throwable.getMessage()));
                });
        Utils.openFile(Folders.getInstance().getTempFolder().toFile());
    }
}
