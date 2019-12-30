package ui_windows.product.data;

import javafx.util.Callback;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.product.Product;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DataItem {
    private int id;
    private String displayingName;
    private Field field;
    private Callback<Parameters, XSSFCell> excelCellValueFactory;
    private Callback<Product, T> valueFactory;


    public DataItem(String name, String fieldName) {
        this.displayingName = name;

        if (fieldName != null && !fieldName.trim().isEmpty()) {
            try {
                field = Product.class.getDeclaredField(fieldName);

            } catch (NoSuchFieldException e) {
                System.out.println("field " + fieldName + " not found");
            }
        }
    }

    public XSSFCell createXssfCell(Product product, XSSFRow row, int index, Map<String, Object> options) {
        return excelCellValueFactory == null ? null : excelCellValueFactory.call(new Parameters(product, row, index, options));
    }

    public String getDisplayingName() {
        return displayingName;
    }

    public void setExcelCellValueFactory(Callback<Parameters, XSSFCell> excelCellValueFactory) {
        this.excelCellValueFactory = excelCellValueFactory;
    }

    public T getValue(Product product) {
        return valueFactory.call(product);
    }

    public class Parameters {
        private Product product;
        private XSSFRow row;
        int index;
//        private PriceListSheet priceListSheet;
        private Map<String, Object> options;

        public Parameters(Product product, XSSFRow row, int index, /*PriceListSheet priceListSheet,*/ Map<String, Object> options) {
            this.product = product;
            this.row = row;
            this.index = index;
//            this.priceListSheet = priceListSheet;
            this.options = options;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public XSSFRow getRow() {
            return row;
        }

        public void setRow(XSSFRow row) {
            this.row = row;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public Map<String, Object> getOptions() {
            return options;
        }
    }
}
