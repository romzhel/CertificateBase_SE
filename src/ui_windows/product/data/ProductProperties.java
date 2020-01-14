package ui_windows.product.data;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ProductProperties {
    public static final String DESC_ORDER_NUMBER = "Заказной номер";
    public static final String DESC_ARTICLE = "Артикул";
    public static final String DESC_HIERARCHY = "Иерархия";
    public static final String DESC_LGBK = "LGBK";
    public static final String DESC_COUNTRY = "Страна производства";
    public static final String DESC_DESCRIPTION_RU = "Описание (RU)";
    public static final String DESC_DESCRIPTION_EN = "Описание (EN)";
    public static final String DESC_LOGISTIC_LIMITATION = "Ограничения транспортировки";
//    public static final String DESC_VALID_FROM = "Доступно с";
    public static final String DESC_SERVICE_END = "Окончание сервисного периода";
    public static final String DESC_DCHAIN = "Код доступности";
    public static final String DESC_ORDER_NUMBER_PRINT = "Заказной номер для печати";
    public static final String DESC_LOCAL_PRICE = "Локальный прайс";
    public static final String DESC_MIN_ORDER = "Минимальный заказ";
    public static final String DESC_PACKSIZE = "Упаковка";
    public static final String DESC_LEADTIME = "Время поставки";
    public static final String DESC_WEIGHT = "Вес";
    public static final String DESC_FAMILY = "Направление";
    public static final String DESC_END_OF_SERVICE = "Сервисный период";
    public static final String DESC_DCHAIN_COMMENT = "Код доступности - расшифровка";
    public static final String DESC_PRICE = "Включена в прайс";
    public static final String DESC_COMMENT = "Комментарий";
    public static final String DESC_REPLACEMENT = "Замена";
    public static final String DESC_TYPE = "Тип";
    public static final String DESC_NORMS_MODE = "Добавление/замещение норм";
    public static final String DESC_NORMS_LIST = "Список норм";

    public static final String FIELD_ORDER_NUMBER = "material";
    public static final String FIELD_ARTICLE = "article";
    public static final String FIELD_HIERARCHY = "hierarchy";
    public static final String FIELD_LGBK = "lgbk";
    public static final String FIELD_COUNTRY = "country";
    public static final String FIELD_DESCRIPTION_RU = "descriptionru";
    public static final String FIELD_DESCRIPTION_EN = "descriptionen";
    public static final String FIELD_LOGISTIC_LIMITATION = "dangerous";
    public static final String FIELD_VALID_FROM = "Доступно с";
    public static final String FIELD_SERVICE_END = "endofservice";
    public static final String FIELD_DCHAIN = "dchain";
    public static final String FIELD_ORDER_NUMBER_PRINT = "productForPrint";
    public static final String FIELD_LOCAL_PRICE = "localPrice";
    public static final String FIELD_MIN_ORDER = "minOrder";
    public static final String FIELD_PACKSIZE = "packetSize";
    public static final String FIELD_LEADTIME = "leadTime";
    public static final String FIELD_WEIGHT = "weight";
    public static final String FIELD_FAMILY = "family";
    public static final String FIELD_END_OF_SERVICE = "endofservice";
    public static final String FIELD_DCHAIN_COMMENT = "";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_COMMENT = "comments";
    public static final String FIELD_REPLACEMENT = "replacement";
    public static final String FIELD_TYPE = "type_id";
    public static final String FIELD_NORMS_MODE = "normsMode";
    public static final String FIELD_NORMS_LIST = "normsList";

    public static String[] getAllNamesRu() {
        ArrayList<String> result = new ArrayList<>();
        result.add("");

        ProductProperties instance = new ProductProperties();
        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().startsWith("DESC_")) {
                try {
                    result.add((String) field.get(instance));
                } catch (IllegalAccessException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return result.toArray(new String[]{});
    }

}
