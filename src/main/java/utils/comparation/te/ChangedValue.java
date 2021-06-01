package utils.comparation.te;

import lombok.Data;

@Data
public class ChangedValue<E> {
    private E oldValue;
    private E newValue;

    public ChangedValue(E oldValue, E newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
