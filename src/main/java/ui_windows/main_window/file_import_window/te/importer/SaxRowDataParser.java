package ui_windows.main_window.file_import_window.te.importer;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Log4j2
public class SaxRowDataParser {
    private final Pattern PATTERN = Pattern.compile("^(\\D+)(\\d+)");
    private Consumer<SaxRowData> rowDataConsumer;
    private List<String> buffer = new LinkedList<>();
    private String lastAdr;

    public void setSAXCellAddress(String adr) {
        int colIndex = parseColIndex(adr);
        while (buffer.size() < colIndex) {
            buffer.add(null);
        }

        if (lastAdr != null && isNextRow(adr)) {
            SaxRowData saxRowData = new SaxRowData(buffer);
            saxRowData.setRowIndex(parseRowIndex(lastAdr));
            rowDataConsumer.accept(saxRowData);

            buffer.clear();
        }

        lastAdr = adr;
    }

    private int parseRowIndex(String adr) {
        Matcher matcher = PATTERN.matcher(adr);
        if (matcher.matches()) {
            try {
                String rowIndexS = matcher.group(2);
                return Integer.parseInt(rowIndexS) - 1;
            } catch (Exception e) {
                log.error("Error matching / parsing integer value, rawValue={}, matcher.group(1)={} ", adr, matcher.group(2));
            }
        }

        return -1;
    }

    public int parseColIndex(String adr) {
        Matcher matcher = PATTERN.matcher(adr);
        int result = 0;
        if (matcher.matches()) {
            try {
                String colIndexS = matcher.group(1);
                for (int i = 0; i < colIndexS.length(); i++) {
                    result += colIndexS.charAt(i) - 65 + i * 33;
                }
                return result;
            } catch (Exception e) {
                log.error("Error matching / parsing integer value, rawValue={}, matcher.group(1)={} ", adr, matcher.group(1));
            }
        }

        return -1;
    }

    private boolean isNextRow(String adr) {
        return adr.matches("^A\\d+.*$");
    }

    public void setSAXCellValue(String value) {
        buffer.add(value.trim());
    }
}
