package ui_windows.main_window.file_import_window;

import javafx.collections.ObservableList;
import ui_windows.product.data.DataItem;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ui_windows.product.data.DataItem.*;

public class ColumnsMapper {
    private Map<String, DataItem> nameToDataItemMapping;
    private ArrayList<FileImportParameter> comparingParameters;

    public ColumnsMapper() {
        initMapping();
    }

    private void initMapping() {
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
                {"gbk", DATA_LGBK},
                {"orig", DATA_COUNTRY},
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
                {"llp fy", DATA_LOCAL_PRICE},
                {"minimum order quan", DATA_MIN_ORDER},
                {"packsize", DATA_PACKSIZE},
                {"leadtime", DATA_LEAD_TIME_EU},
                {"вес", DATA_WEIGHT},
                {"weight", DATA_WEIGHT}
        }).collect(Collectors.toMap(data -> (String) data[0], data -> (DataItem) data[1]));
    }

    public DataItem getDataItemByTitle(String title) {
        if (title == null || title.trim().isEmpty()) return DATA_EMPTY;
        for (String key : nameToDataItemMapping.keySet()) {
            if (title.toLowerCase().contains(key.toLowerCase())) {
                return nameToDataItemMapping.getOrDefault(key, DATA_EMPTY);
            }
        }
        return DATA_EMPTY;
    }

    public ArrayList<FileImportParameter> getColumnsForComparing(ObservableList<FileImportParameter> foundColumns) {
        comparingParameters = new ArrayList<>();
        for (FileImportParameter fiti : foundColumns) {
            if (fiti.isImportValue()) {
                comparingParameters.add(fiti);
            }
        }
        return comparingParameters;
    }

    public ArrayList<FileImportParameter> getColumnsForComparing() {
        return comparingParameters;
    }

    public int getFieldIndexByDataItem(DataItem dataItem) {
        for (FileImportParameter fip : comparingParameters) {
            if (fip.getDataItem() == dataItem) {
                return fip.getColumnIndex();
            }
        }
        return -1;
    }
}
