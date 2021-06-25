package files.reports;

import lombok.extern.log4j.Log4j2;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import ui_windows.product.Product;
import ui_windows.product.data.DataItem;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static files.reports.ReportParameterEnum.*;

@Log4j2
public class ReportToExcel extends ReportToExcelTemplate_v2 {
    private List<Product> productItems;
    private List<DataItem> dataItems;

    public ReportToExcel(Map<ReportParameterEnum, Object> params) {
        super(params);
    }

    @Override
    protected void getAndCheckParams() throws RuntimeException {
        super.getAndCheckParams();

        confirmAndCheckReportFile(REPORT_PATH, "Сохранение файла");

        productItems = (List<Product>) params.get(REPORT_ITEMS);
        dataItems = (List<DataItem>) params.get(REPORT_COLUMNS);

        if (productItems == null || dataItems == null) {
            log.warn("Illegal params");
            throw new IllegalArgumentException();
        }
    }

    public File export() throws RuntimeException {
        getAndCheckParams();

        SXSSFSheet sheet = workbook.createSheet("custom_report");
        sheet.setRandomAccessWindowSize(100);

        rowNum = 0;
        SXSSFRow row = sheet.createRow(rowNum++);
        SXSSFCell cell;

        colIndex = 0;
        for (DataItem die : dataItems) {
            fillCell(row.createCell(colIndex++), die.getDisplayingName(), styles.CELL_ALIGN_HLEFT_BOLD);
            sheet.trackColumnForAutoSizing(colIndex - 1);
            sheet.autoSizeColumn(colIndex - 1);
        }

        sheet.createFreezePane(0, 1);

        long t = System.nanoTime();

        Map<Object, Long> times = new HashMap<>();

        for (Product product : productItems) {
            long t0 = System.nanoTime();
            row = sheet.createRow(rowNum++);
            long t1 = System.nanoTime();
            times.merge("row creating", TimeUnit.NANOSECONDS.toMillis(t1 - t0), Long::sum);

            colIndex = 0;
            for (DataItem die : dataItems) {
                t0 = System.nanoTime();
                fillCell(row.createCell(colIndex++), die.getValue(product), styles.CELL_ALIGN_HLEFT);
                t1 = System.nanoTime();
                times.merge(die, TimeUnit.NANOSECONDS.toMillis(t1 - t0), Long::sum);
            }


            /*if (rowNum % 1000 == 0) {
                t0 = System.nanoTime();
                try {
                    sheet.flushRows(1000);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                t1 = System.nanoTime();
                times.merge("flushing", t1 - t0, Long::sum);
            }*/
        }


        long t0 = System.nanoTime();
        saveToFile();
        long t1 = System.nanoTime();
        times.put("save to file", TimeUnit.NANOSECONDS.toMillis(t1 - t0));

        log.info("spent time, ms: {}", times.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
        log.info("total time = {} ms", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t));

        return reportFile;
    }
}
