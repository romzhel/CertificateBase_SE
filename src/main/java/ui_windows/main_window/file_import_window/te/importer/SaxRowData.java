package ui_windows.main_window.file_import_window.te.importer;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.List;

@Data
@Log4j2
public class SaxRowData {
    private String filePath;
    private String sheetName;
    private String[] data;
    private int rowIndex;

    public SaxRowData(List<String> buffer) {
        data = buffer.toArray(new String[0]);
    }

    public int getSize() {
        return data.length;
    }

    public String getCellValue(int colIndex) {
        if (colIndex < data.length) {
            return data[colIndex];
        }

        return null;
    }

    @Override
    public String toString() {
        return "{ row index='" + rowIndex + ", " + Arrays.toString(data) + '}';
    }
}
