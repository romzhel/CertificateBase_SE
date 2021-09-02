package ui_windows.product.data;

import java.util.ArrayList;
import java.util.List;

import static ui_windows.product.data.DataItem.*;

public class DataSets {

    public static DataItem[] getDataItemsForNowImport() {
        List<DataItem> result = new ArrayList<>();
        for (DataItem dataItem : DataItem.values()) {
            if (dataItem.getField() != null || dataItem == DataItem.DATA_EMPTY) {
                result.add(dataItem);
            }
        }
        result.sort((o1, o2) -> o1.getDisplayingName().compareToIgnoreCase(o2.getDisplayingName()));

        return result.toArray(new DataItem[]{});
    }

    public static DataItem[] getDataItemsForExcelExport() {
        ArrayList<DataItem> result = new ArrayList<>();
        for (DataItem dataItem : DataItem.values()) {
            if (dataItem != DataItem.DATA_EMPTY) {
                result.add(dataItem);
            }
        }
        return result.toArray(new DataItem[]{});
    }

    public static DataItem[] getDataItemsForPriceList() {
        return new DataItem[]{DATA_ORDER_NUMBER_PRINT_NOT_EMPTY, DATA_ARTICLE, DATA_DESCRIPTION, DATA_DESCRIPTION_RU,
                DATA_DESCRIPTION_EN, DATA_DESCRIPTION_WRAP, DATA_LOCAL_PRICE_LIST, DATA_LEAD_TIME_RU, DATA_MIN_ORDER,
                DATA_LGBK_PRICE, DATA_WEIGHT, DATA_WARRANTY, DATA_COMMENT_PRICE};
    }
}
