package ui_windows.main_window.file_import_window.te.importer;

import lombok.Data;
import ui_windows.product.data.DataItem;

import java.util.Objects;

@Data
public class ImportedProperty {
    private Object value;
    private DataItem dataItem;
    private ImportDataSheet source;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportedProperty property = (ImportedProperty) o;
        return Objects.equals(value, property.value) && dataItem == property.dataItem;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, dataItem, source);
    }

    @Override
    public String toString() {
        return dataItem + "=" + value;
    }
}
