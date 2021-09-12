package utils.comparation.products;

import ui_windows.options_window.families_editor.ProductFamilies;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.product.Product;

import java.util.ArrayList;
import java.util.List;

import static ui_windows.options_window.families_editor.ProductFamilies.UNKNOWN;

public class ObjectsComparatorResult {
    private boolean isNeedUpdateInDB;
    private String historyComment;
    private String logComment;
    private List<String[]> reportLines;

    public ObjectsComparatorResult() {
        isNeedUpdateInDB = false;
        historyComment = "";
        logComment = "";
        reportLines = new ArrayList<>();
    }

    public ObjectsComparatorResult(boolean isNeedUpdateInDB, String historyComment, String logComment) {
        this.isNeedUpdateInDB = isNeedUpdateInDB;
        this.historyComment = historyComment;
        this.logComment = logComment;
    }

    public boolean isNeedUpdateInDB() {
        return isNeedUpdateInDB;
    }

    public String getHistoryComment() {
        return historyComment;
    }

    public void setNeedUpdateInDB(boolean needUpdateInDB) {
        isNeedUpdateInDB = needUpdateInDB;
    }

    public void setHistoryComment(String historyComment) {
        this.historyComment = historyComment;
    }

    public String getLogComment() {
        return logComment;
    }

    public void setLogComment(String logComment) {
        this.logComment = logComment;
    }

    public void addToReport(Product product, String... line) {
        String[] resultLine = new String[line.length + 5];
        ProductFamily pf = ProductFamilies.getInstance().getProductFamily(product);
        resultLine[0] = pf != UNKNOWN ? pf.getName() : "";
        resultLine[1] = pf != UNKNOWN ? pf.getResponsible() : "";
        resultLine[2] = product.getMaterial();
        resultLine[3] = product.getArticle();
        resultLine[4] = product.getPrice() ? "В прайсе" : "Не в прайсе";

        for (int i = 5; i < resultLine.length; i++) {
            resultLine[i] = line[i - 5];
        }

        reportLines.add(resultLine);
    }

    public List<String[]> getReportLines() {
        return reportLines;
    }
}
