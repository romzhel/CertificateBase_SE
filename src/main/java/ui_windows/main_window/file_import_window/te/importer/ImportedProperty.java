package ui_windows.main_window.file_import_window.te.importer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ui_windows.product.data.DataItem;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportedProperty {
    protected Object newValue;
    protected DataItem dataItem;
    protected ImportDataSheet source;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportedProperty property = (ImportedProperty) o;
        return Objects.equals(newValue, property.newValue) && dataItem == property.dataItem;
    }

    @Override
    public int hashCode() {
        return Objects.hash(newValue, dataItem, source);
    }

    @Override
    public String toString() {
        return dataItem + "=" + newValue;
    }
}
