package utils.comparation.te;

import files.reports.ReportParameterEnum;
import files.reports.ReportToExcelTemplate_v2;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.product.Products;
import ui_windows.product.data.DataItem;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static files.reports.ReportParameterEnum.PRICE_COMPARISON_REPORT_PATH;
import static files.reports.ReportParameterEnum.PRICE_COMPARISON_RESULT;
import static ui_windows.product.data.DataItem.DATA_LOCAL_PRICE;
import static ui_windows.product.data.DataItem.DATA_ORDER_NUMBER;

@Log4j2
public class PriceChangesToExcelExporter extends ReportToExcelTemplate_v2 {
    private final String[] titles = {"ssn", "cost1", "cost2", "eCi", "imall"};
    private final int[] colWidths = {8200, 6150, 6150, 6150, 6150};
    private final CellStyle[] itemDataStyles = {
            styles.CELL_ALIGN_HCENTER_TEXT_FORMAT,
            styles.CELL_ALIGN_HCENTER_TEXT_FORMAT,
            styles.CELL_ALIGN_HCENTER_TEXT_FORMAT,
            styles.CELL_ALIGN_HCENTER_TEXT_FORMAT,
            styles.CELL_ALIGN_HCENTER_TEXT_FORMAT
    };
    private TotalPriceComparisonResult comparisonResult;

    public PriceChangesToExcelExporter(Map<ReportParameterEnum, Object> params) {
        super(params);
    }

    @Override
    protected void getAndCheckParams() throws RuntimeException {
        super.getAndCheckParams();

        confirmAndCheckReportFile(PRICE_COMPARISON_REPORT_PATH, "Сохранение отчета сравнения прайсов");
        comparisonResult = (TotalPriceComparisonResult) params.get(PRICE_COMPARISON_RESULT);
        if (comparisonResult == null) {
            log.warn("Illegal params: comparison result = null");
            throw new IllegalArgumentException("Param 'PRICE_COMPARISON_RESULT' not found");
        }
    }

    public void export() throws RuntimeException {
        log.trace("Checking params...");
        getAndCheckParams();

        SXSSFSheet sheet = workbook.createSheet("Cost changes report");

        log.trace("Filling report titles");
        fillTitles(sheet);

        log.trace("Filling report data");
        fillItems(sheet, comparisonResult.getNewItemList(), "10", "true");
        fillChangedItems(sheet, "10", "true");
        fillItems(sheet, comparisonResult.getGoneItemList(), "-10", "false");

        saveToFile();
    }

    private void fillItems(SXSSFSheet sheet, List<ImportedProduct> itemList, String eCi, String iMall) {
        Row row;
        for (ImportedProduct ip : itemList) {
            colIndex = 0;
            String cost = ip.getProperties().get(DATA_LOCAL_PRICE).getNewValue().toString().replaceAll(",", ".");
            row = sheet.createRow(rowNum++);
            fillCell(row.createCell(colIndex), ip.getProperties().get(DATA_ORDER_NUMBER).getNewValue(), itemDataStyles[colIndex++]);
            fillCell(row.createCell(colIndex), cost, itemDataStyles[colIndex++]);
            fillCell(row.createCell(colIndex), cost, itemDataStyles[colIndex++]);
            fillCell(row.createCell(colIndex), eCi, itemDataStyles[colIndex++]);
            fillCell(row.createCell(colIndex), iMall, itemDataStyles[colIndex++]);
        }
    }

    private void fillChangedItems(SXSSFSheet sheet, String eCi, String iMall) {
        Row row;
        List<ChangedItem> priceChangeList = comparisonResult.getChangedItemList().stream()
                .filter(item -> item.getChangedPropertyList().stream().anyMatch(property -> property.getDataItem() == DATA_LOCAL_PRICE))
                .collect(Collectors.toList());

        for (ChangedItem item : priceChangeList) {
            colIndex = 0;
            Object ssn = Products.getInstance().getProductByVendorMaterialId(item.getId()).getMaterial();
            String cost = getValue(item, DATA_LOCAL_PRICE).toString().replaceAll(",", ".");

            row = sheet.createRow(rowNum++);
            fillCell(row.createCell(colIndex), ssn, itemDataStyles[colIndex++]);
            fillCell(row.createCell(colIndex), cost, itemDataStyles[colIndex++]);
            fillCell(row.createCell(colIndex), cost, itemDataStyles[colIndex++]);
            fillCell(row.createCell(colIndex), eCi, itemDataStyles[colIndex++]);
            fillCell(row.createCell(colIndex), iMall, itemDataStyles[colIndex++]);
        }
    }

    private Object getValue(ChangedItem item, DataItem dataItem) throws RuntimeException {
        try {
            ChangedProperty property = item.getChangedPropertyList().stream()
                    .filter(prop -> prop.getDataItem() == dataItem)
                    .findFirst().orElseThrow((Supplier<Throwable>) () -> {
                        log.warn("can't find {} in {}", dataItem, item);
                        return new RuntimeException("can't find DataItem in ChangedItem");
                    });
            return property.getNewValue();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void fillTitles(SXSSFSheet sheet) throws RuntimeException {
        Row titleRow = sheet.createRow(rowNum++);

        colIndex = 0;
        for (String title : titles) {
            fillCell(titleRow.createCell(colIndex), title, styles.CELL_ALIGN_HCENTER_BOLD);
            sheet.setColumnWidth(colIndex, colWidths[colIndex]);
            colIndex++;
        }

        sheet.createFreezePane(0, 1);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, titles.length - 1));
    }
}

