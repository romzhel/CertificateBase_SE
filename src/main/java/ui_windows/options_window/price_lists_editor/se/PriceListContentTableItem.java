package ui_windows.options_window.price_lists_editor.se;

import lombok.Data;

@Data
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

    @Override
    public String toString() {
        return "PriceListContentTableItem{" +
                "itemType=" + itemType +
                ", itemId=" + itemId +
                ", content=" + content +
                ", price=" + price +
                ", excluded=" + excluded +
//                ", order=" + order +
                '}';
    }
}
