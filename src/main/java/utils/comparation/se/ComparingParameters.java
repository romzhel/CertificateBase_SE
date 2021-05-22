package utils.comparation.se;

import lombok.Getter;
import lombok.ToString;
import ui_windows.main_window.file_import_window.te.ImportColumnParameter;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class ComparingParameters<T> {
    public static final boolean WITH_GONE = true;
    public static final boolean WITHOUT_GONE = false;
    private List<ImportColumnParameter> columnParameters;
    private ComparingRules<T> comparingRules;
    private boolean checkGoneItems;

    public ComparingParameters() {
        columnParameters = new ArrayList<>();
    }

    public ComparingParameters(List<ImportColumnParameter> columnParameters, ComparingRules<T> comparingRules, boolean checkGoneItems) {
        this.columnParameters = columnParameters;
        this.comparingRules = comparingRules;
        this.checkGoneItems = checkGoneItems;
    }

    public ComparingParameters(ComparingRules<T> comparingRules) {
        this.comparingRules = comparingRules;
    }
}
