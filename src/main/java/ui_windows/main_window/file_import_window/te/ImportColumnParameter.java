package ui_windows.main_window.file_import_window.te;

import javafx.beans.property.BooleanProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ui_windows.product.data.DataItem;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ui_windows.product.data.DataItem.DATA_EMPTY;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportColumnParameter {
    private String columnTitle;
    private DataItem dataItem = DATA_EMPTY;
    private int columnIndex = -1;
    private Map<FilesImportParametersEnum, BooleanProperty> options = new HashMap<>();

    public ImportColumnParameter(String columnTitle) {
        this.columnTitle = columnTitle;
    }

    public ImportColumnParameter(String columnTitle, DataItem dataItem, int columnIndex) {
        this.columnTitle = columnTitle;
        this.dataItem = dataItem;
        this.columnIndex = columnIndex;
    }

    @Override
    public String toString() {
        return "ImportColumnParameter{" +
                "columnTitle='" + columnTitle + '\'' +
                ", dataItem=" + dataItem +
                ", columnIndex=" + columnIndex +
                ", options=" + options.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get())) +
                '}';
    }
}
