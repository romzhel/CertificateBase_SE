package ui_windows.main_window.file_import_window.se;

import files.reports.NowImportResultToExcel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.comparation.se.ProductsComparator;
import utils.comparation.se.ProductsComparisonResult;

import java.io.File;
import java.util.List;

public class ImportNowFile {
    private static final Logger logger = LogManager.getLogger(ImportNowFile.class);
    private ProductsComparisonResult result;
    private ProductsComparator comparator;
    private FileImport fileImport;

    public ImportNowFile() {
        comparator = new ProductsComparator();
        fileImport = new FileImport();
    }

    public void treat(List<File> files) throws Exception {
        /*if (files == null || files.size() == 0 || files.stream().allMatch(Objects::isNull)) {
            throw new RuntimeException("Не выбраны файлы для импорта");
        }

        Set<Product> changedItemsForDB = new HashSet<>();
        ComparingParameters<Product> comparingParameters = null;
        boolean resetCostNonFoundItems = false;
        for (File file : files) {
            fileImport.getProductsInManualMode(file);

            if (fileImport.isDeleteOldStatistic() && files.indexOf(file) < 1) {
                changedItemsForDB.addAll(Products.getInstance().resetLastImportCodes());
            }

            resetCostNonFoundItems |= fileImport.isResetCostNonFoundItem();

            FileImportParameter[] importParameters = fileImport.getExcelFile().getMapper().getParameters().toArray(new FileImportParameter[]{});
            comparingParameters = new ComparingParameters<>(new Adapter<Product>().convert(importParameters),
                    new ComparingRulesImportNow(), WITHOUT_GONE);

            comparator.compare(Products.getInstance().getItems(),
                    new DoublesPreprocessor().getTreatedItems(fileImport.getProductItems()), comparingParameters);
        }

        comparator.fixChanges();

        result = comparator.getComparisonResult();

        //resetting cost of absent items
//        List<ObjectsComparatorResultSe<Product>> itemsWithOldPriceResult = result.getItemsWithoutNewPriceResult();
        List<Product> itemsWithOldPrice = new ArrayList<>();
        if (resetCostNonFoundItems) {
            List<ObjectsComparatorResultSe<Product>> itemsWithoutNewPriceResult = result.calcItemsWithoutNewPriceResult();
            for (ObjectsComparatorResultSe<Product> resultItem : itemsWithoutNewPriceResult) {
                comparingParameters.getComparingRules().addHistoryComment(resultItem);
            }
            itemsWithOldPrice.addAll(result.getItemsWithoutNewPrice());
        }

        Platform.runLater(() -> Dialogs.showMessage("Результаты импорта",
                String.format("Новых позиций: %d\nИзменённых позиций: %d\nАктуальных позиций: %d\nНенайденных позиций: %d\n" +
                                "Позиций без цены: %d",
                        result.getNewItems().size(),
                        result.getChangedItems().size(),
                        result.getNonChangedItems().size(),
                        result.getGoneItems().size(),
                        itemsWithOldPrice.size())));

        if (result.getChangedItems().size() + result.getNewItems().size() + itemsWithOldPrice.size() > 0) {
            changedItemsForDB.addAll(result.getChangedItems());
            changedItemsForDB.addAll(itemsWithOldPrice);
            Products.getInstance().getItems().addAll(result.getNewItems());

            DataSelectorMenu.MENU_DATA_LAST_IMPORT_RESULT.activate();

            if (result.getNewItems().size() > 0)
                new ProductsDB().putData(result.getNewItems());// save new items to db
            if (changedItemsForDB.size() > 0)
                new ProductsDB().updateData(new ArrayList<>(changedItemsForDB));//save changed items to db

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
        }*/
    }

    public File getReportFile(File targetFile) {
        return new NowImportResultToExcel().export(result, targetFile);
    }
}
