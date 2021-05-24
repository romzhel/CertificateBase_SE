package utils.property_change_protect;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class ProductProtectChange {
    private String id;
    private List<PropertyProtectChange> propertyProtectChangeList = new LinkedList<>();
}
