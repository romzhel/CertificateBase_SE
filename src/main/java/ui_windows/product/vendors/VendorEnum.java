package ui_windows.product.vendors;

import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public enum VendorEnum {
    SIEMENS(0),
    VANDERBILT(1);

    private int id;

    VendorEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    private static final Map<Integer, VendorEnum> idMap = new HashMap();

    static {
        for (VendorEnum vendor : VendorEnum.values())
            idMap.put(vendor.id, vendor);
    }

    public static VendorEnum getById(int id) {
        return idMap.get(id);
    }

    public static VendorEnum recognizeVendor(String vendorRaw) {
        try {
            if (vendorRaw.matches("^\\d+$")) {
                return VendorEnum.getById(Integer.parseInt(vendorRaw));
            } else {
                return VendorEnum.valueOf(vendorRaw.toUpperCase());
            }
        } catch (Exception e) {
            log.error("can't recognize vendor from '{}'", vendorRaw);
        }

        return SIEMENS;
    }
}
