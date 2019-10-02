package ui_windows.options_window.price_lists_editor.se;

import org.apache.poi.ss.usermodel.Row;
import ui_windows.product.Product;

import java.util.ArrayList;

import static ui_windows.main_window.file_import_window.NamesMapping.*;

public class PriceListColumns extends ArrayList<PriceListColumn> {

    public PriceListColumns() {
        add(new PriceListColumn(DESC_ORDER_NUMBER, FIELD_ORDER_NUMBER));
        add(new PriceListColumn(DESC_ARTICLE, FIELD_ARTICLE));
        add(new PriceListColumn(DESC_DESCRIPTION_RU, FIELD_DESCRIPTION_RU));
        add(new PriceListColumn(DESC_DESCRIPTION_EN, FIELD_DESCRIPTION_EN));
        add(new PriceListColumn(DESC_LOCAL_PRICE, FIELD_LOCAL_PRICE));
        add(new PriceListColumn(DESC_LEADTIME, FIELD_LEADTIME));
        add(new PriceListColumn(DESC_MIN_ORDER, FIELD_MIN_ORDER));
        add(new PriceListColumn(DESC_LGBK, FIELD_LGBK));
        add(new PriceListColumn(DESC_WEIGHT, FIELD_WEIGHT));

    }

    public void fillRow(Product product, Row row) {
        for (PriceListColumn column : this) {

        }

    }


}
