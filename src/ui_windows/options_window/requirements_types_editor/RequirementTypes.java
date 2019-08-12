package ui_windows.options_window.requirements_types_editor;

import core.CoreModule;
import core.Dialogs;
import database.RequirementTypesDB;

import java.util.ArrayList;
import java.util.TreeSet;

public class RequirementTypes {
    private ArrayList<RequirementType> requirementTypes;

    public RequirementTypes() {
        requirementTypes = new RequirementTypesDB().getData();
    }

    public ArrayList<RequirementType> getItems() {
        return requirementTypes;
    }

    public boolean hasDoubles(RequirementType requirementType) {
        for (RequirementType ct : requirementTypes) {
            if (ct.getId() != requirementType.getId())
                if (ct.getShortName().equals(requirementType.getShortName()) ||
                        ct.getFullName().equals(requirementType.getFullName())) {
                    Dialogs.showMessage("Повторяющееся значения", "Регламент с таким именем уже существует");
                    return true;
                }
        }
        return false;
    }

    public void remove(RequirementType requirementType) {
        requirementTypes.remove(requirementType);
        CoreModule.getRequirementTypesTable().getTableView().getItems().remove(requirementType);
        CoreModule.getRequirementTypesTable().getTableView().refresh();
    }

    public RequirementType getRequirementByID(int id) {
        for (RequirementType ct : requirementTypes) {
            if (ct.getId() == id) return ct;
        }
        return null;
    }

    public RequirementType getRequirementByShortName(String name) {
        for (RequirementType ct : requirementTypes) {
            if (ct.getShortName().equals(name)) return ct;
        }
        return null;
    }

    public int getRequirementIndexByID(int id) {
        for (RequirementType ct : requirementTypes) {
            if (ct.getId() == id) {
                return requirementTypes.indexOf(ct);
            }
        }
        return -1;
    }

    public ArrayList<String> getRequirementsList(String idLine) {
        if (idLine == null) return null;

        String[] ids = idLine.split("\\,");
        ArrayList<String> result = new ArrayList<>();

        for (String id : ids) {
            result.add(getRequirementByID(Integer.parseInt(id)).getShortName());
        }

        return result;
    }

    public String getRequirementIdsLine(ArrayList<String> list) {
        if (list == null) return "";
        String result = list.size() > 0 ? String.valueOf(getRequirementByShortName(list.get(0)).getId()) : "";

        for (int i = 1; i < list.size(); i++) {
            result = result.concat(",").concat(String.valueOf(getRequirementByShortName(list.get(1)).getId()));
        }

        return result;
    }

    public ArrayList<String> getAllRequirementTypes() {
        ArrayList<String> temp = new ArrayList<>();
        for (RequirementType rt : requirementTypes) {
            temp.add(rt.getShortName());
        }

        return new ArrayList(new TreeSet(temp));
    }

    public String getNormsShortNamesByIds(String ids){
        String[] adArr = ids.split("\\,");

        String result = adArr.length > 0 ? getRequirementByID(Integer.parseInt(adArr[0])).getShortName() : "n/a";

        for (int i = 1; i < adArr.length; i++) {
            result = result.concat(", ").concat(getRequirementByID(Integer.parseInt(adArr[i])).getShortName());
        }

        return result;
    }
}
