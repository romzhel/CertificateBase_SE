package ui_windows.main_window.file_import_window.se;

import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.main_window.file_import_window.RowData;
import ui_windows.product.data.DataItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ui_windows.product.data.DataItem.*;

public class Mapper {
    private Map<String, DataItem> nameToDataItemMapping;
    private List<FileImportParameter> parameters;
    private RowData unprovedTitleRow;

    public Mapper() {
        initMapping();
    }

    private void initMapping() {
        parameters = new ArrayList<>();
        nameToDataItemMapping = Stream.of(new Object[][]{
                {"material", DATA_ORDER_NUMBER},
                {"заказной номер", DATA_ORDER_NUMBER},
                {"order number", DATA_ORDER_NUMBER},
                {"materialnummer", DATA_ORDER_NUMBER},

                {"article", DATA_ARTICLE},
                {"artikeltyp", DATA_ARTICLE},
                {"марка", DATA_ARTICLE},
                {"артикул", DATA_ARTICLE},

                {"hierarchy", DATA_HIERARCHY},
                {"prod.hier.", DATA_HIERARCHY},
                {"Product hierarchy", DATA_HIERARCHY},
                {"gbk", DATA_LGBK},
                {"lgbk", DATA_LGBK},
                {"orig", DATA_COUNTRY},
                {"Ctry of origin", DATA_COUNTRY},

                {"material descriptio", DATA_DESCRIPTION_RU},
                {"описание", DATA_DESCRIPTION_RU},
                {"description_ru", DATA_DESCRIPTION_RU},
                {"description_en", DATA_DESCRIPTION_EN},
                {"battery code", DATA_LOGISTIC_NOTES},
                {"dangerous", DATA_LOGISTIC_NOTES},
//                {"valid from", },
                {"end of service period", DATA_SERVICE_END},
                {"dchain", DATA_DCHAIN},
                {"print", DATA_ORDER_NUMBER_PRINT},
                {"Prod.nr.print", DATA_ORDER_NUMBER_PRINT},
                {"llp fy", DATA_LOCAL_PRICE},
                {"PL", DATA_LOCAL_PRICE},
                {"minimum order quan", DATA_MIN_ORDER},
                {"Мин зак", DATA_MIN_ORDER},
                {"packsize", DATA_PACKSIZE},
                {"leadtime", DATA_LEAD_TIME_EU},
                {"ВрД", DATA_LEAD_TIME_EU},
                {"вес", DATA_WEIGHT},
                {"weight", DATA_WEIGHT},
                {"Комментарий", DATA_COMMENT_PRICE},
                {"Гарант. срок, лет", DATA_WARRANTY}
        }).collect(Collectors.toMap(data -> (String) data[0], data -> (DataItem) data[1]));
    }

    public DataItem getDataItemByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return DATA_EMPTY;
        }

        if (nameToDataItemMapping.containsKey(title)) {
            return nameToDataItemMapping.get(title);
        }

        for (String key : nameToDataItemMapping.keySet()) {
            if (title.toLowerCase().startsWith(key.toLowerCase())) {
                return nameToDataItemMapping.get(key);
            }
        }

        return DATA_EMPTY;
    }

    public boolean isTitleRow(RowData rowData) {
        int matchesCount = 0;
        boolean hasOrderNumberValue = false;
        for (String tableCellValue : rowData.getAll()) {
            DataItem die = getDataItemByTitle(tableCellValue);
            if (die != DATA_EMPTY) {
                if (die == DATA_ORDER_NUMBER) {
                    hasOrderNumberValue = true;
                } else {
                    matchesCount++;
                }
            }
        }

        if (hasOrderNumberValue && matchesCount > 0) {
            createParameters(rowData);
            return true;
        }

        unprovedTitleRow = unprovedTitleRow == null ? rowData : unprovedTitleRow;
        return false;
    }

    public int getFieldIndexByDataItem(DataItem dataItem) {
        for (FileImportParameter fip : parameters) {
            if (fip.getDataItem() == dataItem) {
                return fip.getColumnIndex();
            }
        }
        return -1;
    }

    public boolean isDataRow(RowData rowData) {
        int matchesCount = 0;
        boolean hasOrderNumberValue = false;

        for (FileImportParameter fiti : parameters) {
            String cellValue = rowData.get(fiti.getColumnIndex());

            if (cellValue != null && !cellValue.trim().isEmpty()) {
                if (fiti.getDataItem() == DATA_ORDER_NUMBER) {
                    hasOrderNumberValue = true;
                } else {
                    matchesCount++;
                }
            }
        }
        return hasOrderNumberValue && matchesCount > 0;
    }

    public void createParameters(RowData rowData) {
        DataItem dataItem;
        String[] fieldsName = rowData.getAll();//получаем заголовки из файла
        String title;

        for (int colIndex = 0; colIndex < fieldsName.length; colIndex++) {
            title = fieldsName[colIndex];
            if (title != null && !title.trim().isEmpty()) {
                dataItem = getDataItemByTitle(title);

                parameters.add(new FileImportParameter(
                        title,
                        dataItem,
                        dataItem != DATA_EMPTY,
                        dataItem != DATA_EMPTY,
                        colIndex,
                        true));
            }
        }
    }

    public List<FileImportParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<FileImportParameter> parameters) {
        this.parameters = parameters;
    }

    public RowData getUnprovedTitleRow() {
        return unprovedTitleRow;
    }
}
