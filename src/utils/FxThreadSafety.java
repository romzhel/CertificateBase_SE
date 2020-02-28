package utils;

import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;

public class FxThreadSafety {

    public void run(final Runnable task) {
        if (!Thread.currentThread().getName().equals("JavaFX Application Thread")) {
            CountDownLatch inputWaiting = new CountDownLatch(1);

            Platform.runLater(() -> {
                task.run();
                inputWaiting.countDown();
            });

            try {
                inputWaiting.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            task.run();
        }
    }
}
