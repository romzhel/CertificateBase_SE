package utils.comparation.se;

import ui_windows.main_window.file_import_window.FileImportParameter;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Adapter<T> {

    public ArrayList<Field> convert(FileImportParameter[] parameters) {
        ArrayList<Field> fields = new ArrayList<>();

        for (FileImportParameter fip : parameters) {
            if (fip.getDataItem().getField() != null) {
                fields.add(fip.getDataItem().getField());
            }
        }

        return fields;
    }
}
