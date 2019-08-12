package ui_windows.options_window.profile_editor;

public enum SimpleRight {
    HIDE ("HIDE"), DISPLAY ("DISPLAY"), OWN ("OWN"), FULL ("FULL");

    private String name;

    SimpleRight(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SimpleRight getSimpleRight(String name){
       return valueOf(name);
    }
}
