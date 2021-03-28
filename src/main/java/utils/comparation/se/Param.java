package utils.comparation.se;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Field;

@Getter
@ToString
public class Param<T> {
    private T object1;
    @Setter
    private T object2;
    private Field field;

    public Param(T object1, T object2, Field field) {
        this.object1 = object1;
        this.object2 = object2;
        this.field = field;
    }
}
