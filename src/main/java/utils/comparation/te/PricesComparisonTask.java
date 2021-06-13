package utils.comparation.te;

import files.reports.ReportParameterEnum;
import lombok.extern.log4j.Log4j2;
import ui_windows.main_window.file_import_window.te.importer.ExcelFileImporter_v2;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static files.reports.ReportParameterEnum.PRICE_COMPARISON_REPORT_PATH;
import static files.reports.ReportParameterEnum.PRICE_COMPARISON_RESULT;

@Log4j2
public class PricesComparisonTask {


    public void comparePriceFilesAndGenerateReport(File prevPriceFile, File newPriceFile, Path reportFolder) throws RuntimeException {
        if (prevPriceFile == null || !prevPriceFile.exists() || newPriceFile == null || !newPriceFile.exists()) {
            log.warn("One of needed files is absent: prev '{}', new '{}'", prevPriceFile, newPriceFile);
            throw new RuntimeException("Not all needed files for price comparison and report were provided!!!");
        }

        ExcelFileImporter_v2 fileImporter = new ExcelFileImporter_v2();
        List<ImportedProduct> prevPriceItems = fileImporter.getProducts(Collections.singletonList(prevPriceFile), false);
        List<ImportedProduct> newPriceItems = fileImporter.getProducts(Collections.singletonList(newPriceFile), false);

        TotalPriceComparator priceComparator = new TotalPriceComparator();
        TotalPriceComparisonResult priceComparisonResult = priceComparator.compare(prevPriceItems, newPriceItems);
        priceComparisonResult.getSheetNames().addAll(fileImporter.getSheetNames());

        Map<ReportParameterEnum, Object> params = new HashMap<>();
        String fileName = String.format("%s_%s_vs_%s.xlsx",
                "Prices_comparison_report",
                newPriceFile.getName().replaceAll(".xlsx", ""),
                prevPriceFile.getName().replaceAll(".xlsx", ""));
        reportFolder = reportFolder == null ? Paths.get(fileName) : reportFolder.resolve(fileName);
        params.put(PRICE_COMPARISON_REPORT_PATH, reportFolder);
        params.put(PRICE_COMPARISON_RESULT, priceComparisonResult);

        TotalPriceComparisonResultToExcelExporter toExcelExporter = new TotalPriceComparisonResultToExcelExporter(params);
        toExcelExporter.export();
    }
}
