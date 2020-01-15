package ui_windows.product.data;

import core.CoreModule;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.product.Product;
import ui_windows.product.certificatesChecker.CertificatesChecker;
import ui_windows.product.certificatesChecker.CheckParameters;
import utils.Countries;

import java.lang.reflect.Field;
import java.util.Map;

import static files.ExcelCellStyleFactory.*;
import static ui_windows.options_window.price_lists_editor.se.PriceListContentTable.CONTENT_MODE_FAMILY;
import static ui_windows.options_window.price_lists_editor.se.PriceListContentTable.CONTENT_MODE_LGBK;
import static ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet.LANG_RU;

public enum DataItemEnum {
    DATA_ORDER_NUMBER("Заказной номер", "material") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getMaterial());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getMaterial();
        }
    },
    DATA_ORDER_NUMBER_PRINT("Заказной номер для печати", "productForPrint") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getProductForPrint());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getProductForPrint();
        }
    },
    DATA_ARTICLE("Артикул", "article") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getArticle());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getArticle();
        }
    },
    DATA_DESCRIPTION_RU("Описание (RU)", "descriptionru") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getDescriptionru());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getDescriptionru();
        }
    },
    DATA_DESCRIPTION_EN("Описание (EN)", "descriptionen") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getDescriptionen());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getDescriptionen();
        }
    },
    DATA_DESCRIPTION("Описание", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getDescriptionRuEn());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getDescriptionRuEn();
        }
    },
    DATA_LOCAL_PRICE("Локальный прайс (Без скидок)", "localPrice") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getLocalPrice());
            cell.setCellStyle(CELL_CURRENCY_FORMAT);
        }

        public Object getValue(Product product) {
            return product.getLocalPrice();
        }
    },
    DATA_LOCAL_PRICE_LIST("Локальный прайс (В прайс-листе)", null) {
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
                for (PriceList priceList : CoreModule.getPriceLists().getItems()) {
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
    DATA_IN_WHICH_PRICE_LIST("В каком прайс-листе", null) {
        private String getPriceSheetName(Product product) {
            String result = "";
            for (PriceList priceList : CoreModule.getPriceLists().getItems()) {
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
    DATA_LEAD_TIME_EU("Время доставки (Европа)", "leadTime") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getLeadTime());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return product.getLeadTime();
        }
    },
    DATA_LEAD_TIME_RU("Время доставки (Россия)", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getPreparedLeadTime());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return product.getPreparedLeadTime();
        }
    },
    DATA_MIN_ORDER("Минимальный заказ", "minOrder") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getMinOrder());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return product.getMinOrder();
        }
    },
    DATA_LGBK("LGBK", "lgbk") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getLgbk());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return product.getLgbk();
        }
    },
    DATA_HIERARCHY("Иерархия", "hierarchy") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getHierarchy());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return product.getHierarchy();
        }
    },
    DATA_WEIGHT("Вес", "weight") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getWeight());
            cell.setCellStyle(CELL_ALIGN_RIGHT);
        }

        public Object getValue(Product product) {
            return product.getWeight();
        }
    },
    DATA_COUNTRY("Страна производства", "country") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getCountry());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return product.getCountry();
        }
    },
    DATA_COUNTRY_WITH_COMMENTS("Страна производства c расшифровкой", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(Countries.getCombinedName(product.getCountry()));
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return Countries.getCombinedName(product.getCountry());
        }
    },
    DATA_LOGISTIC_NOTES("Ограничения транспортировки", "dangerous") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getDangerous());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getDangerous();
        }
    },
    DATA_SERVICE_END("Окончание сервисного периода", "endofservice") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getEndofservice());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return product.getEndofservice();
        }
    },
    DATA_DCHAIN("Код доступности", "dchain") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getDchain());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return product.getDchain();
        }
    },
    DATA_DCHAIN_COMMENT("Код доступности - расшифровка", null) {
        private String getDchainComment(Product product) {
            OrderAccessibility oa = CoreModule.getOrdersAccessibility().getOrderAccessibilityByStatusCode(product.getDchain());
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
    DATA_DCHAIN_WITH_COMMENT("Код доступности c расшифровкой", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(CoreModule.getOrdersAccessibility().getCombineOrderAccessibility(product.getDchain()));
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return CoreModule.getOrdersAccessibility().getCombineOrderAccessibility(product.getDchain());
        }
    },
    DATA_PACKSIZE("Размер упаковки", "packetSize") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getPacketSize());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }

        public Object getValue(Product product) {
            return product.getPacketSize();
        }
    },
    DATA_FAMILY("Направление", "family") {
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
    DATA_RESPONSIBLE("Ответственный", null) {
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
    DATA_IS_IN_PRICE("Включена в прайс", "price") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.isPrice() ? "В прайсе" : "Не в прайсе");
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.isPrice();
        }
    },
    DATA_COMMENT("Комментарий", "comments") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getComments());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getComments();
        }
    },
    DATA_REPLACEMENT("Замена", "replacement") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getReplacement());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }

        public Object getValue(Product product) {
            return product.getReplacement();
        }
    },
    DATA_TYPE("Тип", "type_id") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getType_id());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }
        public Object getValue(Product product) {
            return product.getType_id();
        }
    },
    DATA_TYPE_DESCRIPTION("Тип (Описание)", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(CoreModule.getProductTypes().getTypeById(product.getType_id()));
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }
        public Object getValue(Product product) {
            return CoreModule.getProductTypes().getTypeById(product.getType_id());
        }
    },
    DATA_CERTIFICATE("Наличие сертификатов", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(new CertificatesChecker(product, new CheckParameters()).getCheckStatusResult().getText());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }
        public Object getValue(Product product) {
            return new CertificatesChecker(product, new CheckParameters()).getCheckStatusResult().getText();
        }
    },
    DATA_NORMS_MODE("Добавление/замещение норм", "normsMode") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue("not released yet");
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }
        public Object getValue(Product product) {
            return product.getNormsMode();
        }

    },
    DATA_NORMS_LIST("Список норм", "normsList") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue("not released yet");
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }
        public Object getValue(Product product) {
            return product.getNormsList();
        }
    };

    private String displayingName;
    private Field field;

    DataItemEnum(String displayingName, String fieldName) {
        this.displayingName = displayingName;
        field = getFieldByName(fieldName);
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
}
