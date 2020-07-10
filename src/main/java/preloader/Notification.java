package preloader;

import javafx.application.Preloader;

public class Notification implements Preloader.PreloaderNotification {
    private String details;

    @Override
    public String toString() {
        return details;
    }

    private Notification(String details) {
        this.details = details;
    }

    public static Notification build(String details) {
        return new Notification(details);
    }

    public String getDetails() {
        return details;
    }
}
