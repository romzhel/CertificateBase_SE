package files.reports;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.main_window.file_import_window.te.importer.ImportedProduct;
import ui_windows.product.Product;
import ui_windows.product.Products;
import utils.Utils;
import utils.comparation.te.ChangedItem;
import utils.comparation.te.ChangedProperty;
import utils.comparation.te.ImportedProductToProductMapper;
import utils.comparation.te.TotalComparisonResult;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ui_windows.product.data.DataItem.*;

public class NowImportResultToExcel_v2 extends ReportToExcelTemplate_v3<TotalComparisonResult> {
    public static final Logger logger = LogManager.getLogger(NowImportResultToExcel_v2.class);

    public NowImportResultToExcel_v2(TotalComparisonResult data, Path reportFilePath) {
        super(data, reportFilePath);
    }

    @Override
    public void run() {
        getAndCheckParams();
        confirmAndCheckReportFile("Выберите путь сохранения файла отчета импорта");

        logger.trace("filling main import report sheet");
        fillCommonImportSheet();

        if (data.getNonChangedProtectedItemList().size() > 0) {
            logger.trace("filling items with blocked changes");
            fillBlockedChangesSheet();
        }

        if (data.getNoCostItemList().size() > 0) {
            logger.trace("filling items without new price sheet");
            fillNoCostItemsSheet();
        }

        saveToFile();
    }

    private void fillCommonImportSheet() {
        currentSheet = workbook.createSheet("ImportReport_".concat(Utils.getDateTimeForFileName()));
        fillTitles();

        ImportedProductToProductMapper mapper = new ImportedProductToProductMapper();
        for (ImportedProduct importedProduct : data.getNewItemList()) {
            Product newProduct = Products.getInstance().getProductByVendorMaterialId(importedProduct.getId());

            if (newProduct == null) {//для случаев, когда изменения не применяются
                newProduct = mapper.mapToProduct(importedProduct);
            }

            fillRow(0,
                    new ReportCell(DATA_FAMILY_NAME.getValue(newProduct), styles.CELL_ALIGN_HLEFT),
                    new ReportCell(DATA_RESPONSIBLE.getValue(newProduct), styles.CELL_ALIGN_HLEFT),
                    new ReportCell(DATA_ORDER_NUMBER.getValue(newProduct), styles.CELL_ALIGN_HLEFT),
                    new ReportCell(DATA_ARTICLE.getValue(newProduct), styles.CELL_ALIGN_HLEFT),
                    new ReportCell(DATA_IS_IN_PRICE.getValue(newProduct), styles.CELL_ALIGN_HLEFT),
                    new ReportCell(DATA_IN_WHICH_PRICE_LIST.getValue(newProduct), styles.CELL_ALIGN_HLEFT),
                    new ReportCell(DATA_DCHAIN_WITH_COMMENT.getValue(newProduct), styles.CELL_ALIGN_HLEFT),
                    new ReportCell("added", styles.CELL_ALIGN_HLEFT)
            );
        }

        List<ChangedItem> sortedList = new ArrayList<>(data.getChangedItemList());
        sortedList.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));
        for (ChangedItem changedItem : sortedList) {
            fillChangedItemData(changedItem);
        }
    }

    private void fillBlockedChangesSheet() {
        currentSheet = workbook.createSheet("BlockedChanges_".concat(Utils.getDateTimeForFileName()));
        rowNum = 0;

        fillRow(0,
                new ReportCell("Направление", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("Ответственный", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("Заказной номер", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("Артикул", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("В прайсе", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("Прайс", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("Доступность", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("Тип изменения", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("Изменяемое свойство", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("Исходное значение", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("     ", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("Заблокированное изменение", styles.CELL_ALIGN_HLEFT_BOLD)
        );

        List<ChangedItem> sortedList = new ArrayList<>(data.getNonChangedProtectedItemList());
        sortedList.sort(Comparator.comparing(ChangedItem::getId));
        for (ChangedItem changedItem : sortedList) {
            Product changedProduct = Products.getInstance().getProductByVendorMaterialId(changedItem.getId());

            for (ChangedProperty changedProperty : changedItem.getProtectedField()) {

                fillRow(0,
                        new ReportCell(DATA_FAMILY_NAME.getValue(changedProduct), styles.CELL_ALIGN_HLEFT),
                        new ReportCell(DATA_RESPONSIBLE.getValue(changedProduct), styles.CELL_ALIGN_HLEFT),
                        new ReportCell(DATA_ORDER_NUMBER.getValue(changedProduct), styles.CELL_ALIGN_HLEFT),
                        new ReportCell(DATA_ARTICLE.getValue(changedProduct), styles.CELL_ALIGN_HLEFT),
                        new ReportCell(DATA_IS_IN_PRICE.getValue(changedProduct), styles.CELL_ALIGN_HLEFT),
                        new ReportCell(DATA_IN_WHICH_PRICE_LIST.getValue(changedProduct), styles.CELL_ALIGN_HLEFT),
                        new ReportCell(DATA_DCHAIN_WITH_COMMENT.getValue(changedProduct), styles.CELL_ALIGN_HLEFT),

                        new ReportCell("not changed", styles.CELL_ALIGN_HLEFT),
                        new ReportCell(changedProperty.getDataItem().getDisplayingName(), styles.CELL_ALIGN_HLEFT),
                        new ReportCell(changedProperty.getOldValue().toString(), styles.CELL_ALIGN_HLEFT),
                        new ReportCell("<-", styles.CELL_ALIGN_HCENTER),
                        new ReportCell(changedProperty.getNewValue().toString(), styles.CELL_ALIGN_HLEFT)
                );
            }
        }
    }

    private void fillNoCostItemsSheet() {
        currentSheet = workbook.createSheet("NoNewCost_".concat(Utils.getDateTimeForFileName()));
        fillTitles();

        for (ChangedItem changedItem : data.getNoCostItemList()) {
            fillChangedItemData(changedItem);
        }
    }

    private void fillTitles() {
        rowNum = 0;

        fillRow(0,
                new ReportCell("Направление", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("Ответственный", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("Заказной номер", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("Артикул", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("В прайсе", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("Прайс", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("Доступность", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("Тип изменения", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("Изменённое свойство", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("Исходное значение", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("     ", styles.CELL_ALIGN_HLEFT_BOLD),
                new ReportCell("Новое значение", styles.CELL_ALIGN_HLEFT_BOLD)
        );

        setColumnSize();
        decorateTitles();
    }

    private void fillChangedItemData(ChangedItem changedItem) {
        for (ChangedProperty changedProperty : changedItem.getChangedPropertyList()) {
            Product changedProduct = Products.getInstance().getProductByVendorMaterialId(changedItem.getId());

            fillRow(0,
                    new ReportCell(DATA_FAMILY_NAME.getValue(changedProduct), styles.CELL_ALIGN_HLEFT),
                    new ReportCell(DATA_RESPONSIBLE.getValue(changedProduct), styles.CELL_ALIGN_HLEFT),
                    new ReportCell(DATA_ORDER_NUMBER.getValue(changedProduct), styles.CELL_ALIGN_HLEFT),
                    new ReportCell(DATA_ARTICLE.getValue(changedProduct), styles.CELL_ALIGN_HLEFT),
                    new ReportCell(DATA_IS_IN_PRICE.getValue(changedProduct), styles.CELL_ALIGN_HLEFT),
                    new ReportCell(DATA_IN_WHICH_PRICE_LIST.getValue(changedProduct), styles.CELL_ALIGN_HLEFT),
                    new ReportCell(DATA_DCHAIN_WITH_COMMENT.getValue(changedProduct), styles.CELL_ALIGN_HLEFT),

                    new ReportCell("changed", styles.CELL_ALIGN_HLEFT),
                    new ReportCell(changedProperty.getDataItem().getDisplayingName(), styles.CELL_ALIGN_HLEFT),
                    new ReportCell(changedProperty.getOldValue().toString(), styles.CELL_ALIGN_HLEFT),
                    new ReportCell("->", styles.CELL_ALIGN_HCENTER),
                    new ReportCell(changedProperty.getNewValue().toString(), styles.CELL_ALIGN_HLEFT)
            );
        }
    }
}
