package utils;

public class DoubleProperties {

    public String merge(String... values) {
        for (String s : values) {
            if (s != null && s != "" && !s.matches("00.00.0000"))
                return s;
        }

        return "";
    }
}
