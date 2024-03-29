package ui_windows.main_window.file_import_window.te;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import ui_windows.product.data.DataItem;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ui_windows.product.data.DataItem.*;

public class FileColumnMappingService {
    private static final Logger logger = LogManager.getLogger(FileColumnMappingService.class);
    private static FileColumnMappingService instance;
    private Map<String, DataItem> titleNameToDataItemMapping;

    private FileColumnMappingService() {
    }

    public static FileColumnMappingService getInstance() {
        if (instance == null) {
            instance = new FileColumnMappingService();
            instance.init();
        }
        return instance;
    }

    private void init() {
        titleNameToDataItemMapping = Stream.of(new Object[][]{
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
                {"врд", DATA_LEAD_TIME_RU},//todo может всплыть ошибка
                {"вес", DATA_WEIGHT},
                {"weight", DATA_WEIGHT},
                {"Комментарий", DATA_COMMENT_PRICE},
                {"Гарант. срок, лет", DATA_WARRANTY}
        }).collect(Collectors.toMap(data -> ((String) data[0]).toLowerCase(), data -> (DataItem) data[1]));
    }

    public DataItem getMappingForColumnTitle(String title) {
        String lTitle = title.toLowerCase();

        if (titleNameToDataItemMapping.containsKey(lTitle)) {
            return titleNameToDataItemMapping.get(lTitle);
        }

        Set<DataItem> calcDataItems = titleNameToDataItemMapping.entrySet().stream()
                .filter(entry -> lTitle.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());

        if (calcDataItems.isEmpty()) {
            return DATA_EMPTY;
        } else if (calcDataItems.size() == 1) {
            return calcDataItems.iterator().next();
        } else {
            logger.info("Найдено сопоставление столбца '{}' в несколько свойств: {}", title, Strings.join(calcDataItems, ','));
            return calcDataItems.iterator().next();
        }
    }
}
