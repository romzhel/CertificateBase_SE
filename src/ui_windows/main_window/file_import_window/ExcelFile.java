package ui_windows.main_window.file_import_window;

import core.Dialogs;
import files.DataSize;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import ui_windows.product.Product;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ExcelFile {
    private File file;
    private Workbook book;
    private Sheet currentSheet;
    private RowData currentSheetTitles;
    private DataSize sheetDataSize;
    private int curRow;
    private ColumnsMapper mapper;

    public ExcelFile() {
        mapper = new ColumnsMapper();
    }

    public boolean open(File fileForOpening) {
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

        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            book = WorkbookFactory.create(inputStream);
            System.out.println("count of ExcelTable file sheets: " + book.getNumberOfSheets());
            inputStream.close();
        } catch (Exception e) {
            System.out.println("error creating ExcelTable book, may be file is using (" + file.getAbsolutePath() + ")");
            Dialogs.showMessage("Ошибка открытия файла", "Не удалось открыть файл:\n" +
                    file.getAbsolutePath() + ".\nВозможно он открыт в другой программе.");

            return false;
        }

        return true;
    }

    public ArrayList<String> getSheetNames(){
        ArrayList<String> names = new ArrayList<>();
        for (int indexSheet = 0; indexSheet < book.getNumberOfSheets(); indexSheet++) {
            names.add(book.getSheetName(indexSheet));
        }
        return names;
    }

    public RowData getCurrentSheetTitles() {
        sheetDataSize = getSheetDataSize();
        Row row;

        curRow = 0;
        RowData titles = null;
        boolean isTitleRow = false;

        do {                            //determining titles row
            row = currentSheet.getRow(curRow++);

            if (row != null) {
                titles = new RowData(row, sheetDataSize.getCols());//get titles in Excel file
                isTitleRow = mapper.isRowHasTitles(titles);//get mapping using title line
            }

        } while ((row == null || !isTitleRow) && sheetDataSize.getRows() > curRow);

        System.out.println("titles " + Arrays.toString(titles.getAll()));
//        System.out.println("columns mapping: " + Arrays.toString(mapper.getColsIndexes()));

        currentSheetTitles = titles;
        return titles;
    }

    public ArrayList<Product> getProductsFromCurrentSheet() {
        ArrayList<Product> products = new ArrayList<>();
        Row row;

        int emptyRows = 0;
        RowData data = null;

        for (int rowIndex = curRow; rowIndex < sheetDataSize.getRows() + emptyRows; rowIndex++) {
            row = currentSheet.getRow(rowIndex);

            if (row == null) {
                emptyRows++;
                continue;
            }

            data = new RowData(row, sheetDataSize.getCols());
            if (mapper.isRowHasData(data)) products.add(new Product(data, mapper));
//            else System.out.println("no data in " + row.getCell(5).getStringCellValue());

        }
        System.out.println("excelTableValues rows: " + sheetDataSize.getRows());
        System.out.println("product items: " + products.size());

        return products;
    }


    private DataSize getSheetDataSize() {
        System.out.println("trying to read from " + getFileName() + " sheet " + currentSheet.getSheetName());
        Row row;

        int colWithData = 0;
        int rowWithData = 0;
        int rowsFromFile = currentSheet.getLastRowNum();

        for (int rowIndex = 0; rowIndex <= rowsFromFile + 1; rowIndex++) {
            row = currentSheet.getRow(rowIndex);

            if (row != null) {
                rowWithData++;
                int currColsWithData = row.getLastCellNum();
                colWithData = colWithData > currColsWithData ? colWithData : currColsWithData;
            }
        }

        System.out.println("размер данных " + (colWithData + 1) + " x " + rowWithData);

        return new DataSize(colWithData + 1, rowWithData);
    }



    private String getFileName() {
        return file == null ? "-" : file.getAbsolutePath();
    }

    public void setCurrentSheet(int index) {
        currentSheet = book.getSheetAt(index);
    }

    public void close(){
        try {
            if (book != null) book.close();
        } catch (IOException e) {

        }
        file = null;
    }

    public ColumnsMapper getMapper() {
        return mapper;
    }
}
