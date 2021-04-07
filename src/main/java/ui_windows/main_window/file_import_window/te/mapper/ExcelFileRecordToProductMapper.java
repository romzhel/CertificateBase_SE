package ui_windows.main_window.file_import_window.te.mapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import ui_windows.main_window.file_import_window.te.ImportColumnParameter;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.product.Product;
import utils.PriceUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static ui_windows.product.data.DataItem.DATA_EMPTY;

public class ExcelFileRecordToProductMapper implements FileRecordToProductMapper<Row> {
    private static final Logger logger = LogManager.getLogger(ExcelFileRecordToProductMapper.class);

    @Override
    public Product getProductFromFileRecord(Row record, List<ImportColumnParameter> params) throws RuntimeException {
        Product product = new Product();
        product.setId(0);
        product.setPrice(false);
        product.setNormsList(new NormsList(new ArrayList<>()));

        for (ImportColumnParameter param : params) {
            if (param.getDataItem() == DATA_EMPTY) {
                continue;
            }

            Field field = null;
            String valueS = null;
            Object value = null;
            String cellValue = null;
            try {
                field = param.getDataItem().getField();
                String fieldType = field.getType().getSimpleName();

                Cell cell = record.getCell(param.getColumnIndex());
                if (cell == null || (cellValue = cell.toString().trim()).isEmpty()) {
                    continue;
                }

                if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                    if (DateUtil.isCellDateFormatted(cell))
                        valueS = new SimpleDateFormat("dd.MM.yyyy").format(cell.getDateCellValue());
                    else {
                        if (cellValue.matches("^(\\d+[\\,\\.]{1}[0]+)|(\\d\\.\\d+E\\d)$") /*|| cellValue.matches("^\\d\\.\\d+E\\d$")*/) {
                            valueS = Long.toString((long) cell.getNumericCellValue());
                        } else if (cell.toString().matches("^\\d+[\\,\\.]{1}\\d+$")) {//double value
                            valueS = Double.toString(cell.getNumericCellValue());
                        }
                    }
                } else {
                    valueS = cell.getStringCellValue().trim();
                }


                if (fieldType.equals("String")) {
                    value = valueS.replaceAll("00\\.00\\.0000", "");
                } else if (fieldType.equals("Integer")) {
                    value = (int) getDoubleFromString(valueS);
                } else if (fieldType.equals("Double")) {
                    value = getDoubleFromString(valueS);
                } else {
                    throw new RuntimeException("unsupported field type for value " + cell.toString());
                }

                field.setAccessible(true);
                field.set(product, value);
            } catch (Exception e) {
                logger.error("error set Product field '{}' with value '{} => {}'", field, cellValue, value);
                throw new RuntimeException(e);
            }
        }

        return product;
    }

    private double getDoubleFromString(String text) throws RuntimeException {
        String initialValue = text;
        if (text == null || text.isEmpty() || text.equals("По запросу")) {
            return 0.0;
        }

        if (text.matches("\\d+\\.+\\d+[.,]+\\d+")) {
            text = text.replaceFirst("\\.", "");
        }

        try {
            text = text.replaceAll("\\,", ".");
            return PriceUtils.getFromString(text);
        } catch (Exception e) {
            throw new RuntimeException("error parsing do Double from " + text + " (" + initialValue + ")");
        }
    }
}