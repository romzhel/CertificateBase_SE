package ui_windows.main_window.file_import_window.te.importer;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter
@Log4j2
public class ExcelFileSaxImporter {
    private SaxRowDataParser saxRowDataParser;
    @Setter
    private Consumer<ImportDataSheet> importDataSheetConsumer;
    private XSSFReader xssfReader;
    private StylesTable stylesTable;


    public ExcelFileSaxImporter() {
        this.saxRowDataParser = new SaxRowDataParser();
    }

    public void processFiles(List<File> files) throws Exception {
        for (File file : files) {
            try (OPCPackage pkg = OPCPackage.open(file.getPath(), PackageAccess.READ)) {
                xssfReader = new XSSFReader(pkg);
                SharedStrings sst = xssfReader.getSharedStringsTable();
                stylesTable = xssfReader.getStylesTable();

                XMLReader parser = fetchSheetParser(sst);

                XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
                while (sheets.hasNext()) {
                    try (InputStream sheet = sheets.next()) {
                        log.info("Processing sheet of {} / {}", file, sheets.getSheetName());

                        ImportDataSheet currentDataSheet = new ImportDataSheet();
                        currentDataSheet.setFileName(file.getPath());
                        currentDataSheet.setSheetName(sheets.getSheetName());
                        importDataSheetConsumer.accept(currentDataSheet);

                        InputSource sheetSource = new InputSource(sheet);
                        parser.parse(sheetSource);
                    }
                }
            }
        }
    }

    public XMLReader fetchSheetParser(SharedStrings sst) throws SAXException, ParserConfigurationException {
        XMLReader parser = XMLHelper.newXMLReader();
        ContentHandler handler = new SheetHandler(sst);
        parser.setContentHandler(handler);
        return parser;
    }

    private class SheetHandler extends DefaultHandler {
        private final SharedStrings sst;
        private final LruCache<Integer, String> lruCache = new LruCache<>(50);
        private final DataFormatter dataFormatter = new DataFormatter();
        private String lastContents;
        private boolean nextIsString;
        private boolean inlineStr;
        private int formatIndex;
        private String formatString;
        private String lastCellRef;

        private SheetHandler(SharedStrings sst) {
            this.sst = sst;
        }

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
//            log.trace("startEl = {} ({}), attr = {}", name, localName, attributes);

            // c => cell
            if (name.equals("c")) {
                // Print the cell reference
                lastCellRef = attributes.getValue("r");
                saxRowDataParser.setSAXCellAddress(lastCellRef);

                // Figure out if the value is an index in the SST
                String cellType = attributes.getValue("t");
                nextIsString = cellType != null && cellType.equals("s");
                inlineStr = cellType != null && cellType.equals("inlineStr");

                formatIndex = -1;
                formatString = null;

                String cellStyleStr = attributes.getValue("s");
                // It's a number, but almost certainly one
                //  with a special style or format

                XSSFCellStyle style = null;
                if (cellStyleStr != null) {
                    int styleIndex = Integer.parseInt(cellStyleStr);
                    style = stylesTable.getStyleAt(styleIndex);
                } else if (stylesTable.getNumCellStyles() > 0) {
                    style = stylesTable.getStyleAt(0);
                }
                if (style != null) {
                    this.formatIndex = style.getDataFormat();
                    this.formatString = style.getDataFormatString();
                    if (this.formatString == null)
                        this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
                }
            }

            // Clear contents cache
            lastContents = "";
        }

        @Override
        public void endElement(String uri, String localName, String name) throws SAXException {
//            log.trace("endEl = {} ({})", name, localName);

            // Process the last contents as required.
            // Do now, as characters() may be called more than once
            if (nextIsString && !lastContents.trim().isEmpty()) {
                Integer idx = Integer.valueOf(lastContents);
                lastContents = lruCache.get(idx);
                if (lastContents == null && !lruCache.containsKey(idx)) {
                    lastContents = sst.getItemAt(idx).getString();
                    lruCache.put(idx, lastContents);
                }
                nextIsString = false;
            }

            // v => contents of a cell
            // Output after we've seen the string contents
            if (name.equals("v") || (inlineStr && name.equals("c"))) {
                if (DateUtil.isADateFormat(formatIndex, formatString)) {
                    double numberValue = 0;
                    try {
                        numberValue = Double.parseDouble(lastContents);
                        saxRowDataParser.setSAXCellValue(dataFormatter.formatRawCellContents(numberValue, formatIndex,
                                "dd.MM.yyyy"));
                    } catch (NumberFormatException e) {
                        log.warn("Error cell '{}' value '{}' format converting to number", lastCellRef, lastContents);
                        saxRowDataParser.setSAXCellValue("");
                    }
                } else {
                    saxRowDataParser.setSAXCellValue(lastContents);
                }
            }
        }

        @Override
        public void endDocument() throws SAXException {
            saxRowDataParser.finalizeSheet();
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException { // NOSONAR
            lastContents += new String(ch, start, length);
        }

        private class LruCache<A, B> extends LinkedHashMap<A, B> {
            private final int maxEntries;

            public LruCache(final int maxEntries) {
                super(maxEntries + 1, 1.0f, true);
                this.maxEntries = maxEntries;
            }

            @Override
            protected boolean removeEldestEntry(final Map.Entry<A, B> eldest) {
                return super.size() > maxEntries;
            }
        }
    }
}
