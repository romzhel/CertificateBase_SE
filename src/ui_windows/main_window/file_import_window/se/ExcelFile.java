package ui_windows.main_window.file_import_window.se;

import core.Dialogs;
import files.DataSize;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.main_window.file_import_window.RowData;
import ui_windows.product.Product;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExcelFile {
    private Workbook workbook;
    private Sheet sheet;
    private Mapper mapper;
    private File file;
    private DataSize dataSize;
    private int rowIndex;

    public ExcelFile(File file) {
        this.file = file;
    }

    public boolean open() {
        if (file != null && file.exists()) {
            FileInputStream inputStream;
            try {
                inputStream = new FileInputStream(file);
                workbook = WorkbookFactory.create(inputStream);
                System.out.println("count of ExcelTable file sheets: " + workbook.getNumberOfSheets());
                inputStream.close();
                return true;
            } catch (Exception e) {
                System.out.println("error creating ExcelTable book, may be file is using (" + file.getAbsolutePath() + ")");
                Dialogs.showMessage("Ошибка открытия файла", "Не удалось открыть файл:\n" +
                        file.getAbsolutePath() + ".\nВозможно он открыт в другой программе.");

                return false;
            }
        }
        return false;
    }

    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    public List<FileImportParameter> getImportParameters(int sheetIndex) {
        sheet = workbook.getSheetAt(sheetIndex);
        dataSize = getSheetDataSize();
        mapper = new Mapper();
        Row row;

        rowIndex = 0;
        RowData titles = null;
        boolean isTitleRowFound = false;

        do {                            //determining titles row
            row = sheet.getRow(rowIndex++);

            if (row != null) {
                titles = new RowData(row, dataSize.getCols());//get titles in Excel file
                isTitleRowFound = mapper.isTitleRow(titles);
            }

        } while (!isTitleRowFound && rowIndex < dataSize.getRows());

        if (!isTitleRowFound) {
            System.out.println("titles weren't found, cancelling...");
            return null;
        }

        System.out.println("titles " + Arrays.toString(titles.getAll()));

        return mapper.getParameters();
    }

    public ArrayList<Product> getData() {
        ArrayList<Product> products = new ArrayList<>();
        Row row;

        int emptyRows = 0;
        RowData data = null;

        for (int rowI = rowIndex; this.rowIndex < dataSize.getRows() + emptyRows; this.rowIndex++) {
            row = sheet.getRow(this.rowIndex);

            if (row == null) {
                emptyRows++;
                continue;
            }

            data = new RowData(row, dataSize.getCols());
            if (mapper.isDataRow(data)) {
                products.add(new Product(data, mapper));
            }
//            else System.out.println("no data in " + row.getCell(5).getStringCellValue());

        }
        try {
            workbook.close();
            System.out.println("workbook closed");
        } catch (IOException e) {
            System.out.println("workbook not closed");
        }
        System.out.println("excelTableValues rows: " + dataSize.getRows());
        System.out.println("product items: " + products.size());

        return products;
    }

    public ArrayList<String> getSheetsName() {
        ArrayList<String> names = new ArrayList<>();
        for (int indexSheet = 0; indexSheet < workbook.getNumberOfSheets(); indexSheet++) {
            names.add(workbook.getSheetName(indexSheet));
        }
        return names;
    }

    private DataSize getSheetDataSize() {
        System.out.println("reading " + file.getAbsolutePath() + " sheet " + sheet.getSheetName());
        Row row;

        int colWithData = 0;
        int rowWithData = 0;
        int rowsFromFile = sheet.getLastRowNum();

        for (int rowIndex = 0; rowIndex <= rowsFromFile + 1; rowIndex++) {
            row = sheet.getRow(rowIndex);

            if (row != null) {
                rowWithData++;
                int currColsWithData = row.getLastCellNum();
                colWithData = Math.max(colWithData, currColsWithData);
            }
        }

        System.out.println("размер данных " + (colWithData + 1) + " x " + rowWithData);

        return new DataSize(colWithData + 1, rowWithData);
    }

    public Mapper getMapper() {
        return mapper;
    }

    public int getSheetsCount() {
        return workbook.getNumberOfSheets();
    }

    public File getFile() {
        return file;
    }
}
