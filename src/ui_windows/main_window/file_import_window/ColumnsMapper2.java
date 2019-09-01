package ui_windows.main_window.file_import_window;

import javafx.collections.ObservableList;
import ui_windows.product.Product;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ui_windows.main_window.file_import_window.NamesMapping.*;

public class ColumnsMapper2 {
    private Map<String, String> titleToName;
    private Map<String, String> nameToField;
    private boolean isTitleRow;
    private boolean isDataRow;
    private ArrayList<FieldForImport> fieldForImports;

    public ColumnsMapper2() {
        initTitlesToName();
        initNamesToFields();
    }

    private void initTitlesToName() {
        titleToName = Stream.of(new String[][]{
                {"material", DESC_ORDER_NUMBER},
                {"заказной номер", DESC_ORDER_NUMBER},
                {"order number", DESC_ORDER_NUMBER},
                {"Materialnummer", DESC_ORDER_NUMBER},
                {"article", DESC_ARTICLE},
                {"Artikeltyp", DESC_ARTICLE},
                {"марка", DESC_ARTICLE},
                {"hierarchy", DESC_HIERARCHY},
                {"prod.hier.", DESC_HIERARCHY},
                {"gbk", DESC_LGBK},
                {"orig", DESC_COUNTRY},
                {"material descriptio", DESC_DESCRIPTION_RU},
                {"описание", DESC_DESCRIPTION_RU},
                {"battery code", DESC_LOGISTIC_LIMITATION},
                {"dangerous", DESC_LOGISTIC_LIMITATION},
//                {"valid from", DESC_VALID_FROM},
                {"end of service period", DESC_SERVICE_END},
                {"dchain", DESC_DCHAIN},
                {"print", DESC_ORDER_NUMBER_PRINT},
                {"llp fy", DESC_LOCAL_PRICE},
                {"minimum order quan", DESC_MIN_ORDER},
                {"packsize", DESC_PACKSIZE},
                {"leadtime", DESC_LEADTIME},
                {"вес", DESC_WEIGHT},
                {"weight", DESC_WEIGHT}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    private void initNamesToFields() {
        nameToField = Stream.of(new String[][]{
                {DESC_ORDER_NUMBER, FIELD_ORDER_NUMBER},
                {DESC_ARTICLE, FIELD_ARTICLE},
                {DESC_HIERARCHY, FIELD_HIERARCHY},
                {DESC_LGBK, FIELD_LGBK},
                {DESC_COUNTRY, FIELD_COUNTRY},
                {DESC_DESCRIPTION_RU, FIELD_DESCRIPTION_RU},
                {DESC_DESCRIPTION_EN, FIELD_DESCRIPTION_EN},
                {DESC_LOGISTIC_LIMITATION, FIELD_LOGISTIC_LIMITATION},
//                {DESC_VALID_FROM, FIELD_VALID_FROM},
                {DESC_SERVICE_END, FIELD_SERVICE_END},
                {DESC_DCHAIN, FIELD_DCHAIN},
                {DESC_ORDER_NUMBER_PRINT, FIELD_ORDER_NUMBER_PRINT},
                {DESC_LOCAL_PRICE, FIELD_LOCAL_PRICE},
                {DESC_MIN_ORDER, FIELD_MIN_ORDER},
                {DESC_PACKSIZE, FIELD_PACKSIZE},
                {DESC_LEADTIME, FIELD_LEADTIME},
                {DESC_WEIGHT, FIELD_WEIGHT}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    public boolean isRowHasTitles(RowData rowData) {
        int matchesCount = 0;
        boolean hasOrderNumberValue = false;
        for (String tableCellValue : rowData.getAll()) {
            String name = getNameByTitle(tableCellValue);
            if (getNameByTitle(tableCellValue).equals(DESC_ORDER_NUMBER)) hasOrderNumberValue = true;
            else if (!getNameByTitle(tableCellValue).isEmpty()) matchesCount++;
        }
        isTitleRow = hasOrderNumberValue && matchesCount > 0;
        isDataRow = !isTitleRow;
        return isTitleRow;
    }

    public boolean isRowHasData(RowData rowData) {
        int matchesCount = 0;
        boolean hasOrderNumberValue = false;
        for (FieldForImport fieldForImport : fieldForImports) {
            String cellValue = rowData.get(fieldForImport.getTableColIndex());

            if (cellValue != null && !cellValue.trim().isEmpty()) {
                if (fieldForImport.getFileImportTableItem().getProductField().equals(DESC_ORDER_NUMBER)) {
                    hasOrderNumberValue = true;
                } else {
                    matchesCount++;
                }
            }
        }
        isDataRow = hasOrderNumberValue && matchesCount > 0;
        isTitleRow = !isDataRow;
        return isDataRow;
    }

    public int getFieldIndexByName(String fieldName) {
        for (FieldForImport fieldForImport : fieldForImports) {
            if (fieldForImport.getFileImportTableItem().getProductField().equals(fieldName)) {
                return fieldForImport.getTableColIndex();
            }
        }

        return -1;
    }

    public String getNameByTitle(String title) {
        if (title == null || title.isEmpty()) return "";
        for (String s : titleToName.keySet()) {
            if (title.replaceAll("\\s", "").toLowerCase().matches(
                    ".*" + s.replaceAll("\\s", "").toLowerCase() + ".*")) {
                return titleToName.get(s);
            }
        }
        return "";
    }

    public String getProductFieldNameByName(String name) {
        if (name == null || name.isEmpty()) return "";
        for (String s : nameToField.keySet()) {
            if (name.toLowerCase().matches(s.toLowerCase())) return nameToField.get(s);
        }
        return "";

        /*if (nameToField.containsKey(name)) {
            return nameToField.get(name);
        }
        return "";*/
    }


    public FieldForImport[] getFieldsForImport(ObservableList<FileImportTableItem> importTableItems) {
        boolean hasMaterialField = false;
        ArrayList<FieldForImport> result = new ArrayList<>();
        for (FileImportTableItem tableItem : importTableItems) {
            if (!tableItem.getProductField().isEmpty() && tableItem.isImportValue()) {
                if (tableItem.getProductField().equals(DESC_ORDER_NUMBER)) hasMaterialField = true;
                result.add(new FieldForImport(tableItem));
            }
        }

        fieldForImports = hasMaterialField && result.size() > 1 ? result : new ArrayList<>();
        return fieldForImports.toArray(new FieldForImport[]{});
    }

    public class FieldForImport {
        private Field field;
        private int tableColIndex;
        private FileImportTableItem fileImportTableItem;

        public FieldForImport(FileImportTableItem importTableItem) {
            this.tableColIndex = importTableItem.getColumnIndex();

            String name;
            String fieldNameFromName;
            String fieldName;
            for (Field field : Product.class.getDeclaredFields()) {
                name = importTableItem.getProductField().toLowerCase();
                fieldNameFromName = getProductFieldNameByName(name).toLowerCase();
                fieldName = field.getName().toLowerCase();

                if (fieldName.equals(fieldNameFromName) || fieldName.equals(name) ) {
                    this.field = field;
                    fileImportTableItem = importTableItem;
                    return;
                }
            }
            System.out.println("Product field " + importTableItem.getProductField() + " not found!");
        }

        public Field getField() {
            return field;
        }

        public int getTableColIndex() {
            return tableColIndex;
        }

        public FileImportTableItem getFileImportTableItem() {
            return fileImportTableItem;
        }
    }
}
