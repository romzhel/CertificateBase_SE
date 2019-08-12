package utils;

public enum Changes {
    ADD("Добавлена");

    private String name;

    Changes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
