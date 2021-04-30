package ui_windows.main_window.file_import_window.te;

import exceptions.DataNotSelectedException;
import exceptions.OperationCancelledByUserException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.main_window.file_import_window.te.importer.ExcelFileImporter;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.product.Product;
import ui_windows.product.Products;
import utils.comparation.se.ProductsComparator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ui_windows.main_window.file_import_window.te.FilesImportParametersEnum.APPLY_CHANGES;
import static ui_windows.main_window.file_import_window.te.FilesImportParametersEnum.RESET_STATISTIC;

public class ProductDataFileImportTask implements Runnable {
    private static final Logger logger = LogManager.getLogger(ProductDataFileImportTask.class);
    private FilesImportParameters filesImportParameters;

    public ProductDataFileImportTask(FilesImportParameters filesImportParameters) {
        this.filesImportParameters = filesImportParameters;
    }

    @Override
    public void run() throws RuntimeException {
        logger.info("Выполнение импорта c параметрами {}", filesImportParameters);

        if (filesImportParameters == null) {
            throw new OperationCancelledByUserException();
        }

        if (filesImportParameters.getFiles().size() == 0) {
            throw new DataNotSelectedException();
        }

        boolean applyChanges = filesImportParameters.getParams().getOrDefault(APPLY_CHANGES, false);

        Set<Product> changedItems = new HashSet<>();
        ExcelFileImporter importer = new ExcelFileImporter();
        ExcelFileImportUtils importUtils = ExcelFileImportUtils.getInstance();
        ProductsComparator comparator = new ProductsComparator();

        if (applyChanges) {
            boolean deleteOldStatistic = filesImportParameters.getParams().getOrDefault(RESET_STATISTIC, false);
            if (deleteOldStatistic) {
                changedItems.addAll(Products.getInstance().resetLastImportCodes());
                logger.debug("Old statistic was deleted");
            }
        }

        List<ImportedProduct> importedItems = importer.getProducts(filesImportParameters.getFiles(), true);

        System.out.println();
        /*ComparingParameters<Product> comparingParameters = new ComparingParameters<>(columnParams,
                new ComparingRulesImportNow(), WITHOUT_GONE);
        comparator.compare(Products.getInstance().getItems(), productsFromFile, comparingParameters);

        if (applyChanges) {
            comparator.fixChanges();
        }

        ProductsComparisonResult result = comparator.getComparisonResult();

        boolean treatItemWithNoCost = filesImportParameters.getParams().getOrDefault(RESET_NON_FOUND_ITEMS_COST, false);
        List<Product> itemsWithOldPrice = new ArrayList<>();
        if (treatItemWithNoCost) {
            logger.trace("Сбрасываем стоимость ненайденных позиций");
            ComparingRulesImportNow comparingRules = new ComparingRulesImportNow();
            List<ObjectsComparatorResultSe<Product>> itemsWithoutNewPriceResult = result.calcItemsWithoutNewPriceResult();
            for (ObjectsComparatorResultSe<Product> resultItem : itemsWithoutNewPriceResult) {
                comparingRules.addHistoryComment(resultItem);
            }
            itemsWithOldPrice.addAll(result.getItemsWithoutNewPrice());
        }

        Platform.runLater(() -> Dialogs.showMessage("Результаты импорта",
                String.format("Новых позиций: %d\nИзменённых позиций: %d\nАктуальных позиций: %d\nНенайденных позиций: %d" +
                                (treatItemWithNoCost ? "\nПозиций без цены: %d" : "%s"),
                        result.getNewItems().size(),
                        result.getChangedItems().size(),
                        result.getNonChangedItems().size(),
                        result.getGoneItems().size(),
                        treatItemWithNoCost ? itemsWithOldPrice.size() : "")));

        final File[] importReport = new File[1];
        ThreadManager.startNewThread("Excel Report Thread", () -> {
                    NowImportResultToExcel reportCreator = new NowImportResultToExcel();
                    importReport[0] = reportCreator.export(result, ThreadManager.executeFxTaskSafe(() ->
                            new Dialogs().selectAnyFile(
                                    MainWindow.getMainStage(),
                                    "Сохранение отчёта импорта",
                                    Dialogs.EXCEL_FILES,
                                    Utils.getDateTimeForFileName().concat("_import_report.xlsx")).get(0)
                    ));

                    Utils.openFile(importReport[0]);
                },
                throwable -> {
                    logger.error("can't open import report file '{}', error: {}", importReport[0], throwable.getMessage());
                    return null;
                });

        if (applyChanges) {
            changedItems.addAll(result.getChangedItems());
            changedItems.addAll(itemsWithOldPrice);

            Products.getInstance().getItems().addAll(result.getNewItems());
            DataSelectorMenu.MENU_DATA_LAST_IMPORT_RESULT.activate();

            if (changedItems.size() + result.getNewItems().size() > 0) {
                logger.info("Start updating DB");
                if (result.getNewItems().size() > 0)
                    new ProductsDB().putData(result.getNewItems());// save new items to db
                if (changedItems.size() > 0)
                    new ProductsDB().updateData(new ArrayList<>(changedItems));//save changed items to db

            *//*Statement vacuumStatement = null;
                try {
                    vacuumStatement = DataBase.getInstance().getDbConnection().createStatement();
                    logger.debug("db file vacuum started");
                    vacuumStatement.executeUpdate("vacuum;");
                    logger.debug("db file vacuum finished");
                } catch (SQLException e) {
                    logger.warn("sql request vacuum error: {}", e.getMessage());
                }*//*

                logger.info("DB updating is finished");
            }
        }*/
    }
}
