package ui_windows.product.data;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import ui_windows.options_window.families_editor.ProductFamilies;
import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;
import ui_windows.options_window.order_accessibility_editor.OrdersAccessibility;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.price_lists_editor.PriceLists;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.product.Product;
import ui_windows.product.ProductTypes;
import ui_windows.product.Products;
import ui_windows.product.certificatesChecker.CertificatesChecker;
import ui_windows.product.certificatesChecker.CheckParameters;
import utils.Countries;
import utils.PriceUtils;

import java.lang.reflect.Field;
import java.util.Map;

import static files.ExcelCellStyleFactory.*;

public enum DataItem {
    DATA_EMPTY(0, "", null) {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue("");
        }

        public Object getValue(Product product) {
            return null;
        }
    },
    DATA_ORDER_NUMBER(1, "Заказной номер", "material") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getMaterial());
            cell.setCellStyle(CELL_ALIGN_HLEFT_VCENTER);
        }

        public Object getValue(Product product) {
            return product.getMaterial();
        }
    },
    DATA_ORDER_NUMBER_PRINT(2, "Заказной номер для печати", "productForPrint") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getProductForPrint());
            cell.setCellStyle(CELL_ALIGN_HLEFT_VCENTER);
        }

        public Object getValue(Product product) {
            return product.getProductForPrint();
        }
    },
    DATA_ARTICLE(3, "Артикул", "article") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getArticle());
            cell.setCellStyle(CELL_ALIGN_HLEFT_VCENTER);
        }

        public Object getValue(Product product) {
            return product.getArticle();
        }
    },
    DATA_DESCRIPTION_RU(4, "Описание (RU)", "descriptionru") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getDescriptionru());
            cell.setCellStyle(CELL_ALIGN_HLEFT_WRAP);
        }

        public Object getValue(Product product) {
            return product.getDescriptionru();
        }
    },
    DATA_DESCRIPTION_EN(5, "Описание (EN)", "descriptionen") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getDescriptionen());
            cell.setCellStyle(CELL_ALIGN_HLEFT_WRAP);
        }

        public Object getValue(Product product) {
            return product.getDescriptionen();
        }
    },
    DATA_DESCRIPTION(6, "Описание", null) {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(Products.getInstance().getDescriptionRuEn(product));
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return Products.getInstance().getDescriptionRuEn(product);
        }
    },
    DATA_LOCAL_PRICE(7, "Локальный прайс (Без скидок)", "localPrice") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getLocalPrice());
            cell.setCellStyle(CELL_CURRENCY_FORMAT_VCENTER);
        }

        public Object getValue(Product product) {
            return PriceUtils.roundCost(product.getLocalPrice());
        }
    },
    DATA_LOCAL_PRICE_LIST(8, "Локальный прайс (В прайс-листе)", null) {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            double cost = PriceUtils.getCostFromPriceList(product, null);

            if (!product.getPriceHidden() && cost > 0) {
                cell.setCellType(CellType.NUMERIC);
                cell.setCellValue(cost);
                cell.setCellStyle(CELL_CURRENCY_FORMAT_VCENTER);
            } else {
                cell.setCellType(CellType.STRING);
                cell.setCellStyle(CELL_ALIGN_HCENTER_HCENTER);
//                cell.setCellValue(pls.getLanguage() == LANG_RU ? "По запросу" : "By request");
                cell.setCellValue("По запросу");
            }
        }

        public Object getValue(Product product) {
            return product == null ? 0.0 : PriceUtils.getCostFromPriceList(product, null);
        }
    },
    DATA_IN_WHICH_PRICE_LIST(9, "В каком прайс-листе", null) {
        private String getPriceSheetName(Product product) {
            if (product.getPrice() == null || product.getBlocked() == null || !product.getPrice() || product.getBlocked()) {
                return "";
            }

            String result = "";

            for (PriceList priceList : PriceLists.getInstance().getItems()) {
                for (PriceListSheet pls : priceList.getSheets()) {
                    if (pls.isInPrice(product)) {
                        result += result == "" ? priceList.getName() + " / " + pls.getSheetName() :
                                "; " + priceList.getName() + " / " + pls.getSheetName();
                    }
                }
            }
            return result;
        }

        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(getPriceSheetName(product));
        }

        public Object getValue(Product product) {
            return getPriceSheetName(product);
        }
    },
    DATA_LEAD_TIME_EU(10, "Время доставки (Европа)", "leadTime") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getLeadTime());
            cell.setCellStyle(CELL_ALIGN_HCENTER_HCENTER);
        }

        public Object getValue(Product product) {
            return product.getLeadTime();
        }
    },
    DATA_LEAD_TIME_RU(11, "Время доставки (Россия)", "leadTime") {//todo возможна ошибка

        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((int) getValue(product));
            cell.setCellStyle(CELL_ALIGN_HCENTER_HCENTER);
        }

        public Object getValue(Product product) {
            return Products.getInstance().getLeadTimeRu(product);
        }
    },
    DATA_MIN_ORDER(12, "Минимальный заказ", "minOrder") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getMinOrder());
            cell.setCellStyle(CELL_ALIGN_HCENTER_HCENTER);
        }

        public Object getValue(Product product) {
            return product.getMinOrder();
        }
    },
    DATA_LGBK(13, "LGBK", "lgbk") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getLgbk());
            cell.setCellStyle(CELL_ALIGN_HCENTER_HCENTER);
        }

        public Object getValue(Product product) {
            return product.getLgbk();
        }
    },
    DATA_HIERARCHY(14, "Иерархия", "hierarchy") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getHierarchy());
            cell.setCellStyle(CELL_ALIGN_HCENTER);
        }

        public Object getValue(Product product) {
            return product.getHierarchy();
        }
    },
    DATA_WEIGHT(15, "Вес", "weight") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getWeight());
            cell.setCellStyle(CELL_ALIGN_HRIGHT_VCENTER);
        }

        public Object getValue(Product product) {
            return product.getWeight();
        }
    },
    DATA_COUNTRY(16, "Страна производства", "country") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getCountry());
            cell.setCellStyle(CELL_ALIGN_HCENTER);
        }

        public Object getValue(Product product) {
            return product.getCountry();
        }
    },
    DATA_COUNTRY_WITH_COMMENTS(17, "Страна производства c расшифровкой", null) {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(Countries.getCombinedName(product.getCountry()));
            cell.setCellStyle(CELL_ALIGN_HCENTER);
        }

        public Object getValue(Product product) {
            return Countries.getCombinedName(product.getCountry());
        }
    },
    DATA_LOGISTIC_NOTES(18, "Ограничения транспортировки", "dangerous") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getDangerous());
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return product.getDangerous();
        }
    },
    DATA_SERVICE_END(19, "Окончание сервисного периода", "endofservice") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getEndofservice());
            cell.setCellStyle(CELL_ALIGN_HCENTER);
        }

        public Object getValue(Product product) {
            return product.getEndofservice();
        }
    },
    DATA_DCHAIN(20, "Код доступности", "dchain") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getDchain());
            cell.setCellStyle(CELL_ALIGN_HCENTER);
        }

        public Object getValue(Product product) {
            return product.getDchain();
        }
    },
    DATA_DCHAIN_COMMENT(21, "Код доступности - расшифровка", null) {
        private String getDchainComment(Product product) {
            OrderAccessibility oa = OrdersAccessibility.getInstance().getOrderAccessibility(product);
            if (oa != null) {
                return oa.getDescription();
            }
            return "";
        }

        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(getDchainComment(product));
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return getDchainComment(product);
        }
    },
    DATA_DCHAIN_WITH_COMMENT(22, "Код доступности c расшифровкой", null) {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(OrdersAccessibility.getInstance().getOrderAccessibility(product).toString());
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return OrdersAccessibility.getInstance().getOrderAccessibility(product).toString();
        }
    },
    DATA_PACKSIZE(23, "Размер упаковки", "packetSize") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getPacketSize());
            cell.setCellStyle(CELL_ALIGN_HCENTER);
        }

        public Object getValue(Product product) {
            return product.getPacketSize();
        }
    },
    DATA_FAMILY_ID(24, "Направление (код)", "family_id") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getFamily_id());
            cell.setCellStyle(CELL_ALIGN_HCENTER);
        }

        public Object getValue(Product product) {
            return product.getFamily_id();
        }
    },
    DATA_FAMILY_NAME(25, "Направление", null) {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(getValue(product).toString());
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return ProductFamilies.getInstance().getProductFamily(product);
        }
    },
    DATA_RESPONSIBLE(26, "Ответственный", null) {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(getValue(product).toString());
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return ProductFamilies.getInstance().getProductFamily(product).getResponsible();
        }
    },
    DATA_IS_IN_PRICE(27, "Назначена в прайс", "price") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getPrice() ? "В прайсе" : "Не в прайсе");
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return product.getPrice();
        }
    },
    DATA_COMMENT(28, "Комментарий", "comments") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getComments());
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return product.getComments();
        }
    },
    DATA_REPLACEMENT(29, "Замена", "replacement") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getReplacement());
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return product.getReplacement();
        }
    },
    DATA_TYPE(30, "Тип", "type_id") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getType_id());
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return product.getType_id();
        }
    },
    DATA_TYPE_DESCRIPTION(31, "Тип (Описание)", null) {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(ProductTypes.getInstance().getTypeById(product.getType_id()));
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return ProductTypes.getInstance().getTypeById(product.getType_id());
        }
    },
    DATA_CERTIFICATE(32, "Наличие сертификатов", null) {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(new CertificatesChecker(product, CheckParameters.getDefault()).getCheckStatusResult().getText());
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return new CertificatesChecker(product, CheckParameters.getDefault()).getCheckStatusResult().getText();
        }
    },
    DATA_NORMS_MODE(33, "Добавление/замещение норм", "normsMode") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getNormsMode() == NormsList.ADD_TO_GLOBAL ? "Добавление" : "Замещение");
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return product.getNormsMode();
        }

    },
    DATA_NORMS_LIST(34, "Список норм для продукта", "normsList") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getNormsList().getNormNamesLine());
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return product.getNormsList().getNormNamesLine();
        }
    },
    DATA_GLOBAL_NORMS_LIST(35, "Список глобальных норм для продукта", null) {
        private String getGlobalNormsDescriptions(Product product) {
            NormsList normsList = new NormsList(Products.getInstance().getGlobalNorms(product));
            return normsList.getNormNamesLine();
        }

        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(getGlobalNormsDescriptions(product));
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return getGlobalNormsDescriptions(product);
        }
    },
    DATA_MANUAL_FILE(36, "Файл описания", "fileName") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getFileName());
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return product.getFileName();
        }
    },
    DATA_NORMS_ID(37, "Список норм для продукта (коды)", null) {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getNormsList().getStringLine());
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return product.getNormsList().getIntegerItems();
        }
    },
    DATA_GLOBAL_MORMS_ID(38, "Список глобальных норм для продукта (коды)", null) {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(new NormsList(Products.getInstance().getGlobalNorms(product)).getStringLine());
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return Products.getInstance().getGlobalNorms(product);
        }
    },
    DATA_ORDER_NUMBER_PRINT_NOT_EMPTY(39, "Заказной номер SSN для печати (или заказной номер)", null) {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getProductForPrint() == null || product.getProductForPrint().isEmpty() ?
                    product.getMaterial() : product.getProductForPrint());
            cell.setCellStyle(CELL_ALIGN_HLEFT_VCENTER);
        }

        public Object getValue(Product product) {
            return product.getProductForPrint() == null || product.getProductForPrint().isEmpty() ?
                    product.getMaterial() : product.getProductForPrint();
        }
    },
    DATA_LGBK_PRICE(40, "LGBK для прайса", "lgbk") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getLgbk());
            cell.setCellStyle(CELL_ALIGN_HCENTER_HCENTER);
        }

        public Object getValue(Product product) {
            return product.getLgbk();
        }
    },
    DATA_IS_BLOCKED(41, "Блокировка", "blocked") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getBlocked() ? "Заблокирована" : "Не заблокирована");
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return product.getBlocked();
        }
    },
    DATA_IS_PRICE_HIDDEN(42, "Сокрытие стоимости в прайсе", "priceHidden") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getPriceHidden() ? "Скрыта" : "Отображена");
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return product.getPriceHidden();
        }
    },
    DATA_ARTICLE_GAMMA(43, "Артикул (SSN для GAMMA)", "article") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(getValue(product).toString());
            cell.setCellStyle(CELL_ALIGN_HLEFT);
        }

        public Object getValue(Product product) {
            return ProductFamilies.getInstance().getProductFamily(product).getName().equals("GAMMA") ?
                    product.getArticle() :
                    DATA_ORDER_NUMBER_PRINT_NOT_EMPTY.getValue(product);
        }
    },
    DATA_WARRANTY(44, "Гарантия, лет", "warranty") {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((int) getValue(product));
            cell.setCellStyle(CELL_ALIGN_HCENTER_HCENTER);
        }

        public Object getValue(Product product) {
            return product.getWarranty() == null || product.getWarranty() < 2 ? 1 : product.getWarranty();
        }
    },
    DATA_DESCRIPTION_WRAP(45, "Описание (с переносом текста)", null) {
        public void fillExcelCell(
                Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(getValue(product).toString());
            cell.setCellStyle(CELL_ALIGN_HLEFT_VCENTER_WRAP);
        }

        public Object getValue(Product product) {
            return Products.getInstance().getDescriptionRuEn(product);
        }
    },
    DATA_COMMENT_PRICE(46, "Комментарий для прайса", "commentsPrice") {
        public void fillExcelCell(Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getCommentsPrice());
            cell.setCellStyle(CELL_ALIGN_HLEFT_VCENTER_WRAP);
        }

        public Object getValue(Product product) {
            return product.getCommentsPrice();
        }
    },
    DATA_VENDOR(47, "Вендор", "vendor") {
        public void fillExcelCell(Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getVendor().name());
            cell.setCellStyle(CELL_ALIGN_HLEFT_VCENTER_WRAP);
        }

        public Object getValue(Product product) {
            return product.getVendor();
        }
    },
    DATA_HISTORY(48, "История изменений", "history") {
        public void fillExcelCell(Cell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getHistory());
            cell.setCellStyle(CELL_ALIGN_HLEFT_VCENTER_WRAP);
        }

        public Object getValue(Product product) {
            return product.getHistory();
        }
    };

    private int id;
    private String displayingName;
    private Field field;

    DataItem(int id, String displayingName, String fieldName) {
        this.id = id;
        this.displayingName = displayingName;
        field = getFieldByName(fieldName);
    }

    public static DataItem getByDisplayingName(String lookingForDisplayingName) {
        for (DataItem die : DataItem.values()) {
            if (die.getDisplayingName().equals(lookingForDisplayingName)) {
                return die;
            }
        }
        return null;
    }

    public static DataItem getDataItemById(int id) {
        for (DataItem dataItem : values()) {
            if (dataItem.id == id) {
                return dataItem;
            }
        }
        return DATA_EMPTY;
    }

    public static DataItem getDataItemByField(Field field) {
        for (DataItem dataItem : values()) {
            if (dataItem.getField() != null && dataItem.getField().equals(field)) {
                return dataItem;
            }
        }
        return DATA_EMPTY;
    }

    public int getId() {
        return id;
    }

    public String getDisplayingName() {
        return displayingName;
    }

    public Field getField() {
        return field;
    }

    public abstract void fillExcelCell(Cell cell, Product product, Map<String, Object> options);

    public abstract Object getValue(Product product);

    private Field getFieldByName(String fieldName) {
        if (fieldName != null && !fieldName.trim().isEmpty()) {
            try {
                return Product.class.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                System.out.println("field " + fieldName + " not found");
            }
        }
        return null;
    }
}
