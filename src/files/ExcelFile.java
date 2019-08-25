package files;

import core.Dialogs;
import utils.ColumnsMapper;
import utils.RowData;
import org.apache.poi.ss.usermodel.*;
import ui_windows.product.Product;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ExcelFile {
    private static DataSize dataSize = new DataSize(0, 0);
    private static FileInputStream inputStream;
    private static FileOutputStream outputStream;
    private static File file = null;
    private static boolean hasError = true;
    private static Workbook book = null;

    public ExcelFile() {
    }

    public static boolean open(File fileForOpening) {
        if (fileForOpening != null && fileForOpening.exists()) {
            file = fileForOpening;
        } else {
            file = null;
            return false;
        }

        try {
            if (book != null) book.close();
        } catch (Exception e) {
            System.out.println("error of trying to close ExcelTable book, may be file is using (" + file.getAbsolutePath() + ")");
        }

        try {
            inputStream = new FileInputStream(file);
            book = WorkbookFactory.create(inputStream);
            hasError = false;
            System.out.println("count of ExcelTable file sheets: " + book.getNumberOfSheets());

            inputStream.close();
        } catch (Exception e) {
            System.out.println("error creating ExcelTable book, may be file is using (" + file.getAbsolutePath() + ")");
            Dialogs.showMessage("Ошибка открытия файла", "Не удалось открыть файл:\n" +
                    file.getAbsolutePath() + ".\nВозможно он открыт в другой программе.");

            hasError = true;
        }

        return !hasError;
    }

    public static DataSize getSheetDataSize(int sheetIndex) {
        System.out.println("trying to read from " + getFileName() + " sheet with index " + sheetIndex);

        Sheet sheet = book.getSheetAt(sheetIndex);
        Row row;

        int colWithData = 0;
        int rowWithData = 0;
        int rowsFromFile = sheet.getLastRowNum();

        for (int rowIndex = 0; rowIndex <= rowsFromFile + 1; rowIndex++) {
            row = sheet.getRow(rowIndex);

            if (row != null) {
                rowWithData++;
                int currColsWithData = row.getLastCellNum();
                colWithData = colWithData > currColsWithData ? colWithData : currColsWithData;
            }
        }

        dataSize.resizeData(colWithData + 1, rowWithData);
        System.out.println("размер данных " + dataSize.getCols() + " x " + dataSize.getRows());

        return dataSize;
    }

    public static ArrayList<Product> getProductDataFromSheet(int sheetIndex, DataSize dataSize) {
        ArrayList<Product> products = new ArrayList<>();
        Row row;

        int emptyRows = 0;
        int curRow = 0;
        ColumnsMapper mapper = null;
        RowData titles = null;

        do {                            //determining titles row
            row = book.getSheetAt(sheetIndex).getRow(curRow++);

            if (row != null) {
                titles = new RowData(row, dataSize.getCols());//get titles in Excel file
                mapper = new ColumnsMapper(titles);//get mapping using title line
            }

        } while ((row == null || !mapper.isTitleRow()) && dataSize.getRows() > curRow);

        System.out.println("titles " + Arrays.toString(titles.getAll()));
        System.out.println("columns mapping: " + Arrays.toString(mapper.getColsIndexes()));

        for (int rowIndex = curRow; rowIndex < dataSize.getRows() + emptyRows; rowIndex++) {
            row = book.getSheetAt(sheetIndex).getRow(rowIndex);

            if (row == null) {
                emptyRows++;
                continue;
            }

            if (mapper.isRowHasData(new RowData(row, dataSize.getCols()))) products.add(new Product(mapper));
//            else System.out.println("no data in " + row.getCell(5).getStringCellValue());

        }
        System.out.println("excelTableValues rows: " + dataSize.getRows());
        System.out.println("product items " + products.size());

        return products;
    }

    public static ArrayList<Product> getProductDataFromAllSheets() {
        ArrayList<Product> result = new ArrayList<>();
        String[] sheetNames = new String[]{"orderstat", "product information", "ru", "access", "intrusion", "video",
                "gpl siemens branded eur", "maretial_description_en", "LLP FY20"};

        for (int i = 0; i < book.getNumberOfSheets(); i++) {
            for (String sheetName : sheetNames) {

                if (book.getSheetName(i).toLowerCase().matches("^" + sheetName.toLowerCase() + ".*$")) {
                    System.out.println("import from sheet: " + sheetName);
                    result.addAll(getProductDataFromSheet(i));
                    System.out.println("resulter = " + result.size());

                    break;
                }
            }
        }

        close();

        return result;
    }

    public static ArrayList<Product> getProductDataFromSheet(int sheetIndex) {
        return getProductDataFromSheet(sheetIndex, getSheetDataSize(sheetIndex));
    }

    public static void close() {
        try {
            book.close();
            System.out.println("excel workbook is closed");
        } catch (IOException e) {
            System.out.println("error closing ExcelTable book");
        } catch (Exception ee) {
            System.out.println("try to close empty Excel workbook");
        }

        file = null;


        System.out.println("excel data is cleared");
    }

//    public static int findFirstEmptyCell(int columnIndex, int initialRow) {
//        Sheet sheet = book.getSheetAt(0);
//        Row row;
//        int emptyRows = 0;
//        int lastUsedRow = -1;
//        int currentRow = 0;
//
//        for (currentRow = initialRow; currentRow < dataSize.getRows() && emptyRows < 50; currentRow++) {
//            row = sheet.getRow(currentRow);
//
//            try {
//                Cell cell = row.getCell(columnIndex);
//
////                System.out.println("row " + currentRow + ", value = " + cell.getStringCellValue());
//
//                if (cell.getStringCellValue().trim().length() != 0) lastUsedRow = currentRow;
//            } catch (Exception e) {
//                emptyRows++;
//            }
//        }
//
//        System.out.println("finding empty and used cells, empty: " + emptyRows + ", current: " + currentRow + ", last " +
//                "used: " + lastUsedRow);
//
//        return lastUsedRow;
//    }

//    public static File exportToFile(ArrayList<RequestResult> items) {
//        HSSFWorkbook workbook = new HSSFWorkbook();
//        HSSFSheet sheet = workbook.createSheet("Сертификаты");
//        String[] titles = new String[]{"№ п/п", "Заказной номер", "Наименование", "Описание"};
//
//        double totalCost = 0.0;
//        int rowNum = 0;
//        Cell cell;
//        Row row;
//        HSSFCellStyle cellStyle = workbook.createCellStyle();
//        cellStyle.setAlignment(HorizontalAlignment.CENTER);
//        int addColumns = 0;
//
//        //fill data
//        for (RequestResult rr : items) {
//            rowNum++;
//            row = sheet.createRow(rowNum);
//
//            cell = row.createCell(0, CellType.STRING);
//            cell.setCellStyle(cellStyle);
//            cell.setCellValue(rowNum);
//
//            cell = row.createCell(1, CellType.STRING);
//            cell.setCellStyle(cellStyle);
//            cell.setCellValue(rr.getProduct().getMaterial());
//
//            cell = row.createCell(2, CellType.STRING);
//            cell.setCellStyle(cellStyle);
//            cell.setCellValue(rr.getProduct().getArticle());
//
//            cell = row.createCell(3, CellType.STRING);
//            cell.setCellValue(rr.getProduct().getDescriptionru());
//
//
//            HSSFCellStyle style = workbook.createCellStyle();
//            HSSFFont font = workbook.createFont();
//            font.setUnderline(HSSFFont.U_SINGLE);
//            font.setColor(HSSFColor.BLUE.index);
//            style.setFont(font);
//
//            for (int i = 0; i < rr.getFiles().size(); i++) {
//                cell = row.createCell(4 + i, CellType.STRING);
//                cell.setCellValue(rr.getFiles().get(i).getName());
//                final Hyperlink link = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
//                link.setAddress(rr.getFiles().get(i).getName());
//                cell.setHyperlink(link);
//                cell.setCellStyle(style);
//
//                addColumns = addColumns < rr.getFiles().size() ? rr.getFiles().size() : addColumns;
//            }
//        }
//
//        //title row
//        row = sheet.createRow(0);
//        for (int col = 0; col < titles.length + addColumns; col++) {
//            cell = row.createCell(col, CellType.STRING);
//            cell.setCellStyle(cellStyle);
//            if (col < titles.length) cell.setCellValue(titles[col]);
//            else cell.setCellValue("Файл сертификата " + (col - 3));
//            sheet.autoSizeColumn(col);
//        }
//
//        //output to file
//        if (!CoreModule.getFolders().getTempFolder().exists()) {
//            if (!CoreModule.getFolders().getTempFolder().mkdir()) {
//
//                Dialogs.showMessage("Создание временной папки", "Не удалось создать временную папку\n" +
//                        CoreModule.getFolders().getTempFolder() + "\n\nОперация не может быть выполнена.");
//                return null;
//            }
//        }
//
//        File file = new File(CoreModule.getFolders().getTempFolder().getPath() + "\\" +
//                "Сертификаты_" + Utils.getDateTime().replaceAll(":", "-") + ".xls");
//
//        try {
//            FileOutputStream outFile = new FileOutputStream(file);
//            workbook.write(outFile);
//
//            workbook.close();
//            outFile.close();
//        } catch (Exception e) {
//            System.out.println("Something wrong....");
//        }
//
//        System.out.println("Created file: " + file.getAbsolutePath());
//
//        return file;
//    }

    public static Workbook getBook() {
        return book;
    }

    public static DataSize getDataSize() {
        return dataSize;
    }

    public static String getFileName() {
        return file == null ? "-" : file.getAbsolutePath();
    }

    public static File getFile() {
        return file;
    }

    public static FileInputStream getInputStream() {
        return inputStream;
    }

    public static FileOutputStream getOutputStream() {
        return outputStream;
    }
}
