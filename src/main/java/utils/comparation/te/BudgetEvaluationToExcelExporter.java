package utils.comparation.te;

import files.reports.ReportCell;
import files.reports.ReportToExcelTemplate_v3;
import lombok.extern.log4j.Log4j2;
import ui_windows.options_window.families_editor.ProductFamilies;
import ui_windows.product.Products;
import ui_windows.product.certificatesChecker.CertificatesChecker;
import ui_windows.product.certificatesChecker.CheckStatusResult;
import ui_windows.product.data.DataItem;
import utils.PriceUtils;
import utils.Utils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ui_windows.product.certificatesChecker.CheckStatusResult.*;
import static ui_windows.product.data.DataItem.*;

@Log4j2
public class BudgetEvaluationToExcelExporter extends ReportToExcelTemplate_v3<Void> {

    public BudgetEvaluationToExcelExporter(Path reportFilePath) {
        super(null, reportFilePath);
    }

    @Override
    public void run() {
        confirmAndCheckReportFile("Выберите путь сохранения файла отчета импорта");

        DataItem[] dataList = {DATA_ORDER_NUMBER_PRINT_NOT_EMPTY, DATA_ARTICLE, DATA_DESCRIPTION, DATA_LOCAL_PRICE_BY_STATUS,
                DATA_LEAD_TIME_RU, DATA_MIN_ORDER, DATA_LGBK, DATA_WEIGHT, DATA_COMMENT_PRICE, DATA_CERTIFICATE, DATA_DCHAIN,
                DATA_FAMILY_NAME};

        log.trace("filling Budget_Eval export report sheet");
        currentSheet = workbook.createSheet(String.format("Budget_eval_%s", Utils.getDateTimeForFileName()));
        rowNum = 0;

        List<ReportCell> titleCells = new ArrayList<>();
        for (DataItem data : dataList) {
            titleCells.add(new ReportCell(data.getDisplayingName(), styles.CELL_ALIGN_HLEFT_BOLD));
        }
        fillRow(0, titleCells.toArray(new ReportCell[0]));

        int[] colWidth = new int[dataList.length];
        Arrays.fill(colWidth, 5000);
        setColumnSize(colWidth);

        decorateTitles();

        Products.getInstance().getItems().stream()
                .filter(product -> !product.getBlocked())
                .filter(product -> product.getLocalPrice() > 0.0)
                .filter(PriceUtils::isAccessibleByStatus)
                .filter(product -> ProductFamilies.getInstance().isAccessibleByFamily(product))
                .filter(product -> {
                    CheckStatusResult csr = new CertificatesChecker(product).getCheckStatusResult();
                    return csr == STATUS_OK || csr == NO_DATA || csr == NO_NORMS_DEFINED;
                })
                .forEach(product -> {
                    List<ReportCell> dataCells = new ArrayList<>();
                    for (DataItem data : dataList) {
                        dataCells.add(new ReportCell(data.getValue(product), styles.CELL_ALIGN_HLEFT_TEXT_FORMAT));
                    }
                    fillRow(0, dataCells.toArray(new ReportCell[0]));
                });

        saveToFile();
    }
}

