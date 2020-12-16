package utils.comparation.se;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ComparingParameters<T> {
    public static final boolean WITH_GONE = true;
    public static final boolean WITHOUT_GONE = false;
    private ArrayList<Field> fields;
    private ComparingRules<T> comparingRules;
    private boolean checkGoneItems;

    public ComparingParameters() {
        fields = new ArrayList<>();
    }

    public ComparingParameters(ArrayList<Field> fields, ComparingRules<T> comparingRules, boolean checkGoneItems) {
        this.fields = fields;
        this.comparingRules = comparingRules;
        this.checkGoneItems = checkGoneItems;
    }

    public ArrayList<Field> getFields() {
        return fields;
    }

    public ComparingRules<T> getComparingRules() {
        return comparingRules;
    }

    public boolean isCheckGoneItems() {
        return checkGoneItems;
    }
}
