package utils.comparation.se;

import ui_windows.product.Product;

import java.lang.reflect.Field;

public class Param<T> {
    private T object1;
    private T object2;
    private Field field;

    public Param(T object1, T object2, Field field) {
        this.object1 = object1;
        this.object2 = object2;
        this.field = field;
    }

    public T getObject1() {
        return object1;
    }

    public T getObject2() {
        return object2;
    }

    public Field getField() {
        return field;
    }

    public void setObject2 (T object2) {
        this.object2 = object2;
    }
}
