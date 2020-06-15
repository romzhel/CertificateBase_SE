package utils.comparation.se;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ComparingParameters {
    public static final boolean WITH_GONE = true;
    public static final boolean WITHOUT_GONE = false;
    private ArrayList<Field> fields;
    private ComparingRules comparingRules;
    private boolean checkGoneItems;

    public ComparingParameters() {
        fields = new ArrayList<>();
    }

    public ComparingParameters(ArrayList<Field> fields, ComparingRules comparingRules, boolean checkGoneItems) {
        this.fields = fields;
        this.comparingRules = comparingRules;
        this.checkGoneItems = checkGoneItems;
    }

    public ArrayList<Field> getFields() {
        return fields;
    }

    public ComparingRules getComparingRules() {
        return comparingRules;
    }

    public boolean isCheckGoneItems() {
        return checkGoneItems;
    }
}
