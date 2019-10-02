package ui_windows.options_window.price_lists_editor.se;

import javafx.scene.control.TreeItem;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;

public class PriceListContentTableItem {
    public static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_FAMILY = 1;
    public static final int TYPE_LGBK = 2;
    private int itemType;
    private int itemId;

    private PriceListContentItem content;
    private boolean price;
    private boolean excluded;
    private int order = 100;

    public PriceListContentTableItem(PriceListContentItem content) {
        this.content = content;
    }

    public PriceListContentItem getContent() {
        return content;
    }

    public boolean isPrice() {
        return price;
    }

    public void setPrice(boolean price) {
        this.price = price;
    }

    public boolean isExcluded() {
        return excluded;
    }

    public void setExcluded(boolean excluded) {
        this.excluded = excluded;
    }
}
