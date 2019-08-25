package utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import java.text.SimpleDateFormat;

public class RowData {
    String[] data;

    public RowData(Row row, int cols) {
        data = new String[cols];
//        Arrays.fill(data, "");
        for (int col = 0; col < cols; col++) {//getting title line
            Cell cell = row.getCell(col);
            if (cell == null) continue;

            switch (cell.getCellTypeEnum()) {
                case STRING:
                    data[col] = cell.getStringCellValue().trim();
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell))
                        data[col] = new SimpleDateFormat("dd.MM.yyyy").format(cell.getDateCellValue());
                    else {
                        if (cell.toString().matches("^\\d+[\\,\\.]{1}[0]+$")){
                            data[col] = Long.toString((long)cell.getNumericCellValue());
                        } else if (cell.toString().matches("^\\d+[\\,\\.]{1}\\d+$")) {//double value
                            data[col] = Double.toString(cell.getNumericCellValue());
                        }
                    }
                    break;
                default:
            }
        }
    }

    public String[] getAll() {
        return data;
    }

    public String get(int colIndex) {
        return colIndex < 0 ? "" : data[colIndex] == null ? "" : data[colIndex];
    }

    public int getSize() {
        return data.length;
    }

}
