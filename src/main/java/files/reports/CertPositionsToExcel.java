package files.reports;

import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.util.CellRangeAddress;
import ui_windows.options_window.certificates_editor.Certificate;
import ui_windows.product.Product;
import ui_windows.product.data.DataItem;
import utils.Utils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static ui_windows.product.data.DataItem.*;

@Log4j2
public class CertPositionsToExcel extends ReportToExcelTemplate_v3<Map<Certificate, Map<String, List<Product>>>> {
    private DataItem[] productProps;

    public CertPositionsToExcel(Map<Certificate, Map<String, List<Product>>> data, Path reportFilePath) {
        super(data, reportFilePath);
    }

    @Override
    public void run() {
        getAndCheckParams();
        confirmAndCheckReportFile("Выберите путь сохранения файла отчета импорта");

        log.trace("filling report sheet");
        currentSheet = workbook.createSheet("Certs_positions_".concat(Utils.getDateTimeForFileName()));
        productProps = new DataItem[]{
                DATA_ORDER_NUMBER_PRINT_NOT_EMPTY,
                DATA_ARTICLE,
                DATA_DESCRIPTION,
                DATA_FAMILY_NAME,
                DATA_RESPONSIBLE,
                DATA_DCHAIN_WITH_COMMENT,
                DATA_IN_WHICH_PRICE_LIST
        };

        fillTitles();
        fillSheet();

        saveToFile();
    }

    private void fillSheet() {
        for (Map.Entry<Certificate, Map<String, List<Product>>> entry : data.entrySet()) {
            String certName = String.format("%s (%s)", entry.getKey().getName(), entry.getKey().getFileName());
            fillRow(0, new ReportCell(certName, styles.CELL_ALIGN_HLEFT_BOLD));

            for (Map.Entry<String, List<Product>> nameMapEntry : entry.getValue().entrySet()) {
                fillRow(1,
                        new ReportCell(nameMapEntry.getKey(), styles.CELL_ALIGN_HLEFT_BOLD));

                for (Product product : nameMapEntry.getValue()) {
                    List<ReportCell> reportCells = new LinkedList<>();
                    for (DataItem dataItem : productProps) {
                        reportCells.add(new ReportCell(dataItem.getValue(product), styles.CELL_ALIGN_HLEFT));
                    }

                    fillRow(2, reportCells.toArray(new ReportCell[productProps.length]));
                }
            }
        }

        currentSheet.createFreezePane(colIndex + 10, 1);
        currentSheet.setAutoFilter(new CellRangeAddress(0, 0, 0, colIndex));
    }

    private void fillTitles() {
        List<ReportCell> titles = new ArrayList<>();
        titles.add(new ReportCell("Сертификат", styles.CELL_ALIGN_HLEFT_BOLD));
        titles.add(new ReportCell("Сокращение", styles.CELL_ALIGN_HLEFT_BOLD));

        for (DataItem dataItem : productProps) {
            titles.add(new ReportCell(dataItem.getDisplayingName(), styles.CELL_ALIGN_HLEFT_BOLD));
        }

        rowNum = 0;
        fillRow(0, titles.toArray(new ReportCell[0]));

        setColumnSize();
//        decorateTitles();
    }
}
