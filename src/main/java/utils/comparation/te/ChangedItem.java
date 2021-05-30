package utils.comparation.te;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
public class ChangedItem {
    private String id;
    private List<ChangedProperty> changedPropertyList = new LinkedList<>();
    private List<ChangedProperty> protectedField = new LinkedList<>();

    public ChangedItem(String id) {
        this.id = id;
    }
}


