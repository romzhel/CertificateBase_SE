package ui_windows.product.data;

import core.CoreModule;
import javafx.scene.control.Control;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.Product;
import ui_windows.product.certificatesChecker.CertificatesChecker;
import ui_windows.product.certificatesChecker.CheckParameters;
import utils.comparation.ObjectsComparator;

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
    },
    DATA_ORDER_NUMBER_PRINT("Заказной номер для печати", "productForPrint") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getProductForPrint());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }
    },
    DATA_ARTICLE("Артикул", "article") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getArticle());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }
    },
    DATA_DESCRIPTION_RU("Описание (RU)", "descriptionru") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getDescriptionru());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }
    },
    DATA_DESCRIPTION_EN("Описание (EN)", "descriptionen") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getDescriptionen());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }
    },
    DATA_DESCRIPTION("Описание", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getDescriptionRuEn());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }
    },
    DATA_LOCAL_PRICE("Локальный прайс (Без скидок)", "localPrice") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getLocalPrice());
            cell.setCellStyle(CELL_CURRENCY_FORMAT);
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
                        if (pls.getContentMode() == CONTENT_MODE_FAMILY) pls.getContentTable().switchContentMode(CONTENT_MODE_LGBK);
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
    },
    DATA_IN_WHICH_PRICE_LIST("В каком прайс-листе", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            String result = "";
            for (PriceList priceList : CoreModule.getPriceLists().getItems()) {
                for (PriceListSheet pls : priceList.getSheets()) {
                    if (pls.isInPrice(product)) {
                        result += result == "" ? priceList.getName() + "/" + pls.getSheetName() :
                                "\n" + priceList.getName() + "/" + pls.getSheetName();
                    }
                }
            }
            cell.setCellType(CellType.STRING);
            cell.setCellValue(result);
        }
    },
    DATA_LEAD_TIME_EU("Время доставки (Европа)", "leadTime") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getLeadTime());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }
    },
    DATA_LEAD_TIME_RU("Время доставки (Россия)", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getPreparedLeadTime());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }
    },
    DATA_MIN_ORDER("Минимальный заказ", "minOrder") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getMinOrder());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }
    },
    DATA_LGBK("LGBK", "lgbk") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getLgbk());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }
    },
    DATA_HIERARCHY("Иерархия", "hierarchy") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getHierarchy());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }
    },
    DATA_WEIGHT("Вес", "weight") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getWeight());
            cell.setCellStyle(CELL_ALIGN_RIGHT);
        }
    },
    DATA_COUNTRY("Страна производства", "country") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getCountry());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }
    },
    DATA_LOGISTIC_NOTES("Ограничения транспортировки", "dangerous") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getDangerous());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }
    },
    DATA_SERVICE_END("Окончание сервисного периода", "endofservice") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getEndofservice());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }
    },
    DATA_DCHAIN("Код доступности", "dchain") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getDchain());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }
    },
    DATA_DCHAIN_COMMENT("Код доступности - расшифровка", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue((String) getValue(product));

            OrderAccessibility oa = CoreModule.getOrdersAccessibility().getOrderAccessibilityByStatusCode(product.getDchain());
            if (oa != null) {
                String desc = oa.getDescriptionRu().isEmpty() ? oa.getDescriptionEn() : oa.getDescriptionRu();
                cell.setCellValue(desc);
                cell.setCellStyle(CELL_ALIGN_LEFT);
            }
        }
    },
    DATA_PACKSIZE("Размер упаковки", "packetSize") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getPacketSize());
            cell.setCellStyle(CELL_ALIGN_CENTER);
        }
    },
    DATA_FAMILY("Направление", "family") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            ProductFamily pf = product.getProductFamily();
            if (pf != null) {
                cell.setCellValue(pf.getName());
                cell.setCellStyle(CELL_ALIGN_LEFT);
            }
        }
    },
    DATA_RESPONSIBLE("Ответственный", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            ProductFamily pf = product.getProductFamily();
            if (pf != null) {
                cell.setCellValue(pf.getResponsible());
                cell.setCellStyle(CELL_ALIGN_LEFT);
            }
        }
    },
    DATA_IS_IN_PRICE("Включена в прайс", "price") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.isPrice() ? "В прайсе" : "Не в прайсе");
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }
    },
    DATA_COMMENT("Комментарий", "comments") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getComments());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }
    },
    DATA_REPLACEMENT("Замена", "replacement") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(product.getReplacement());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }
    },
    DATA_TYPE("Тип", "type_id") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            ProductFamily pf = null;
            if (product.getType_id() > 0) {
                pf = CoreModule.getProductFamilies().getFamilyById(product.getType_id());
            } else {
                ProductLgbk plgbk = CoreModule.getProductLgbks().getLgbkByProduct(product);
                if (plgbk != null) {
                    pf = CoreModule.getProductFamilies().getFamilyById(plgbk.getFamilyId());
                }
            }
            if (pf != null) {
                cell.setCellValue(pf.getName());
                cell.setCellStyle(CELL_ALIGN_LEFT);
            }
        }
    },
    DATA_CERTIFICATE("Наличие сертификатов", null) {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(new CertificatesChecker(product, new CheckParameters()).getCheckStatusResult().getText());
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }
    },
    DATA_NORMS_MODE("Добавление/замещение норм", "normsMode") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue("not released yet");
            cell.setCellStyle(CELL_ALIGN_LEFT);
        }
    },
    DATA_NORMS_LIST("Список норм", "normsList") {
        public void fillExcelCell(XSSFCell cell, Product product, Map<String, Object> options) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue("not released yet");
            cell.setCellStyle(CELL_ALIGN_LEFT);
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

    public Object getValue(Product product) {
        if (field == null) return null;
        return new ObjectsComparator().getProperty(product, field);
    }
}
