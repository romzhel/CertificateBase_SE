package ui_windows.options_window.price_lists_editor.se;

import org.apache.poi.xssf.usermodel.XSSFCell;
import ui_windows.main_window.file_import_window.ObjectsComparator2;
import ui_windows.product.Product;
import utils.comparation.ObjectsComparator;

import java.lang.reflect.Field;

public class PriceListColumn {
    private int id;
    private String displayingName;
    private Field field;
    private XSSFCell xssfCell;

    public PriceListColumn(String name, String fieldName) {
        this.displayingName = name;

        try {
            field = Product.class.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /*public XSSFCell getXssfCell(Product product) {
        Object value = new ObjectsComparator().getProperty(product, field);
    }*/


}
