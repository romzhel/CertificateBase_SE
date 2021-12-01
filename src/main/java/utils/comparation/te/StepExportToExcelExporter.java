package utils.comparation.te;

import files.reports.ReportCell;
import files.reports.ReportToExcelTemplate_v3;
import lombok.extern.log4j.Log4j2;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.product.Product;
import ui_windows.product.Products;
import ui_windows.product.data.DataItem;
import utils.Utils;

import java.nio.file.Path;
import java.util.function.Supplier;

import static ui_windows.product.data.DataItem.DATA_LOCAL_PRICE;

@Log4j2
public class StepExportToExcelExporter extends ReportToExcelTemplate_v3<TotalPriceComparisonResult> {

    public StepExportToExcelExporter(TotalPriceComparisonResult data, Path reportFilePath) {
        super(data, reportFilePath);
    }

    @Override
    public void run() {
        getAndCheckParams();
        confirmAndCheckReportFile("Выберите путь сохранения файла отчета импорта");

        log.trace("filling STEP export report sheet");
        currentSheet = workbook.createSheet(String.format("STEP_import_%s", Utils.getDateTimeForFileName()));
        rowNum = 0;

        fillRow(new ReportCell("ssn", styles.CELL_ALIGN_HCENTER_BOLD),
                new ReportCell("cost1", styles.CELL_ALIGN_HCENTER_BOLD),
                new ReportCell("cost2", styles.CELL_ALIGN_HCENTER_BOLD),
                new ReportCell("eCi", styles.CELL_ALIGN_HCENTER_BOLD),
                new ReportCell("imall", styles.CELL_ALIGN_HCENTER_BOLD),
                new ReportCell("Product Classification Link", styles.CELL_ALIGN_HCENTER_BOLD)
        );

        setColumnSize(8200, 5000, 5000, 5000, 5000, 7500);

        fillNewItems();
        fillChangedItems();
        fillGoneItems();

        saveToFile();
    }

    private void fillNewItems() {
        for (ImportedProduct ip : data.getNewItemList()) {
            Product product = Products.getInstance().getProductByVendorMaterialId(ip.getId());

            if (product == null) {
                log.warn("can't find product for {}", ip.getId());
                continue;
            }

            String ssn = Products.getInstance().getSsnNotEmpty(product);
            String cost = ip.getProperties().get(DATA_LOCAL_PRICE).getNewValue().toString().replaceAll(",", ".");

            checkZeroCostAndFillRow(ssn, cost);
        }
    }

    private void fillChangedItems() {
        data.getChangedItemList().stream()
                .filter(item -> item.getChangedPropertyList().stream().anyMatch(property -> property.getDataItem() == DATA_LOCAL_PRICE))
                .forEach(item -> {
                    String ssn = Products.getInstance().getSsnNotEmpty(Products.getInstance().getProductByVendorMaterialId(item.getId()));
                    String cost = getValue(item, DATA_LOCAL_PRICE).toString().replaceAll(",", ".");

                    checkZeroCostAndFillRow(ssn, cost);
                });
    }

    private void fillGoneItems() {
        for (ImportedProduct ip : data.getGoneItemList()) {
            Product product = Products.getInstance().getProductByVendorMaterialId(ip.getId());

            if (product == null) {
                log.warn("can't find product for {}", ip.getId());
                continue;
            }

            String ssn = Products.getInstance().getSsnNotEmpty(product);
            String cost = ip.getProperties().get(DATA_LOCAL_PRICE).getNewValue().toString().replaceAll(",", ".");

            fillRow(new ReportCell(ssn, styles.CELL_ALIGN_HCENTER_TEXT_FORMAT),
                    new ReportCell(cost, styles.CELL_ALIGN_HCENTER_TEXT_FORMAT),
                    new ReportCell(cost, styles.CELL_ALIGN_HCENTER_TEXT_FORMAT),
                    new ReportCell("-10", styles.CELL_ALIGN_HCENTER_TEXT_FORMAT),
                    new ReportCell("false", styles.CELL_ALIGN_HCENTER_TEXT_FORMAT),
                    new ReportCell("OPC_2092617", styles.CELL_ALIGN_HCENTER_TEXT_FORMAT)
            );
        }
    }

    public void checkZeroCostAndFillRow(String ssn, String cost) {
        if (cost.matches("^0+\\.0+$")) {
            log.info("position '{}' with cost = {} was ignored", ssn, cost);
            return;
        }

        fillRow(new ReportCell(ssn, styles.CELL_ALIGN_HCENTER_TEXT_FORMAT),
                new ReportCell(cost, styles.CELL_ALIGN_HCENTER_TEXT_FORMAT),
                new ReportCell(cost, styles.CELL_ALIGN_HCENTER_TEXT_FORMAT),
                new ReportCell("10", styles.CELL_ALIGN_HCENTER_TEXT_FORMAT),
                new ReportCell("true", styles.CELL_ALIGN_HCENTER_TEXT_FORMAT),
                new ReportCell("OPC_2092617", styles.CELL_ALIGN_HCENTER_TEXT_FORMAT)
        );
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
}

