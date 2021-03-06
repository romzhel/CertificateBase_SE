package ui_windows.options_window.requirements_types_editor;

import core.Initializable;
import database.RequirementTypesDB;
import ui.Dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class RequirementTypes implements Initializable {
    private static RequirementTypes instance;
    private List<RequirementType> requirementTypes;

    private RequirementTypes() {
    }

    public static RequirementTypes getInstance() {
        if (instance == null) {
            instance = new RequirementTypes();
        }
        return instance;
    }

    @Override
    public void init() {
        requirementTypes = new RequirementTypesDB().getData();
    }

    public List<RequirementType> getItems() {
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
        RequirementTypesTable.getInstance().getTableView().getItems().remove(requirementType);
        RequirementTypesTable.getInstance().getTableView().refresh();
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

    public ArrayList<String> getRequirementsList(String idLine) {
        if (idLine == null || idLine.isEmpty()) return null;

        String[] ids = idLine.split("\\,");
        ArrayList<String> result = new ArrayList<>();

        for (String id : ids) {
            result.add(getRequirementByID(Integer.parseInt(id)).getShortName());
        }

        return result;
    }

    public String getReqIdsLineFromShortNamesAL(ArrayList<String> list) {
        if (list == null || list.size() == 0) return "";

        String result = String.valueOf(getRequirementByShortName(list.get(0)).getId());
        for (int i = 1; i < list.size(); i++) {
            result = result.concat(",").concat(String.valueOf(getRequirementByShortName(list.get(i)).getId()));
        }

        return result;
    }

    public ArrayList<String> getAllRequirementTypesShortNames() {
        ArrayList<String> temp = new ArrayList<>();
        for (RequirementType rt : requirementTypes) {
            temp.add(rt.getShortName());
        }

        return new ArrayList(new TreeSet(temp));
    }

    public String getNormsShortNamesByIds(String ids) {
        if (ids == null || ids.trim().isEmpty()) return "n/a";

        String[] adArr = ids.split("\\,");
        String result = "";
        for (String id : adArr) {
            if (id.matches("\\d+")) {
                result = result.concat(getRequirementByID(Integer.parseInt(id.trim())).getShortName()).concat(", ");
            }
        }
        return result.replaceAll("(\\,\\s)+$", "");
    }

    public ArrayList<Integer> getReqTypesIdsALbyShortNamesEnum(String shortNamesEnum) {
        ArrayList<Integer> result = new ArrayList<>();
        if (shortNamesEnum == null || shortNamesEnum.isEmpty()) return result;

        String[] shortNames = shortNamesEnum.trim().split("\\,");

        for (String shortName : shortNames) {
            result.add(getRequirementByShortName(shortName.trim()).getId());
        }

        return result;
    }

    public ArrayList<Integer> getReqTypeIdsByShortNames(ArrayList<String> shortNames) {
        ArrayList<Integer> idsList = new ArrayList<>();
        if (shortNames == null || shortNames.size() == 0) return idsList;

        RequirementType rt;
        for (String shortName : shortNames) {
            rt = getRequirementByShortName(shortName);
            if (rt != null) {
                idsList.add(rt.getId());
            }
        }
        return idsList;
    }

    public ArrayList<String> getReqTypeShortNamesByIds(ArrayList<Integer> ids) {
        ArrayList<String> shortNameList = new ArrayList<>();
        if (ids == null || ids.size() == 0) return shortNameList;

        RequirementType rt;
        for (int id : ids) {
            rt = getRequirementByID(id);
            if (rt != null) {
                shortNameList.add(rt.getShortName());
            }
        }

        return shortNameList;
    }

    public int getExNormId() {
        for (RequirementType reqType : requirementTypes) {
            if (reqType.getShortName().toUpperCase().endsWith("EX") || reqType.getFullName().contains("взрывозащ")) {
                return reqType.getId();
            }
        }
        return -1;
    }

}
