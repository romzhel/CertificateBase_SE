package utils.property_change_protect;

import lombok.Data;
import ui_windows.product.data.DataItem;

@Data
public class PropertyProtectChange {
    public static short REMOVE_PROTECT = 0;
    public static short APPLY_PROTECT = 1;

    private DataItem dataItem;
    private short newState;
}
