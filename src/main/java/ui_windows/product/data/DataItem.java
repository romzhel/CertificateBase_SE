package ui_windows.product.data;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;
import ui_windows.options_window.order_accessibility_editor.OrdersAccessibility;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.price_lists_editor.PriceLists;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.product.Product;
import ui_windows.product.ProductTypes;
import ui_windows.product.certificatesChecker.CertificatesChecker;
import ui_windows.product.certificatesChecker.CheckParameters;
import utils.Countries;

import java.lang.reflect.Field;
import java.util.Map;

import static files.ExcelCellStyleFactory.*;
import static ui_windows.options_window.price_lists_editor.se.PriceListContentTable.CONTENT_MODE_FAMILY;
import static ui_windows.options_window.price_lists_editor.se.PriceListContentTable.CONTENT_MODE_LGBK;
import static ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet.LANG_RU;

public enum DataItem {
    DATA_EMPTY(0, "", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue("");
        }

        public Object getValue(Product product) {
            return null;
        }
    },
    DATA_ORDER_NUMBER(1, "Заказной номер", "material") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getMaterial());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getMaterial();
        }
    },
    DATA_ORDER_NUMBER_PRINT(2, "Заказной номер для печати", "productForPrint") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getProductForPrint());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getProductForPrint();
        }
    },
    DATA_ARTICLE(3, "Артикул", "article") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getArticle());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getArticle();
        }
    },
    DATA_DESCRIPTION_RU(4, "Описание (RU)", "descriptionru") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getDescriptionru());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getDescriptionru();
        }
    },
    DATA_DESCRIPTION_EN(5, "Описание (EN)", "descriptionen") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getDescriptionen());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getDescriptionen();
        }
    },
    DATA_DESCRIPTION(6, "Описание", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getDescriptionRuEn());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getDescriptionRuEn();
        }
    },
    DATA_LOCAL_PRICE(7, "Локальный прайс (Без скидок)", "localPrice") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getLocalPrice());
            cell.setCellStyle(CELL_CURRENCY_FORMAT);
        }

        public Object getValue(Product product) {
            return product.getLocalPrice();
        }
    },
    DATA_LOCAL_PRICE_LIST(8, "Локальный прайс (В прайс-листе)", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);

            Object opt = null;
            if (options != null) {
                opt = options.getOrDefault("priceListSheet", null);
            }

            if (opt instanceof PriceListSheet) {
                PriceListSheet pls = (PriceListSheet) opt;

                if (product.getLocalPrice() > 0) {
                    double correction = 1D - ((double) pls.getDiscount() / 100);
                    if (correction > 0.4) {
                        cell.setCellValue(product.getLocalPrice() * correction);
                    } else {
                        System.out.println("price list sheet " + pls.getSheetName() + ", discount = " + ((int) correction * 100) + " %");
                        cell.setCellValue(product.getLocalPrice());
                    }
                    cell.setCellStyle(CELL_CURRENCY_FORMAT);
                } else {
                    cell.setCellType(CellType.STRING);
                    cell.setCellStyle(CELL_ALIGN_CENTER);
                    cell.setCellValue(pls.getLanguage() == LANG_RU ? "По запросу" : "By request");
                }
            } else {
                for (PriceList priceList : PriceLists.getInstance().getItems()) {
                    for (PriceListSheet pls : priceList.getSheets()) {
                        if (pls.getContentMode() == CONTENT_MODE_FAMILY)
                            pls.getContentTable().switchContentMode(CONTENT_MODE_LGBK);
                        if (pls.isInPrice(product)) {
                            double correction = 1D - ((double) pls.getDiscount() / 100);
                            if (correction > 0.4) {
                                cell.setCellValue(product.getLocalPrice() * correction);
                            } else {
                                System.out.println("price list sheet " + pls.getSheetName() + ", discount = " + ((int) correction * 100) + " %");
                                cell.setCellValue(product.getLocalPrice());
                            }
                            cell.setCellStyle(CELL_CURRENCY_FORMAT);
                        }
                    }
                }
            }
        }

        public Object getValue(Product product) {
            return null;
        }
    },
    DATA_IN_WHICH_PRICE_LIST(9, "В каком прайс-листе", null) {
        private String getPriceSheetName(Product product) {
            String result = "";
            for (PriceList priceList : PriceLists.getInstance().getItems()) {
                for (PriceListSheet pls : priceList.getSheets()) {
                    if (pls.isInPrice(product)) {
                        result += result == "" ? priceList.getName() + "/" + pls.getSheetName() :
                                "\n" + priceList.getName() + "/" + pls.getSheetName();
                    }
                }
            }
            return result;
        }

        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(getPriceSheetName(product));
        }

        public Object getValue(Product product) {
            return getPriceSheetName(product);
        }
    },
    DATA_LEAD_TIME_EU(10, "Время доставки (Европа)", "leadTime") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getLeadTime());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return product.getLeadTime();
        }
    },
    DATA_LEAD_TIME_RU(11, "Время доставки (Россия)", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getLeadTimeRu());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return product.getLeadTimeRu();
        }
    },
    DATA_MIN_ORDER(12, "Минимальный заказ", "minOrder") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getMinOrder());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return product.getMinOrder();
        }
    },
    DATA_LGBK(13, "LGBK", "lgbk") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getLgbk());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return product.getLgbk();
        }
    },
    DATA_HIERARCHY(14, "Иерархия", "hierarchy") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getHierarchy());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return product.getHierarchy();
        }
    },
    DATA_WEIGHT(15, "Вес", "weight") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getWeight());
            cell.setCellStyle(CELL_ALIGN_RIGHT);
        }

        public Object getValue(Product product) {
            return product.getWeight();
        }
    },
    DATA_COUNTRY(16, "Страна производства", "country") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getCountry());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return product.getCountry();
        }
    },
    DATA_COUNTRY_WITH_COMMENTS(17, "Страна производства c расшифровкой", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(Countries.getCombinedName(product.getCountry()));
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return Countries.getCombinedName(product.getCountry());
        }
    },
    DATA_LOGISTIC_NOTES(18, "Ограничения транспортировки", "dangerous") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getDangerous());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getDangerous();
        }
    },
    DATA_SERVICE_END(19, "Окончание сервисного периода", "endofservice") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getEndofservice());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return product.getEndofservice();
        }
    },
    DATA_DCHAIN(20, "Код доступности", "dchain") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getDchain());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return product.getDchain();
        }
    },
    DATA_DCHAIN_COMMENT(21, "Код доступности - расшифровка", null) {
        private String getDchainComment(Product product) {
            OrderAccessibility oa = OrdersAccessibility.getInstance().getOrderAccessibilityByStatusCode(product.getDchain());
            if (oa != null) {
                return oa.getDescription();
            }
            return "";
        }

        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(getDchainComment(product));
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return getDchainComment(product);
        }
    },
    DATA_DCHAIN_WITH_COMMENT(22, "Код доступности c расшифровкой", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(OrdersAccessibility.getInstance().getCombineOrderAccessibility(product.getDchain()));
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return OrdersAccessibility.getInstance().getCombineOrderAccessibility(product.getDchain());
        }
    },
    DATA_PACKSIZE(23, "Размер упаковки", "packetSize") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getPacketSize());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return product.getPacketSize();
        }
    },
    DATA_FAMILY_ID(24, "Направление (код)", "family_id") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getFamily_id());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return product.getFamily_id();
        }
    },
    DATA_FAMILY_NAME(25, "Направление", null) {
        private String getFamilyName(Product product) {
            ProductFamily pf = product.getProductFamily();
            if (pf != null) return pf.getName();
            return "Не известно";
        }

        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(getFamilyName(product));
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return getFamilyName(product);
        }
    },
    DATA_RESPONSIBLE(26, "Ответственный", null) {
        private String getFamilyResponsible(Product product) {
            ProductFamily pf = product.getProductFamily();
            if (pf != null) return pf.getResponsible();
            return "Не известно";
        }

        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(getFamilyResponsible(product));
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return getFamilyResponsible(product);
        }
    },
    DATA_IS_IN_PRICE(27, "Включена в прайс", "price") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.isPrice() ? "В прайсе" : "Не в прайсе");
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.isPrice();
        }
    },
    DATA_COMMENT(28, "Комментарий", "comments") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getComments());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getComments();
        }
    },
    DATA_REPLACEMENT(29, "Замена", "replacement") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getReplacement());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getReplacement();
        }
    },
    DATA_TYPE(30, "Тип", "type_id") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getType_id());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getType_id();
        }
    },
    DATA_TYPE_DESCRIPTION(31, "Тип (Описание)", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(ProductTypes.getInstance().getTypeById(product.getType_id()));
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return ProductTypes.getInstance().getTypeById(product.getType_id());
        }
    },
    DATA_CERTIFICATE(32, "Наличие сертификатов", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(new CertificatesChecker(product, CheckParameters.getDefault()).getCheckStatusResult().getText());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return new CertificatesChecker(product, CheckParameters.getDefault()).getCheckStatusResult().getText();
        }
    },
    DATA_NORMS_MODE(33, "Добавление/замещение норм", "normsMode") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getNormsMode() == NormsList.ADD_TO_GLOBAL ? "Добавление" : "Замещение");
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getNormsMode();
        }

    },
    DATA_NORMS_LIST(34, "Список норм для продукта", "normsList") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getNormsList().getNormNamesLine());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getNormsList().getNormNamesLine();
        }
    },
    DATA_GLOBAL_NORMS_LIST(35, "Список глобальных норм для продукта", null) {
        private String getGlobalNormsDescriptions(Product product) {
            NormsList normsList = new NormsList(product.getGlobalNorms());
            return normsList.getNormNamesLine();
        }

        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(getGlobalNormsDescriptions(product));
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return getGlobalNormsDescriptions(product);
        }
    },
    DATA_MANUAL_FILE(36, "Файл описания", "fileName") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getFileName());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getFileName();
        }
    },
    DATA_NORMS_ID(37, "Список норм для продукта (коды)", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getNormsList().getStringLine());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getNormsList().getIntegerItems();
        }
    },
    DATA_GLOBAL_MORMS_ID(38, "Список глобальных норм для продукта (коды)", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(new NormsList(product.getGlobalNorms()).getStringLine());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getGlobalNorms();
        }
    },
    DATA_ORDER_NUMBER_PRINT_NOT_EMPTY(39, "Заказной номер для печати (или заказной номер)", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getProductForPrint() == null || product.getProductForPrint().isEmpty() ?
                    product.getMaterial() : product.getProductForPrint());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getProductForPrint() == null || product.getProductForPrint().isEmpty() ?
                    product.getMaterial() : product.getProductForPrint();
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

    public int getId() {
        return id;
    }

    public String getDisplayingName() {
        return displayingName;
    }

    public Field getField() {
        return field;
    }

    public abstract void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options);

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
}
