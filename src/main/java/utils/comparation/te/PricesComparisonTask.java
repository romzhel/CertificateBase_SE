package utils.comparation.te;

import files.Folders;
import files.reports.ReportParameterEnum;
import lombok.extern.log4j.Log4j2;
import ui_windows.main_window.file_import_window.te.importer.ExcelFileImporter_v2;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import utils.Utils;

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
        String fileName = String.format("prices_comparison_report_%s_vs_%s", newPriceFile.getName(),
                prevPriceFile.getName()).replaceAll(".xlsx", "").concat(".xlsx");
        params.put(PRICE_COMPARISON_REPORT_PATH, reportFolder == null ? Paths.get(fileName) : reportFolder.resolve(fileName));
        params.put(PRICE_COMPARISON_RESULT, priceComparisonResult);

        TotalPriceComparisonResultToExcelExporter toExcelExporter = new TotalPriceComparisonResultToExcelExporter(params);
        toExcelExporter.export();

        log.info("creating STEP export report");
        fileName = String.format("STEP_import_%s.xlsx", Utils.getDateTimeForFileName());
        Path reportPath = Folders.getInstance().getTempFolder().resolve(fileName);
        new StepExportToExcelExporter(priceComparisonResult, reportPath).run();
        Utils.openFile(Folders.getInstance().getTempFolder().toFile());
    }
}
