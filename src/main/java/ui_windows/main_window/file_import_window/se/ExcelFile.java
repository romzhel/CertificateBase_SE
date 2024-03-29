package ui_windows.main_window.file_import_window.se;

import files.DataSize;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import ui.Dialogs;
import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.main_window.file_import_window.RowData;
import ui_windows.product.Product;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelFile {
    public static final Logger logger = LogManager.getLogger(ExcelFile.class);
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

        if (!isTitleRowFound) {//todo если не находит автоматически, то после ручного выбора не забирает данные
            logger.debug("titles row wasn't found, using first row...");
            mapper.createParameters(mapper.getUnprovedTitleRow());
        }

        logger.debug("titles: {}", titles);

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
//                products.add(new Product(data, mapper));
            }
//            else System.out.println("no data in " + row.getCell(5).getStringCellValue());

        }
        try {
            workbook.close();
            logger.trace("workbook closed");
        } catch (IOException e) {
            logger.warn("workbook wasn't closed, error: {}", e.getMessage());
        }
        logger.trace("excelTableValues rows: {}", dataSize.getRows());
        logger.trace("product items: {}", products.size());

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
        logger.trace("reading sheet '{} / {}'", file.getAbsolutePath(), sheet.getSheetName());
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

        logger.trace("размер данных: {} x {}", (colWithData + 1), rowWithData);

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
