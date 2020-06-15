package ui_windows.options_window.profile_editor;

import database.ProfilesDB;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Profiles {
    private static Profiles instance;
    private List<Profile> profiles;
    private ProfilesTable table;

    private Profiles() {
    }

    public static Profiles getInstance() {
        if (instance == null) {
            instance = new Profiles();
        }
        return instance;
    }

    public Profiles getFromDB() {
        profiles = new ProfilesDB().getData();
        return this;
    }

    public void addItem(Profile prof) {
        if (new ProfilesDB().putData(prof)) {
            prof.setNewItem(false);
            profiles.add(prof);
        }
    }

    public void removeItem(Profile prof) {
        if (new ProfilesDB().deleteData(prof)) {
            profiles.remove(prof);
            table.getTableView().getItems().remove(prof);
            table.getTableView().refresh();
        }
    }

    public boolean hasDuplicateName(Profile profile) {
        for (Profile prof : profiles) {
            if (prof.getName().equals(profile.getName()) && prof.getId() != profile.getId()) return true;
        }
        return false;
    }

    public Profile getProfileById(int id) {
        for (Profile prof : profiles) {
            if (prof.getId() == id) return prof;
        }
        return new Profile();
    }

    public Profile getProfileByName(String name) {
        for (Profile prof : profiles) {
            if (prof.getName().equals(name)) return prof;
        }
        return new Profile();
    }

    public ArrayList<String> getProfilesName() {
        TreeSet<String> result = new TreeSet<>();
        for (Profile prof : profiles) {
            result.add(prof.getName());
        }
        return new ArrayList<>(result);
    }

    public List<Profile> getItems() {
        return profiles;
    }

    public void setItems(ArrayList<Profile> profiles) {
        this.profiles = profiles;
    }

    public ProfilesTable getTable() {
        return table;
    }

    public void setTable(ProfilesTable table) {
        this.table = table;
    }
}
