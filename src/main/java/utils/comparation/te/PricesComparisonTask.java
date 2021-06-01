package utils.comparation.te;

import lombok.extern.log4j.Log4j2;
import ui_windows.main_window.file_import_window.te.importer.ExcelFileImporter;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Log4j2
public class PricesComparisonTask {


    public void comparePriceFilesAndGenerateReport(File prevPriceFile, File newPriceFile, File reportFile) throws RuntimeException {
        if (prevPriceFile == null || !prevPriceFile.exists() || newPriceFile == null || !newPriceFile.exists() || reportFile == null) {
            log.warn("One of needed files is absent {}, {}, {}", prevPriceFile, newPriceFile, reportFile);
            throw new RuntimeException("Impossible compare non-existing price files!!!");
        }

        ExcelFileImporter fileImporter = new ExcelFileImporter();
        List<ImportedProduct> prevPriceItems = fileImporter.getProducts(Collections.singletonList(prevPriceFile), false);
        List<ImportedProduct> newPriceItems = fileImporter.getProducts(Collections.singletonList(newPriceFile), false);

        TotalPriceComparator priceComparator = new TotalPriceComparator();
        TotalPriceComparisonResult priceComparisonResult = priceComparator.compare(prevPriceItems, newPriceItems);

        TotalPriceComparisonResultToExcelExporter toExcelExporter = new TotalPriceComparisonResultToExcelExporter();
        toExcelExporter.export(priceComparisonResult, reportFile);
    }
}
