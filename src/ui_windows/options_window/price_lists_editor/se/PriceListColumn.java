package ui_windows.options_window.price_lists_editor.se;

import javafx.util.Callback;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import ui_windows.product.Product;

import java.lang.reflect.Field;

public class PriceListColumn {
    private int id;
    private String displayingName;
    private Field field;
    private Callback<Container, XSSFCell> valueFactory;


    public PriceListColumn(String name, String fieldName) {
        this.displayingName = name;

        try {
            field = Product.class.getDeclaredField(fieldName);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    public XSSFCell createXssfCell(Product product, XSSFRow row, int index) {
        /*Object value = new ObjectsComparator().getProperty(product, field);
        XSSFCell cell = null;
        if (value instanceof Number) {
            cell = row.createCell(index, CellType.NUMERIC);
            if (value instanceof Integer) {
                cell.setCellValue((Integer) value);
            } else if (value instanceof Double) {
                cell.setCellValue((Double) value);
            }
        } else if (value instanceof String) {
            cell = row.createCell(index, CellType.STRING);
            cell.setCellValue((String) value);
        } else {
            System.out.println("filling price list, unknown object type " + value.toString() + value.getClass().getName());
        }
        return cell;*/

        return valueFactory == null ? null : valueFactory.call(new Container(product, row, index));
    }

    public String getDisplayingName() {
        return displayingName;
    }

    public void setValueFactory(Callback<Container, XSSFCell> valueFactory) {
        this.valueFactory = valueFactory;
    }

    public class Container {
        private Product product;
        private XSSFRow row;
        int index;

        public Container(Product product, XSSFRow row, int index) {
            this.product = product;
            this.row = row;
            this.index = index;
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
    }
}
