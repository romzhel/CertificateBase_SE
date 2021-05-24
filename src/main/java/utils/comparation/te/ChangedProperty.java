package utils.comparation.te;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ui_windows.main_window.file_import_window.te.importer.ImportedProperty;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ChangedProperty extends ImportedProperty {
    private Object oldValue;

    public ChangedProperty(ImportedProperty ip) {
        super(ip.getNewValue(), ip.getDataItem(), ip.getSource(), ip.getParams());
    }
}
