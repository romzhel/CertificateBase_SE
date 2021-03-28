package ui_windows.main_window.file_import_window.te.importer;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import ui_windows.main_window.file_import_window.te.ImportColumnParameter;
import ui_windows.main_window.file_import_window.te.mapper.ExcelFileRecordToProductMapper;
import ui_windows.product.Product;

import java.io.File;
import java.util.*;

public class ExcelFileImporter extends AbstractFileImporter {
    private Workbook workbook;

    @Override
    public Set<Product> getProducts(String sheetName, List<ImportColumnParameter> params) throws RuntimeException {
        ExcelFileRecordToProductMapper mapper = new ExcelFileRecordToProductMapper();
        Sheet sheet = workbook.getSheet(sheetName);
        Set<Product> result = new HashSet<>();

        Row row;
        for (int rowIndex = 1; rowIndex < sheet.getLastRowNum(); rowIndex++) {
            if ((row = sheet.getRow(rowIndex)) != null) {
                result.add(mapper.getProductFromFileRecord(row, params));
            }
        }

        return result;
    }

    @Override
    public void openFile(File file) throws RuntimeException {
        try {
            sheetSet = new HashMap<>();
            workbook = WorkbookFactory.create(file);
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                Row row = sheet.getRow(0);

                List<String> titles = new ArrayList<>();
                for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                    titles.add(row.getCell(cellIndex).getStringCellValue().trim().toLowerCase());
                }

                sheetSet.put(sheet.getSheetName(), titles);
            }
        } catch (Exception e) {
            closeFile();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeFile() {
        try {
            workbook.close();
        } catch (Exception e) {

        }
    }
}
