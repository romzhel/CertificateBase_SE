package ui_windows.options_window.product_lgbk;

import core.CoreModule;
import ui_windows.options_window.requirements_types_editor.RequirementType;

import java.util.ArrayList;

public class NormsList {
    public static final int ADD_TO_GLOBAL = 0;
    public static final int INSTEAD_GLOBAL = 1;
    private ArrayList<Integer> norms;

    public NormsList(ArrayList<Integer> norms) {
        this.norms = norms;
    }

    public NormsList(String normsId) {
        norms = new ArrayList<>();

        if (normsId == null || normsId.isEmpty()) return;

        String[] itemsS = normsId.split("\\,");
        for (String itemS : itemsS) {
            if (itemS.trim().matches("^\\d+$")) {
                RequirementType rt = CoreModule.getRequirementTypes().getRequirementByID(Integer.parseInt(itemS.trim()));

                if (rt != null) {
                    norms.add(rt.getId());
                }
            }
        }
    }

    public ArrayList<Integer> getIntegerItems() {
        return norms;
    }

    public ArrayList<String> getNorms() {
        ArrayList<String> stringItems = new ArrayList<>();
        for (int value : norms) {
            stringItems.add(String.valueOf(value));
        }
        return stringItems;
    }

    public String getStringLine() {
        if (norms == null || norms.size() == 0) return "";

        String stringLine = "";
        for (int i = 0; i < norms.size() - 1; i++) {
            stringLine = stringLine.concat(String.valueOf(norms.get(i))).concat(",");
        }

        stringLine = stringLine.concat(String.valueOf(norms.get(norms.size() - 1)));

        return stringLine;
    }

    @Override
    public String toString() {
        String result = "";
        for (int normId : norms) {
            result = result.concat(String.valueOf(normId).concat(","));
        }
        return result.replaceAll("\\,$", "");
    }
}
