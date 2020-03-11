package ui_windows.main_window.filter_window_se;

public enum CustomValueCondition {
    START_WITH {
        boolean matches(String text, String pattern) {
            return text.startsWith(pattern);
        }
    },
    END_WITH {
        boolean matches(String text, String pattern) {
            return text.endsWith(pattern);
        }
    },
    CONTAINS{
        boolean matches(String text, String pattern) {
            return text.contains(pattern);
        }
    },
    NOT_CONTAINS{
        boolean matches(String text, String pattern) {
            return !text.contains(pattern);
        }
    },
    REGULAR_EXPRESSION{
        boolean matches(String text, String pattern) {
            return text.matches(pattern);
        }
    },
    EQUALS{
        boolean matches(String text, String pattern) {
            return text.equals(pattern);
        }
    };

    abstract boolean matches(String text, String pattern);
}
