package files.reports;

import lombok.Data;
import org.apache.poi.ss.usermodel.CellStyle;

@Data
public class ReportCell {
    private Object value;
    private CellStyle style;
    private int combinedCellsCount;

    public ReportCell(Object value, CellStyle style) {
        this.value = value;
        this.style = style;
    }

    public ReportCell(Object value, CellStyle style, int combinedCellsCount) {
        this.value = value;
        this.style = style;
        this.combinedCellsCount = combinedCellsCount;
    }
}
