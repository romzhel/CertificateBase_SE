package ui_windows.product.data;

import javafx.util.Callback;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.product.Product;

import java.lang.reflect.Field;

public class DataItem {
    private int id;
    private String displayingName;
    private Field field;
    private Callback<Container, XSSFCell> excelCellValueFactory;
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

    public XSSFCell createXssfCell(Product product, XSSFRow row, int index, PriceListSheet priceListSheet) {
        return excelCellValueFactory == null ? null : excelCellValueFactory.call(new Container(product, row, index, priceListSheet));
    }

    public String getDisplayingName() {
        return displayingName;
    }

    public void setExcelCellValueFactory(Callback<Container, XSSFCell> excelCellValueFactory) {
        this.excelCellValueFactory = excelCellValueFactory;
    }

    public T getValue(Product product) {
        return valueFactory.call(product);
    }

    public class Container {
        private Product product;
        private XSSFRow row;
        int index;
        private PriceListSheet priceListSheet;

        public Container(Product product, XSSFRow row, int index, PriceListSheet priceListSheet) {
            this.product = product;
            this.row = row;
            this.index = index;
            this.priceListSheet = priceListSheet;
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

        public PriceListSheet getPriceListSheet() {
            return priceListSheet;
        }

        public void setPriceListSheet(PriceListSheet priceListSheet) {
            this.priceListSheet = priceListSheet;
        }
    }
}
