package core;

import exceptions.OperationCancelledByUserException;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ThreadManager {
    private static final Logger logger = LogManager.getLogger(ThreadManager.class);

    public static void startNewThread(String name, Runnable task, Consumer<Throwable> exceptionAction, Runnable... finalActions) {
        Thread thread = new Thread(task, name);
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler((th, ex) -> {
            if (exceptionAction != null) {
                Arrays.stream(finalActions).forEach(Runnable::run);
                exceptionAction.accept(ex);
            }
        });
        thread.start();
    }

    public static void executeFxTaskSafe(Runnable task) throws RuntimeException {
        if (!Platform.isFxApplicationThread()) {
            CountDownLatch inputWaiting = new CountDownLatch(1);
            AtomicReference<Throwable> exception = new AtomicReference<>(null);

            Platform.runLater(() -> {
                try {
                    task.run();
                } catch (Exception e) {
                    logger.error("FX thread error: {}", e.getMessage(), e);
                    exception.set(e);
                } finally {
                    inputWaiting.countDown();
                }
            });

            waitAnswer(inputWaiting, exception);
        } else {
            task.run();
        }
    }

    public static <T> T executeFxTaskSafe(Callable<T> task) throws RuntimeException {
        if (!Platform.isFxApplicationThread()) {
            AtomicReference<T> result = new AtomicReference<>(null);
            AtomicReference<Throwable> exception = new AtomicReference<>(null);

            CountDownLatch inputWaiting = new CountDownLatch(1);

            Platform.runLater(() -> {
                try {
                    result.set(task.call());
                } catch (Throwable e) {
                    logger.error("FX thread error: {}", e.getMessage(), e);
                    exception.set(e);
                } finally {
                    inputWaiting.countDown();
                }
            });

            waitAnswer(inputWaiting, exception);

            if (result.get() == null) {
                throw new OperationCancelledByUserException();
            }

            return result.get();
        } else {
            try {
                return task.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void waitAnswer(CountDownLatch inputWaiting, AtomicReference<Throwable> exception) throws RuntimeException {
        try {
            inputWaiting.await();
        } catch (InterruptedException e) {
            logger.error("thread wait exception: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

        if (exception.get() != null) {
            throw new RuntimeException(exception.get());
        }
    }
}
