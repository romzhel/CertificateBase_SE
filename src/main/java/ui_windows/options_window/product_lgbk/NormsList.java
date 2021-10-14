package ui_windows.options_window.product_lgbk;

import ui_windows.options_window.requirements_types_editor.RequirementType;
import ui_windows.options_window.requirements_types_editor.RequirementTypes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NormsList {
    public static final int ADD_TO_GLOBAL = 0;
    public static final int INSTEAD_GLOBAL = 1;
    private List<Integer> norms;

    public NormsList() {
        norms = new LinkedList<>();
    }

    public NormsList(List<Integer> norms) {
        this.norms = norms;
    }

    public NormsList(String normsId) {
        norms = new ArrayList<>();

        if (normsId == null || normsId.isEmpty()) return;

        String[] itemsS = normsId.split("\\,");
        for (String itemS : itemsS) {
            if (itemS.trim().matches("^\\d+$")) {
                RequirementType rt = RequirementTypes.getInstance().getRequirementByID(Integer.parseInt(itemS.trim()));

                if (rt != null) {
                    norms.add(rt.getId());
                }
            }
        }
    }

    public List<Integer> getIntegerItems() {
        return norms;
    }

    public List<String> getNorms() {
        List<String> stringItems = new ArrayList<>();
        for (int value : norms) {
            stringItems.add(String.valueOf(value));
        }
        return stringItems;
    }

    public String getStringLine() {
        if (norms == null || norms.size() == 0) return "";
        return toString();
    }

    public String getNormNamesLine(){
        return RequirementTypes.getInstance().getNormsShortNamesByIds(toString());
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
